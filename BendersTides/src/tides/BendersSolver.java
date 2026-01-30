package tides;

import java.util.ArrayList;

public class BendersSolver
{
	private Instance _instance;
	private Master _master;
	private ArrayList<Subproblem> _subproblems;
	
	private int _iteration;
	private double _lb;
	private double _ub;
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
		_ub = Double.MAX_VALUE;
		_start = System.currentTimeMillis();
		_master.create();

		while( _lb < _ub )
		{
			_iteration++;
			_master.solve();
			_lb = Math.max(_lb, _master.makespan());
			_subproblems.clear();
			
			if( _master.optimal() == false )
				_lb = _ub;
			
			if( _lb < _ub )
			{
				for(int i=0; i<_instance.berths(); ++i)
				{
					_subproblems.add(new Subproblem(_instance, _master.cluster(i)));
					_subproblems.get(i).solve();

					showStatistics(i);
				}
				
				_ub = Math.min(_ub, this.objective());
				for(int i=0; i<_instance.berths(); ++i) if( _subproblems.get(i).makespan() >= _ub - 0.0001 )
					_master.forbid(_master.cluster(i));
			}
			
			showStatistics();
		}
	}
	
	private double objective()
	{
		return _subproblems.stream().mapToDouble(s -> s.makespan()).max().orElse(0);
	}
	
	private void showStatistics(int berth)
	{
		System.out.print("  - Berth " + berth + " | ");
		System.out.print("St: " + _subproblems.get(berth).status() + " | ");
		System.out.print(String.format("%.2f", _subproblems.get(berth).solvingTime()) + " sec. | ");
		System.out.print("Obj: " + String.format("%.2f", _subproblems.get(berth).makespan()) + " | ");
		System.out.println();
	}
	
	private void showStatistics()
	{
		System.out.println();
		System.out.print("It: " + _iteration + " | ");
		System.out.print("St: " + _master.status() + " | ");
		System.out.print(String.format("%.2f", _master.solvingTime()) + " sec. | ");
		System.out.print(String.format("%.2f", (System.currentTimeMillis() - _start) / 1000.0) + " sec. | ");
		System.out.print("LB: " + String.format("%.2f", _lb) + " | ");
		System.out.print("UB: " + String.format("%.2f", _ub) + " | ");
		System.out.print("Gap: " + String.format("%.2f", _lb != 0 ? (_ub - _lb) * 100 / _lb : 0) + "% | ");
		System.out.print("F: " + _master.forbidden() + " | ");
		System.out.println();
		System.out.println();
	}
}
