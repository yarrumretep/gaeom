package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.CustomEncoder;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.EncodeWith;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestCustomEncoders extends AbstractLocalTest
{
	@EncodeWith(PointLongEncoder.class)
	private static class Point
	{
		final int x;
		final int y;

		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}

	public static class PointLongEncoder implements CustomEncoder<Point, Long>
	{
		@Override
		public Long encode(Point value)
		{
			if (value == null)
				return null;
			else
				return ((long) value.x) << 32 | ((long) value.y);
		}

		@Override
		public Point decode(Long valueObject)
		{
			if (valueObject == null)
			{
				return null;
			}
			else
			{
				long value = valueObject;
				return new Point((int) (value >> 32), ((int) value));
			}
		}
	}

	private static Point randomPoint()
	{
		return new Point(RandomUtils.nextInt(), RandomUtils.nextInt());
	}

	@Test
	public void testLongEncoder()
	{
		Point p = randomPoint();
		Long l = new PointLongEncoder().encode(p);
		Point p2 = new PointLongEncoder().decode(l);
		assertEquals(p, p2);
	}

	public static class LongEncodedEntity
	{
		Point p = randomPoint();
	}

	@Test
	public void testCustomPropertyEncoder() throws Exception
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		LongEncodedEntity e = new LongEncodedEntity();
		Key key = oss.store(e).now();
		Point orig = e.p;
		Long l = new PointLongEncoder().encode(orig);
		
		e.p = null;
		oss.refresh(e).now();
		assertEquals(orig, e.p);

		Entity ent = DatastoreServiceFactory.getDatastoreService().get(key);
		assertEquals(l, ent.getProperty("p"));
	}
}
