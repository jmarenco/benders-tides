package tides;

import com.google.ortools.linearsolver.MPSolver.ResultStatus;

public abstract class Master
{
	protected Instance _instance;
	protected ResultStatus _status;

	protected boolean _verbose = false;
	protected boolean _optimal;
	protected double _makespan;
	protected double _time;
	protected int[] _berth;
	protected int _forbidden;
	
	public Master(Instance instance)
	{
		_instance = instance;
	}
	
	public abstract void create();
	public abstract void forbid(Cluster cluster);
	protected abstract void solveModel();
	public abstract void close();

	public double solve()
	{
		long start = System.currentTimeMillis();
		
		solveModel();

		_time = (System.currentTimeMillis() - start) / 1000.0;
		_optimal = _status == ResultStatus.OPTIMAL;
		
		return _makespan;
	}
	
	
	public double makespan()
	{
		return _makespan;
	}
	
	public int berth(int shipIndex)
	{
		return _berth[shipIndex];
	}
	
	public int forbidden()
	{
		return _forbidden;
	}
	
	public Cluster cluster(int berth)
	{
		Cluster ret = new Cluster(_instance);
		
		for(int i=0; i<_instance.ships(); ++i) if( berth(i) == berth )
			ret.addShip(i);
		
		return ret;
	}
	
	public String status()
	{
		return _status.toString();
	}
	
	public double solvingTime()
	{
		return _time;
	}
	
	public boolean optimal()
	{
		return _optimal;
	}
}
