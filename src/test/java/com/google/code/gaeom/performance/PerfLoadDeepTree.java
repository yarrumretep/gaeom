package com.google.code.gaeom.performance;

public class PerfLoadDeepTree extends AbstractLoadTree
{
	@Override
	protected Node createTree()
	{
		return Node.create(12, 2);
	}
}
