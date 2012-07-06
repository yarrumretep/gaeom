package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;

public class TestPrimitives extends AbstractLocalTest
{
	public static class Foo
	{
		byte b;
		char c;
		short s;
		int i;
		long l;
		float f;
		double d;
	}

	ObjectStore os = ObjectStore.Factory.create();

	@Test
	public void testByte()
	{
		Foo f = new Foo();
		f.b = (byte) RandomUtils.nextInt(256);
		f.c = RandomStringUtils.randomAlphanumeric(1).charAt(0);
		f.s = (short) RandomUtils.nextInt(Short.MAX_VALUE);
		f.i = RandomUtils.nextInt();
		f.l = RandomUtils.nextLong();
		f.f = RandomUtils.nextFloat();
		f.d = RandomUtils.nextDouble();

		Key key = os.beginSession().store(f).now();

		Foo g = os.beginSession().load(key).now();
		assertEquals(f.b, g.b);
		assertEquals(f.c, g.c);
		assertEquals(f.s, g.s);
		assertEquals(f.i, g.i);
		assertEquals(f.l, g.l);
		assertEquals(f.f, g.f, 0);
		assertEquals(f.d, g.d, 0);
	}
}
