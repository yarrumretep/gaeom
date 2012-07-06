package com.google.code.gaeom.performance;

public class PerfLoadSquareTree extends AbstractLoadTree
{
	@Override
	protected Node createTree()
	{
		return Node.create(5, 5);
	}
}
