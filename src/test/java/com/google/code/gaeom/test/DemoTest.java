package com.google.code.gaeom.test;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;

public class DemoTest extends AbstractLocalTest
{
	public static class Gaeom
	{
		String description;
		boolean rocks;
	}

	@Test
	public void demoTest()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		Gaeom gaeom = new Gaeom();
		gaeom.description = "Supah object-datastore mapping";
		gaeom.rocks = true;

		oss.store(gaeom).id(1).now();

		os.beginSession().load(Gaeom.class).id(1).now();

		os.beginSession().find(Gaeom.class).single().now();

		os.beginSession().find(Gaeom.class).filter("rocks", true).single().now();
	}
}
