package com.google.code.gaeom.test.util;

import java.lang.reflect.Array;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.google.code.gaeom.util.ObjectUtils;

public class RandomArrayUtils
{
	public static byte[] nextByteArray(int length)
	{
		byte[] bytes = new byte[length];
		for (int ct = 0; ct < length; ct++)
			bytes[ct] = (byte) RandomUtils.nextInt(256);
		return bytes;
	}

	public static char[] nextCharArray(int length)
	{
		char[] array = new char[length];
		for (int ct = 0; ct < length; ct++)
			array[ct] = RandomStringUtils.randomAlphanumeric(1).charAt(0);
		return array;
	}

	public static short[] nextShortArray(int length)
	{
		short[] array = new short[length];
		for (int ct = 0; ct < length; ct++)
			array[ct] = (short) RandomUtils.nextInt(Short.MAX_VALUE);
		return array;
	}

	public static int[] nextIntegerArray(int length)
	{
		int[] array = new int[length];
		for (int ct = 0; ct < length; ct++)
			array[ct] = RandomUtils.nextInt();
		return array;
	}

	public static long[] nextLongArray(int length)
	{
		long[] array = new long[length];
		for (int ct = 0; ct < length; ct++)
			array[ct] = RandomUtils.nextLong();
		return array;
	}

	public static float[] nextFloatArray(int length)
	{
		float[] array = new float[length];
		for (int ct = 0; ct < length; ct++)
			array[ct] = RandomUtils.nextFloat();
		return array;
	}

	public static double[] nextDoubleArray(int length)
	{
		double[] array = new double[length];
		for (int ct = 0; ct < length; ct++)
			array[ct] = RandomUtils.nextDouble();
		return array;
	}

	public static <T> T[] generate(Class<T> clazz, int length)
	{
		Object array = Array.newInstance(clazz, length);
		Generator<T> g = Generator.generate(clazz);
		for (int ct = 0; ct < length; ct++)
			Array.set(array, ct, g.generate());
		return ObjectUtils.cast(array);
	}
}
