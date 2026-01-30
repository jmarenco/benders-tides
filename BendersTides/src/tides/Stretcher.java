package tides;

import java.util.Map;
import java.util.HashMap;

public class Stretcher
{
	private Instance _instance;
	private Map<String, Integer> _ending;
	private Map<String, Double> _release;
	
	public Stretcher(Instance instance)
	{
		_instance = instance;
		_ending = new HashMap<String, Integer>();
		_release = new HashMap<String, Double>();
		
		for(int i=0; i<_instance.ships(); ++i)
		for(int t=0; t<_instance.tides(); ++t)
		{
			int et = t;
			while( et < _instance.tides() && _instance.end(et) < _instance.start(t) + _instance.attention(i) )
				++et;
			
			if( et < _instance.tides() )
			{
				_ending.put(i + "/" + t, et);
				_release.put(i + "/" + t, Math.max(_instance.start(et), _instance.start(t) + _instance.attention(i)));
			}
			
		}
	}
	
	public boolean feasible(int ship, int startingTide)
	{
		return _ending.containsKey(ship + "/" + startingTide);
	}

	public int endingTide(int ship, int startingTide)
	{
		return _ending.get(ship + "/" + startingTide);
	}
	
	public double releaseTime(int ship, int startingTide)
	{
		return _release.get(ship + "/" + startingTide);
	}
}
