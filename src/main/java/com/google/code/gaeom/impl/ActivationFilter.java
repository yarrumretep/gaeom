package com.google.code.gaeom.impl;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

public abstract class ActivationFilter
{
	public static String getLevelActivationFilter(int levels)
	{
		StringBuilder builder = new StringBuilder();
		for (int ct = 0; ct < levels; ct++)
		{
			if (ct > 0)
				builder.append('.');
			builder.append('*');
		}
		return builder.toString();
	}

	public static ActivationFilter compile(String... strings)
	{
		return compile(Arrays.asList(strings));
	}

	public static ActivationFilter compile(List<String> patterns)
	{
		if (patterns.size() == 0)
			return null;

		List<InternalActivationFilter> filters = Lists.newArrayList();

		for (String string : patterns)
		{
			if ("**".equals(string))
				filters.add(new ActivateAllFilter());
			if (string == null || "".equals(string))
				filters.add(new NoActivationFilter());
			else
				filters.add(new PatternFilter(string));
		}

		if (filters.size() == 1)
			return filters.get(0);
		else
			return new CompoundActivationFilter(filters);
	}

	public boolean accept(String... path)
	{
		return accept(Arrays.asList(path));
	}

	public abstract boolean accept(List<String> path);

	private static abstract class InternalActivationFilter extends ActivationFilter
	{
		public boolean accept(List<String> path)
		{
			return isNegative() ^ match(path);
		}

		protected abstract boolean match(List<String> path);

		protected abstract boolean isNegative();
	}
	
	private static class NoActivationFilter extends InternalActivationFilter
	{
		@Override
		protected boolean match(List<String> path)
		{
			return true;
		}

		@Override
		protected boolean isNegative()
		{
			return true;
		}
	}

	private static class PatternFilter extends InternalActivationFilter
	{
		String[] pattern;
		boolean not = false;

		private static final String cManyLevelMatch = "**";
		private static final String cOneLevelMatch = "*";
		private static final String cNot = "!";

		PatternFilter(String p)
		{
			if (p.startsWith(cNot))
			{
				not = true;
				p = p.substring(cNot.length());
			}
			this.pattern = p.split("\\.");
			for (int ct = 0; ct < pattern.length; ct++)
			{
				if (cManyLevelMatch.equals(pattern[ct]))
					pattern[ct] = cManyLevelMatch;
				else if (cOneLevelMatch.equals(pattern[ct]))
					pattern[ct] = cOneLevelMatch;
			}
		}

		@Override
		protected boolean match(List<String> path)
		{
			return match(path, 0, 0, false);
		}

		@Override
		protected boolean isNegative()
		{
			return not;
		}

		private boolean match(List<String> path, int pathOffset, int patternOffset, boolean matchEnd)
		{
			if (((!not && !matchEnd) || patternOffset == pattern.length) && pathOffset == path.size())
				return true;

			if (patternOffset >= pattern.length || pathOffset >= path.size())
				return false;

			if (pattern[patternOffset] == cManyLevelMatch)
			{
				return match(path, pathOffset + 1, patternOffset, true) || match(path, pathOffset, patternOffset + 1, true) || match(path, pathOffset + 1, patternOffset + 1, true);
			}
			else if (pattern[patternOffset] != cOneLevelMatch && !pattern[patternOffset].equals(path.get(pathOffset)))
			{
				return false;
			}
			else
			{
				return match(path, pathOffset + 1, patternOffset + 1, matchEnd);
			}
		}
	}

	private static class CompoundActivationFilter extends ActivationFilter
	{
		private final List<InternalActivationFilter> includes = Lists.newArrayList();
		private final List<InternalActivationFilter> excludes = Lists.newArrayList();

		CompoundActivationFilter(List<InternalActivationFilter> list)
		{
			for (InternalActivationFilter filter : list)
			{
				if (filter.isNegative())
					excludes.add(filter);
				else
					includes.add(filter);
			}
		}

		@Override
		public boolean accept(List<String> path)
		{
			for (InternalActivationFilter include : includes)
			{
				if (include.match(path))
				{
					for (InternalActivationFilter exclude : excludes)
					{
						if (exclude.match(path))
							return false;
					}
					return true;
				}
			}
			return false;
		}
	}
	private static class ActivateAllFilter extends InternalActivationFilter
	{
		@Override
		protected boolean match(List<String> path)
		{
			return true;
		}

		@Override
		protected boolean isNegative()
		{
			return false;
		}
	}
}
