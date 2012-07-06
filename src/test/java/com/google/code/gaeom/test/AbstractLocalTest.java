package com.google.code.gaeom.test;

import java.util.Set;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.dev.HighRepJobPolicy;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Sets;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class AbstractLocalTest
{
	public static class Policy implements HighRepJobPolicy
	{
		Set<Key> seen = Sets.newHashSet();
		
		@Override
		public boolean shouldApplyNewJob(Key key)
		{
			return check(key);
		}

		private boolean check(Key key)
		{
			if(seen.remove(key))
			{
				return true;
			}
			else
			{
				seen.add(key);
				return false;
			}
		}

		@Override
		public boolean shouldRollForwardExistingJob(Key key)
		{
			return check(key);
		}
	}
	
	private LocalServiceTestHelper helper;

	@Before
	public void setUp()
	{
		if(simulateEventualConsistency())
			helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig().setAlternateHighRepJobPolicyClass(Policy.class));
		else
			helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
		helper.setUp();
	}

	protected boolean simulateEventualConsistency()
	{
		return true;
	}
	
	@After
	public void tearDown()
	{
		helper.tearDown();
	}
}
