package com.google.code.gaeom.performance;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.google.code.gaeom.annotation.Embedded;
import com.google.common.collect.Lists;

public class Node
{
	Node parent;

	String name;

	double payload;

	@Embedded
	List<Phone> phones = Lists.newArrayList();

	List<Node> children = Lists.newArrayList();

	public static Node create(int depth, int fanout)
	{
		if (Math.pow(fanout, depth) > 500000)
			throw new IllegalArgumentException("Cannot allocate a tree of " + ((int) Math.pow(fanout, depth)) + " nodes.");

		Node node = new Node();
		node.payload = RandomUtils.nextDouble() * 100;
		node.name = RandomStringUtils.randomAlphanumeric(30);
		int count = RandomUtils.nextInt(4);
		for (int ct = 0; ct < count; ct++)
			node.phones.add(Phone.generate());
		if (depth > 0)
		{
			for (int ct = 0; ct < fanout; ct++)
				node.addChild(create(depth - 1, fanout));
		}
		return node;
	}

	public void addChild(Node child)
	{
		if (child.parent != null)
			child.parent.removeChild(child);
		child.parent = this;
		children.add(child);
	}

	public void removeChild(Node child)
	{
		if (children.remove(child))
			child.parent = null;
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(payload);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((phones == null) ? 0 : phones.hashCode());
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
		Node other = (Node) obj;
		if (children == null)
		{
			if (other.children != null)
				return false;
		}
		else if (!children.equals(other.children))
			return false;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(payload) != Double.doubleToLongBits(other.payload))
			return false;
		if (phones == null)
		{
			if (other.phones != null)
				return false;
		}
		else if (!phones.equals(other.phones))
			return false;
		return true;
	}

	public int count()
	{
		int count = 1;
		for (Node node : children)
			count += node.count();
		return count;
	}
}
