package tides;

import java.util.ArrayList;
import java.util.Objects;

public class Cluster
{
	private Instance _instance;
	private ArrayList<Integer> _index;
	
	public Cluster(Instance instance)
	{
		_instance = instance;
		_index = new ArrayList<Integer>();
	}
	
	public void addShip(int index)
	{
		_index.add(index);
	}
	
	public int ships()
	{
		return _index.size();
	}
	
	public int index(int i)
	{
		return _index.get(i);
	}
	
	public double attention(int i)
	{
		return _instance.attention(index(i));
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(_index);
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
		Cluster other = (Cluster) obj;
		return Objects.equals(_index, other._index);
	}
}
