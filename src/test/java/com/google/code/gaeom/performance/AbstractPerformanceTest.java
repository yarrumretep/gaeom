package com.google.code.gaeom.performance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import org.junit.Assert;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.thoughtworks.xstream.XStream;

public abstract class AbstractPerformanceTest
{
	private ObjectStore os;

	public static class Entry
	{
		long minimum;
		long average;
		long maximum;
		long stddev;
	}

	private XStream getXStream()
	{
		XStream xstream = new XStream();
		xstream.alias("entry", Entry.class);
		return xstream;
	}

	private static final boolean cRunPerformance = !"false".equals(System.getProperty("runPerformanceTests"));

	private static final DecimalFormat cFormat = new DecimalFormat("0.00 %");

	@Test
	public final void runPerformanceTest() throws Throwable
	{
		if (cRunPerformance)
		{
			Entry best = null;
			String logDir = System.getProperty("com.google.code.gaeom.performance.PerformanceLogDir");
			if (logDir == null)
				logDir = "./perf";
			File dir = new File(logDir);
			dir.mkdirs();
			File bestFile = new File(dir, getClass().getName() + ".best.xml");
			if (bestFile.exists())
			{
				FileInputStream fis = new FileInputStream(bestFile);
				try
				{
					best = (Entry) getXStream().fromXML(fis);
				}
				finally
				{
					fis.close();
				}
			}
			onceBefore();
			if (warmup())
				runTest();
			int runs = runs();
			long sum = 0;
			long min = Long.MAX_VALUE;
			long max = Long.MIN_VALUE;
			long sumofsquares = 0;
			for (int ct = 0; ct < runs; ct++)
			{
				long time = runTest();
				min = time < min ? time : min;
				max = time > max ? time : max;
				sum += time;
				sumofsquares += time * time;
			}
			onceAfter();
			long average = sum / runs;
			long stddev = (long) Math.sqrt(runs * sumofsquares - sum * sum) / runs;

			Entry entry = new Entry();
			entry.minimum = min;
			entry.average = average;
			entry.maximum = max;
			entry.stddev = stddev;

			{
				FileOutputStream fos = new FileOutputStream(new File(dir, getClass().getName() + ".last.xml"));
				try
				{
					getXStream().toXML(entry, fos);
				}
				finally
				{
					fos.close();
				}
			}

			if (best != null)
				Assert.assertTrue("Performance degredation: " + cFormat.format((average - best.average) / (double) best.average) + "%", (average - best.average) < best.average / 10); // less
// than 10% degredation

			if (best == null || entry.average < best.average)
			{
				FileOutputStream fos = new FileOutputStream(bestFile);
				try
				{
					getXStream().toXML(entry, fos);
				}
				finally
				{
					fos.close();
				}
			}
		}
	}

	protected boolean warmup()
	{
		return true;
	}

	protected int runs()
	{
		return 15;
	}

	protected ObjectStoreSession createSession()
	{
		return os.beginSession(dds.asAsync());
	}

	private DummyDatastoreService dds;

	protected long runTest() throws Throwable
	{
		LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setNoStorage(true));
		helper.setUp();
		os = ObjectStore.Factory.create();
		dds = new DummyDatastoreService();
		try
		{
			before();
			long start = System.currentTimeMillis();
			test();
			long duration = System.currentTimeMillis() - start;
			after();
			return duration;
		}
		finally
		{
			helper.tearDown();
			os = null;
			dds = null;
		}
	}

	protected void onceBefore() throws Throwable
	{
	}

	protected void before() throws Throwable
	{
	}

	protected abstract void test() throws Throwable;

	protected void after() throws Throwable
	{
	}

	protected void onceAfter() throws Throwable
	{
	}
}
