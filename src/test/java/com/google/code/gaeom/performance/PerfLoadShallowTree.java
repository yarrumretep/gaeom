package com.google.code.gaeom.performance;



public class PerfLoadShallowTree extends AbstractLoadTree
{
	@Override
	protected Node createTree()
	{
		return Node.create(1, 21111);
	}
}
