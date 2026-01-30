package tides;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class StretchedMaster
{
	private Instance _instance;
	private Stretcher _stretcher;
	private MPSolver _solver;
	private MPVariable[][][] x;
	private MPVariable z;
	private ResultStatus _status;

	private boolean _verbose = true;
	private boolean _optimal;
	private double _start;
	private double _time;
	private double _makespan;
	private int[] _berth;
	private int _forbidden;
	
	public StretchedMaster(Instance instance)
	{
		_instance = instance;
		_stretcher = new Stretcher(_instance);
	}
	
	public void create()
	{
		createSolver();
		createVariables();
		createAssignmentConstraints();
		createNonOverlappingConstraints();
		createBindingConstraints();
		createObjective();
	}
	
	private void createSolver()
	{
	    _solver = MPSolver.createSolver("SCIP");
	    _forbidden = 0;

	    if (_solver == null)
	    	throw new RuntimeException("Solver is null!");
	}
	
	private void createVariables()
	{
		x = new MPVariable[_instance.ships()][_instance.berths()][_instance.tides()];
		z = _solver.makeNumVar(0, 1000, "z");
		
		for(int i=0; i<_instance.ships(); ++i)
		for(int t=0; t<_instance.tides(); ++t) if( _stretcher.feasible(i, t) )
		for(int k=0; k<_instance.berths(); ++k)
			x[i][k][t] = _solver.makeBoolVar("x(" + i + "," + k + "," + t + ")");
	}
	
	private void createAssignmentConstraints()
	{
		for(int i=0; i<_instance.ships(); ++i)
		{
			MPConstraint constr = _solver.makeConstraint(1, 1);
			
			for(int k=0; k<_instance.berths(); ++k)
			for(int t=0; t<_instance.tides(); ++t) if( x[i][k][t] != null )
				constr.setCoefficient(x[i][k][t], 1);
		}
	}
	
	private void createBindingConstraints()
	{
		for(int i=0; i<_instance.ships(); ++i)
		{
			MPConstraint constr = _solver.makeConstraint(-1000, 0);
			constr.setCoefficient(z, -1);
			
			for(int k=0; k<_instance.berths(); ++k)
			for(int t=0; t<_instance.tides(); ++t) if( x[i][k][t] != null )
				constr.setCoefficient(x[i][k][t], _stretcher.releaseTime(i,t));
		}
	}

	private void createNonOverlappingConstraints()
	{
		for(int k=0; k<_instance.berths(); ++k)
		for(int t=0; t<_instance.tides(); ++t)
		{
			MPConstraint constr = _solver.makeConstraint(0, 1);

			for(int i=0; i<_instance.ships(); ++i)
			for(int st=0; st<=t; ++st) if( x[i][k][st] != null && _stretcher.endingTide(i, st) > t )
				constr.setCoefficient(x[i][k][st], 1);
		}
	}

	private void createObjective()
	{
		MPObjective obj = _solver.objective();
		obj.setCoefficient(z, 1);
	}
	
	public void forbid(Cluster cluster)
	{
		for(int k=0; k<_instance.berths(); ++k)
		{
			MPConstraint constr = _solver.makeConstraint(0, cluster.ships()-1);

			for(int i=0; i<cluster.ships(); ++i)
			for(int t=0; t<_instance.tides(); ++t) if( x[cluster.index(i)][k][t] != null )
				constr.setCoefficient(x[cluster.index(i)][k][t], 1);
		}
		
		_forbidden++;
	}
	
	public double solve()
	{
		_start = System.currentTimeMillis();
		_status = _solver.solve();
		_time = (System.currentTimeMillis() - _start) / 1000.0;
		_optimal = _status == ResultStatus.OPTIMAL;
		
		if( _optimal == true )
		{
			_makespan = z.solutionValue();
			_berth = new int[_instance.ships()];
	
			for(int i=0; i<_instance.ships(); ++i)
			for(int k=0; k<_instance.berths(); ++k)
			for(int t=0; t<_instance.tides(); ++t) if( x[i][k][t] != null && x[i][k][t].solutionValue() > 0.9 )
				_berth[i] = k;
		}

		if( _verbose == true )
		{
			System.out.println("Status: " + _status);
			
			if( _optimal == true )
			{
				System.out.println("Makespan: " + z.solutionValue());
				System.out.println();
		
				for(int i=0; i<_instance.ships(); ++i)
				for(int k=0; k<_instance.berths(); ++k)
				for(int t=0; t<_instance.tides(); ++t) if( x[i][k][t] != null && x[i][k][t].solutionValue() > 0.9 )
					System.out.println(" - Ship " + i + " -> Berth " + k + ", Release: " + _stretcher.releaseTime(i,t));
			}
	
			System.out.println();
		}
		
		return _makespan;
	}
	
	public void close()
	{
		_solver.clear();
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
