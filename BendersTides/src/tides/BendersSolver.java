package tides;

import java.util.ArrayList;

public class BendersSolver
{
	private Instance _instance;
	private Master _master;
	private ArrayList<Subproblem> _subproblems;
	
	private int _iteration;
	private int _lb;
	private int _ub;
	private long _start;
	
	public BendersSolver(Instance instance)
	{
		_instance = instance;
		_master = new Master(instance);
		_subproblems = new ArrayList<Subproblem>();
	}
	
	public void solve()
	{
		_iteration = 0;
		_lb = 0;
		_ub = Integer.MAX_VALUE;
		_start = System.currentTimeMillis();
		_master.create();

		while( _lb < _ub )
		{
			_iteration++;
			_master.solve();
			_lb = Math.max(_lb, (int)_master.makespan());
			_subproblems.clear();
			
			if( _lb < _ub )
			{
				for(int i=0; i<_instance.berths(); ++i)
				{
					_subproblems.add(new Subproblem(_instance, _master.cluster(i)));
					_subproblems.get(i).solve();
				}
				
				_ub = Math.min(_ub, this.objective());
				for(int i=0; i<_instance.berths(); ++i) if( _subproblems.get(i).makespan() >= _ub )
					_master.forbid(_master.cluster(i));
			}
			
			showStatistics();
		}
	}
	
	private int objective()
	{
		return _subproblems.stream().mapToInt(s -> (int)s.makespan()).max().orElse(0);
	}
	
	private void showStatistics()
	{
		System.out.print("It: " + _iteration + " | ");
		System.out.print(String.format("%.2f", (System.currentTimeMillis() - _start) / 1000.0) + " sec. | ");
		System.out.print("LB: " + _lb + " | ");
		System.out.print("UB: " + _ub + " | ");
		System.out.print("F: " + _master.forbidden() + " | ");
		System.out.println();
		System.out.println();
	}
}
