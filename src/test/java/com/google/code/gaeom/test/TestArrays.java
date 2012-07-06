package com.google.code.gaeom.test;

import static com.google.code.gaeom.test.util.RandomArrayUtils.generate;
import static com.google.code.gaeom.test.util.RandomArrayUtils.nextByteArray;
import static com.google.code.gaeom.test.util.RandomArrayUtils.nextCharArray;
import static com.google.code.gaeom.test.util.RandomArrayUtils.nextDoubleArray;
import static com.google.code.gaeom.test.util.RandomArrayUtils.nextFloatArray;
import static com.google.code.gaeom.test.util.RandomArrayUtils.nextIntegerArray;
import static com.google.code.gaeom.test.util.RandomArrayUtils.nextLongArray;
import static com.google.code.gaeom.test.util.RandomArrayUtils.nextShortArray;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.annotation.Embedded;
import com.google.code.gaeom.util.ReflectionUtils;

public class TestArrays extends AbstractLocalTest
{
	public static class Foo
	{
		byte[] b;
		char[] c;
		short[] s;
		int[] i;
		long[] l;
		float[] f;
		double[] d;
		Bar[] bars;
		@Embedded
		Bar[] bars2;
	}

	public static class Bar
	{
		String name;
	}

	ObjectStore os = ObjectStore.Factory.create();

	@Test
	public void testByteArray() throws Exception
	{
		Foo f = new Foo();
		f.b = nextByteArray(10);
		f.c = nextCharArray(10);
		f.s = nextShortArray(10);
		f.i = nextIntegerArray(10);
		f.l = nextLongArray(10);
		f.f = nextFloatArray(10);
		f.d = nextDoubleArray(10);
		f.bars = generate(Bar.class, 10);
		f.bars[3] = null;
		f.bars2 = generate(Bar.class, 10);
		f.bars2[6] = null;

		Key key = os.beginSession().store(f).now();

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Entity e = ds.get(key);
		assertTrue(e.getProperty("b") instanceof Blob);
		assertTrue(e.getProperty("c") instanceof String);

		Foo f2 = os.beginSession().load(key).now();
		assertTrue(ReflectionUtils.fieldsEqual(f, f2));
	}
}
