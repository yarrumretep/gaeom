package com.google.code.gaeom.performance;

import org.apache.commons.lang.math.RandomUtils;

public class Phone
{
	public static enum Type
	{
		Home,
		Work,
		Cell
	}
	
	Type type;
	Integer areaCode;
	Integer prefix;
	Integer suffix;
	
	private static int gen(int bottom, int top)
	{
		return RandomUtils.nextInt(top - bottom) + bottom;
	}
	
	public static Phone generate()
	{
		return new Phone(Type.values()[gen(0, Type.values().length)], gen(100, 1000), gen(100,1000), gen(1000, 10000));
	}
	
	@SuppressWarnings("unused") // for gaeom & twig
	private Phone()
	{
	}
	
	public Phone(Type type, Integer areaCode, Integer prefix, Integer suffix)
	{
		this.type = type;
		this.areaCode = areaCode;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((areaCode == null) ? 0 : areaCode.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Phone other = (Phone) obj;
		if (areaCode == null)
		{
			if (other.areaCode != null)
				return false;
		}
		else if (!areaCode.equals(other.areaCode))
			return false;
		if (prefix == null)
		{
			if (other.prefix != null)
				return false;
		}
		else if (!prefix.equals(other.prefix))
			return false;
		if (suffix == null)
		{
			if (other.suffix != null)
				return false;
		}
		else if (!suffix.equals(other.suffix))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
