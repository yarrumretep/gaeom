package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Parent;
import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestParenting extends AbstractLocalTest
{
	public static class Node
	{
		@Parent
		Node parent;
		List<Node> children = Lists.newArrayList();

		public void addChild(Node child)
		{
			child.parent = this;
			children.add(child);
		}
	}

	public static Node create(int depth, int fanout)
	{
		Node node = new Node();
		if (depth > 0)
		{
			for (int ct = 0; ct < fanout; ct++)
				node.addChild(create(depth - 1, fanout));
		}
		return node;
	}

	@Test
	public void testParenting()
	{
		Node node = create(3, 3);
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		oss.store(node).now();

		testParent(oss, node, null);
	}

	private void testParent(ObjectStoreSession session, Node node, Node parent)
	{
		if (parent != null)
			assertEquals(session.getKey(parent), session.getKey(node).getParent());
		for (Node child : node.children)
			testParent(session, child, parent);
	}
}
