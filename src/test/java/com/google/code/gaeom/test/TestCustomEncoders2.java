package com.google.code.gaeom.test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import com.google.code.gaeom.CustomEncoder;
import com.google.code.gaeom.CustomMultiEncoder;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.EncodeWith;
import com.google.code.gaeom.util.MapUtils;
import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestCustomEncoders2 extends AbstractLocalTest
{
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
		public boolean equals(Object obj)
		{
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

	public static class PointMapEncoder implements CustomMultiEncoder<Point>
	{
		@Override
		public Map<String, ?> encode(Point value)
		{
			return MapUtils.createMap("x", value.x, "y", value.y);
		}

		@Override
		public Point decode(Map<String, ?> value)
		{
			return new Point(((Number) value.get("x")).intValue(), ((Number) value.get("y")).intValue());
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

	@Test
	public void testMapEncoder()
	{
		Point p = randomPoint();
		Map<String, ?> map = new PointMapEncoder().encode(p);
		Point p2 = new PointMapEncoder().decode(map);
		assertEquals(p, p2);
	}

	public static class LongEncodedEntity
	{
		@EncodeWith(PointLongEncoder.class)
		Point p = randomPoint();
	}

	public static class MapEncodedEntity
	{
		@EncodeWith(PointMapEncoder.class)
		Point p = randomPoint();
	}

	@Test
	public void testCustomSinglePropertyEncoder()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		LongEncodedEntity e = new LongEncodedEntity();
		oss.store(e).now();
		Point orig = e.p;
		e.p = null;
		oss.refresh(e).now();
		assertEquals(orig, e.p);
		assertEquals(orig, e.p);
	}

	@Test
	public void testCustomMultiPropertyEncoder()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		MapEncodedEntity e = new MapEncodedEntity();
		oss.store(e).now();
		Point orig = e.p;
		e.p = null;
		oss.refresh(e).now();
		assertEquals(orig, e.p);
		assertEquals(orig, e.p);
	}

	public static class MapListEncoder
	{
		@EncodeWith(PointMapEncoder.class)
		List<Point> points = Lists.newArrayList();
	}

	@Test
	public void testCustomMultiPropertyEncoderCollection()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();

		MapListEncoder mle = new MapListEncoder();
		mle.points.add(randomPoint());
		mle.points.add(randomPoint());
		mle.points.add(randomPoint());

		oss.store(mle).now();

		List<Point> orig = mle.points;
		mle.points = null;

		oss.refresh(mle).now();

		assertEquals(orig.get(0), mle.points.get(0));
		assertEquals(orig.get(1), mle.points.get(1));
		assertEquals(orig.get(2), mle.points.get(2));
		assertEquals(3, mle.points.size());
	}
}
