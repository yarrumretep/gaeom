package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Lists;

public class TestActivationFilter2 extends AbstractLocalTest
{
	public static class Node
	{
		Node parent;
		List<Node> children;
		String name;
	}

	@Test
	public void testHierarchyReading()
	{
		ObjectStore store = ObjectStore.Factory.create();

		Node root = new Node();
		root.name = "Root";

		Node child1 = new Node();
		child1.name = "Child1";
		child1.parent = root;

		Node child2 = new Node();
		child2.name = "Child2";
		child2.parent = root;

		root.children = Lists.newArrayList(child1, child2);

		Node grandchild1 = new Node();
		grandchild1.name = "Grandkid1";
		grandchild1.parent = child2;

		Node grandchild2 = new Node();
		grandchild2.name = "Grandkid2";
		grandchild2.parent = child2;

		child2.children = Lists.newArrayList(grandchild1, grandchild2);

		Node grandchild3 = new Node();
		grandchild3.name = "Grandkid3";
		grandchild3.parent = child1;

		child1.children = Lists.newArrayList(grandchild3);

		ObjectStoreSession sess1 = store.beginSession();
		sess1.store(root).now();
		Key key = sess1.getKey(child2);

		ObjectStoreSession sess2 = store.beginSession();
		Node child2a = sess2.load(key).activate("**.children", "**.parent", "!**.parent.children").now();

		assertEquals(child2.name, child2a.name);
		assertEquals(child2.children.size(), child2a.children.size());
		assertEquals(child2.children.get(0).name, child2a.children.get(0).name);
		assertEquals(child2.children.get(1).name, child2a.children.get(1).name);
		assertEquals(child2.parent.name, child2a.parent.name);
		assertEquals(child2a, child2a.parent.children.get(1));
		assertEquals(null, child2a.parent.children.get(0).name);
		assertEquals(null, child2a.parent.children.get(0).children);

		sess2.refresh(child2a.parent.children.get(0)).now();
		
		assertEquals("Child1", child2a.parent.children.get(0).name);
		assertEquals(1, child2a.parent.children.get(0).children.size());
		assertEquals("Grandkid3", child2a.parent.children.get(0).children.get(0).name);
	}
}
