package com.google.code.gaeom.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Category;
import com.google.appengine.api.datastore.Email;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.IMHandle;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Link;
import com.google.appengine.api.datastore.PhoneNumber;
import com.google.appengine.api.datastore.PostalAddress;
import com.google.appengine.api.datastore.Rating;
import com.google.appengine.api.datastore.ShortBlob;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.code.gaeom.CustomEncoder;
import com.google.code.gaeom.CustomMultiEncoder;
import com.google.code.gaeom.annotation.Cached;
import com.google.code.gaeom.annotation.Child;
import com.google.code.gaeom.annotation.Embedded;
import com.google.code.gaeom.annotation.EmbeddedIn;
import com.google.code.gaeom.annotation.EncodeWith;
import com.google.code.gaeom.annotation.Id;
import com.google.code.gaeom.annotation.NoIndex;
import com.google.code.gaeom.annotation.Parent;
import com.google.code.gaeom.annotation.Serialize;
import com.google.code.gaeom.annotation.Text;
import com.google.code.gaeom.impl.encoder.ArrayEncoder;
import com.google.code.gaeom.impl.encoder.ByteArrayEncoder;
import com.google.code.gaeom.impl.encoder.CharArrayEncoder;
import com.google.code.gaeom.impl.encoder.CharEncoder;
import com.google.code.gaeom.impl.encoder.CollectionEncoder;
import com.google.code.gaeom.impl.encoder.CustomMultiPropertyEncoder;
import com.google.code.gaeom.impl.encoder.CustomSinglePropertyEncoder;
import com.google.code.gaeom.impl.encoder.DirectEncoder;
import com.google.code.gaeom.impl.encoder.EmbeddedEncoder;
import com.google.code.gaeom.impl.encoder.EmbeddedInEncoder;
import com.google.code.gaeom.impl.encoder.EnumEncoder;
import com.google.code.gaeom.impl.encoder.MapEncoder;
import com.google.code.gaeom.impl.encoder.ParentRelationshipEncoder;
import com.google.code.gaeom.impl.encoder.RelationshipEncoder;
import com.google.code.gaeom.impl.encoder.SerializableEncoder;
import com.google.code.gaeom.impl.encoder.TextEncoder;
import com.google.code.gaeom.util.FutureUtils;
import com.google.code.gaeom.util.ObjectUtils;
import com.google.code.gaeom.util.ReflectionUtils;
import com.google.code.gaeom.util.ReflectionUtils.FieldCallback;
import com.google.code.gaeom.util.Stack;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class ObjectEncoder
{
	private static final ThreadLocal<Stack.Mutable<Object>> threadStack = new ThreadLocal<Stack.Mutable<Object>>();

	private static Stack.Mutable<Object> getMutableObjectStack()
	{
		Stack.Mutable<Object> stack = threadStack.get();
		if (stack == null)
		{
			stack = Stack.Factory.create();
			threadStack.set(stack);
		}
		return stack;
	}

	public static Stack<Object> getObjectStack()
	{
		return getMutableObjectStack();
	}

	private static Set<Class<?>> directFieldTypes = Sets.newHashSet();
	static
	{
		directFieldTypes.add(boolean.class);
		directFieldTypes.add(Boolean.class);
		directFieldTypes.add(ShortBlob.class);
		directFieldTypes.add(Blob.class);
		directFieldTypes.add(Category.class);
		directFieldTypes.add(Date.class);
		directFieldTypes.add(Email.class);
		directFieldTypes.add(float.class);
		directFieldTypes.add(Float.class);
		directFieldTypes.add(double.class);
		directFieldTypes.add(Double.class);
		directFieldTypes.add(GeoPt.class);
		directFieldTypes.add(User.class);
		directFieldTypes.add(byte.class);
		directFieldTypes.add(Byte.class);
		directFieldTypes.add(char.class);
		directFieldTypes.add(Character.class);
		directFieldTypes.add(short.class);
		directFieldTypes.add(Short.class);
		directFieldTypes.add(int.class);
		directFieldTypes.add(Integer.class);
		directFieldTypes.add(long.class);
		directFieldTypes.add(Long.class);
		directFieldTypes.add(BlobKey.class);
		directFieldTypes.add(Key.class);
		directFieldTypes.add(Link.class);
		directFieldTypes.add(IMHandle.class);
		directFieldTypes.add(PostalAddress.class);
		directFieldTypes.add(Rating.class);
		directFieldTypes.add(PhoneNumber.class);
		directFieldTypes.add(String.class);
		directFieldTypes.add(com.google.appengine.api.datastore.Text.class);
	}

	private final ObjectStoreImpl os;
	private final String kind;
	private final Class<?> clazz;
	private final Map<String, FieldEntry> nameToField = Maps.newHashMap();
	private final List<FieldEntry> fields = Lists.newArrayList();
	private final boolean cacheInstances;
	private Field idField;
	private Field parentField;
	private Iterator<Key> keys;

	private static class FieldEntry
	{
		final Field field;
		final FieldEncoder encoder;

		public FieldEntry(Field field, FieldEncoder mapper)
		{
			if (mapper == null)
				throw new NullPointerException("No mapper found for field: " + field);

			this.field = field;
			field.setAccessible(true);

			this.encoder = mapper;
		}
	}

	public ObjectEncoder(ObjectStoreImpl os, Class<?> clazz)
	{
		this(os, clazz, null);
	}

	public ObjectEncoder(ObjectStoreImpl store, final Class<?> clazz, final String prefix)
	{
		this.os = store;
		this.kind = os.typeToKind(clazz);
		this.clazz = clazz;
		this.cacheInstances = clazz.getAnnotation(Cached.class) != null;
		ReflectionUtils.visitFields(clazz, new FieldCallback()
		{
			public boolean doWith(Field field)
			{
				if (!Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers()))
				{
					if (field.getAnnotation(Id.class) != null)
					{
						Class<?> fieldType = field.getType();
						if (!String.class.isAssignableFrom(fieldType) && !Long.class.isAssignableFrom(fieldType) && !long.class.isAssignableFrom(fieldType))
							throw new IllegalStateException("Cannot have @Id field of type: " + fieldType);
						if (idField != null)
							throw new IllegalStateException("Cannot have more than one @Id field per class (including superclasses).");
						idField = field;
						idField.setAccessible(true);
					}
					else
					{
						FieldEncoder encoder = getFieldEncoder(prefix == null ? field.getName() : prefix + "." + field.getName(), field, field.getGenericType());

						if (encoder != null)
						{
							FieldEntry entry = new FieldEntry(field, encoder);
							nameToField.put(field.getName(), entry);
							fields.add(entry);
							if (field.isAnnotationPresent(Parent.class))
							{
								if (parentField != null)
									throw new IllegalStateException("May not have more than one @Parent annotation in a type.");
								if (!(encoder instanceof RelationshipEncoder) && !(encoder instanceof ParentRelationshipEncoder))
									throw new IllegalStateException("@Parent must be on a to-one relationship");
								parentField = field;
								field.setAccessible(true);
							}
						}
					}
				}
				return true;
			}

			private FieldEncoder getFieldEncoder(final String propertyName, Field field, Type type)
			{
				Class<?> clazz = ReflectionUtils.getBaseClass(type);
				final boolean index = !field.isAnnotationPresent(NoIndex.class);

				if (field.getAnnotation(Serialize.class) != null)
				{
					return new SerializableEncoder(propertyName);
				}
				else if (Map.class.isAssignableFrom(clazz))
				{
					Type[] types = ((ParameterizedType) type).getActualTypeArguments();
					FieldEncoder keyEncoder = getFieldEncoder(propertyName + MapEncoder.cKeyPrefix, field, types[0]);
					FieldEncoder valueEncoder = getFieldEncoder(propertyName + MapEncoder.cValuePrefix, field, types[0]);
					return new MapEncoder(propertyName, clazz, keyEncoder, valueEncoder);
				}
				else if (Collection.class.isAssignableFrom(clazz))
				{
					Type[] types = ((ParameterizedType) type).getActualTypeArguments();
					FieldEncoder valueEncoder = getFieldEncoder(propertyName + CollectionEncoder.cContentsPrefix, field, types[0]);
					return new CollectionEncoder(propertyName, clazz, valueEncoder);
				}
				else if (clazz.isArray())
				{
					if (clazz.getComponentType() == byte.class)
					{
						return new ByteArrayEncoder(propertyName);
					}
					else if (clazz.getComponentType() == char.class)
					{
						return new CharArrayEncoder(propertyName);
					}
					else
					{
						FieldEncoder valueEncoder = getFieldEncoder(propertyName + CollectionEncoder.cContentsPrefix, field, clazz.getComponentType());
						return new ArrayEncoder(propertyName, clazz.getComponentType(), valueEncoder);
					}
				}
				else if (field.getAnnotation(EmbeddedIn.class) != null)
				{
					return new EmbeddedInEncoder(clazz);
				}
				else if (field.isAnnotationPresent(EncodeWith.class) || field.getType().isAnnotationPresent(EncodeWith.class))
				{
					Class<? extends CustomEncoder<?, ?>> encoderClass;
					if (field.isAnnotationPresent(EncodeWith.class))
						encoderClass = field.getAnnotation(EncodeWith.class).value();
					else
						encoderClass = field.getType().getAnnotation(EncodeWith.class).value();
					if (CustomMultiEncoder.class.isAssignableFrom(encoderClass))
					{
						CustomMultiEncoder<?> custom = ObjectUtils.cast(ObjectUtils.newInstance(encoderClass));
						return new CustomMultiPropertyEncoder(propertyName, custom, index);
					}
					else
					{
						CustomEncoder<?, ?> custom = ObjectUtils.newInstance(encoderClass);
						return new CustomSinglePropertyEncoder(propertyName, custom, index);
					}
				}
				else if (Enum.class.isAssignableFrom(clazz))
				{
					return new EnumEncoder(propertyName, clazz, index);
				}
				else if (field.getAnnotation(Text.class) != null)
				{
					return new TextEncoder(propertyName);
				}
				else if (char.class == clazz || Character.class == clazz)
				{
					return new CharEncoder(propertyName, index);
				}
				else if (directFieldTypes.contains(clazz))
				{
					return new DirectEncoder(propertyName, index);
				}
				else if (field.getAnnotation(Embedded.class) != null)
				{
					return new EmbeddedEncoder(propertyName, clazz, os);
				}
				else
				{
					Parent parent = field.getAnnotation(Parent.class);
					if (parent != null && parent.value() != Parent.FilterPolicy.RetainKey)
						return new ParentRelationshipEncoder(field.getName());
					else
						return new RelationshipEncoder(field.getName(), propertyName, field.isAnnotationPresent(Child.class), index);
				}
			}
		});
	}

	public void setId(Object target, Key id)
	{
		if (idField != null)
		{
			if (String.class.isAssignableFrom(idField.getType()))
				ReflectionUtils.set(target, idField, id.getName());
			else
				ReflectionUtils.set(target, idField, id.getId());
		}
	}

	public synchronized long getNextId(AsyncDatastoreService service)
	{
		if (keys == null || !keys.hasNext())
			keys = FutureUtils.safeGet(service.allocateIds(kind, 100)).iterator();
		return keys.next().getId();
	}

	public void encode(final InstanceStore os, final Object object, final PropertyStore store)
	{
		assert clazz == object.getClass();
		Stack.Mutable<Object> stack = getMutableObjectStack();
		stack.push(object);
		try
		{
			for (FieldEntry entry : fields)
				entry.encoder.encode(os, ReflectionUtils.get(object, entry.field), store);
		}
		finally
		{
			stack.pop();
		}
	}

	public void decode(final InstanceSource os, final Object object, final PropertySource source)
	{
		assert clazz == object.getClass();
		Stack.Mutable<Object> stack = getMutableObjectStack();
		stack.push(object);
		try
		{
			for (FieldEntry entry : fields)
			{
				Object value = entry.encoder.decode(os, source);
				if (value != null)
					value = ConvertUtils.convert(value, entry.field.getType());

				ReflectionUtils.set(object, entry.field, value);
			}
		}
		finally
		{
			stack.pop();
		}
	}

	public boolean shouldCache()
	{
		return cacheInstances;
	}

	public Object getId(Object object)
	{
		if (idField != null)
			return ReflectionUtils.get(object, idField);
		else
			return null;
	}

	public Object getParent(Object object)
	{
		if (parentField != null)
			return ReflectionUtils.get(object, parentField);
		else
			return null;
	}

	public FieldEncoder getEncoder(String... field)
	{
		FieldEntry entry = getFieldEntry(0, field);
		return entry != null ? entry.encoder : null;
	}

	public Field getField(String... field)
	{
		FieldEntry entry = getFieldEntry(0, field);
		return entry != null ? entry.field : null;
	}

	public FieldEntry getFieldEntry(int offset, String... field)
	{
		FieldEntry entry = nameToField.get(field[offset]);
		if (entry != null)
		{
			if (offset < field.length - 1)
			{
				FieldEncoder encoder = entry.encoder;
				if (encoder instanceof CollectionEncoder)
					encoder = ((CollectionEncoder) encoder).getDefaultEncoder();

				if (encoder instanceof EmbeddedEncoder)
					return ((EmbeddedEncoder) encoder).getDefaultEncoder().getFieldEntry(offset + 1, field);
				else
					return null;
			}
			else
			{
				return entry;
			}
		}
		else
		{
			return null;
		}
	}
}
