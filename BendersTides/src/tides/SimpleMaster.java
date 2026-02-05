package tides;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class SimpleMaster extends Master
{
	private MPSolver _solver;
	private MPVariable[][] x;
	private MPVariable z;

	public SimpleMaster(Instance instance)
	{
		super(instance);
	}
	
	public void create()
	{
		createSolver();
		createVariables();
		createAssignmentConstraints();
		createBindingConstraints();
		createObjective();
	}
	
	private void createSolver()
	{
	    _solver = MPSolver.createSolver("SCIP");
	    _forbidden = 0;

	    if( _solver == null )
	    	throw new RuntimeException("Solver is null!");
	    
	    if( _solverOutput == true )
			_solver.enableOutput();
	}
	
	private void createVariables()
	{
		x = new MPVariable[_instance.ships()][_instance.berths()];
		z = _solver.makeNumVar(0, 1000, "z");
		
		for(int i=0; i<_instance.ships(); ++i)
		for(int k=0; k<_instance.berths(); ++k)
			x[i][k] = _solver.makeBoolVar("x(" + i + "," + k + ")");
	}
	
	private void createAssignmentConstraints()
	{
		for(int i=0; i<_instance.ships(); ++i)
		{
			MPConstraint constr = _solver.makeConstraint(1, 1);
			
			for(int k=0; k<_instance.berths(); ++k)
				constr.setCoefficient(x[i][k], 1);
		}
	}
	
	private void createBindingConstraints()
	{
		for(int k=0; k<_instance.berths(); ++k)
		{
			MPConstraint constr = _solver.makeConstraint(-1000, 0);
			constr.setCoefficient(z, -1);
			
			for(int i=0; i<_instance.ships(); ++i)
				constr.setCoefficient(x[i][k], _instance.attention(i));
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
				constr.setCoefficient(x[cluster.index(i)][k], 1);
		}
		
		_forbidden++;
	}
	
	protected void solveModel(double timeLimit)
	{
		_solver.setTimeLimit((int)(1000 * timeLimit));
		_status = _solver.solve();
		
		if( _status == ResultStatus.OPTIMAL || _status == ResultStatus.FEASIBLE )
		{
			_makespan = z.solutionValue();
			_lb = _solver.objective().bestBound();
			_berth = new int[_instance.ships()];
	
			for(int i=0; i<_instance.ships(); ++i)
			for(int k=0; k<_instance.berths(); ++k) if( x[i][k].solutionValue() > 0.9 )
				_berth[i] = k;
		}

		if( _verbose == true )
		{
			System.out.println("Status: " + _status);
			
			if( _status == ResultStatus.OPTIMAL || _status == ResultStatus.FEASIBLE )
			{
				System.out.println("Makespan: " + z.solutionValue());
				System.out.println();
		
				for(int i=0; i<_instance.ships(); ++i)
				for(int k=0; k<_instance.berths(); ++k) if( x[i][k].solutionValue() > 0.9 )
					System.out.println(" - Ship " + i + " -> Berth " + k);
			}
	
			System.out.println();
		}
	}
	
	public void close()
	{
		_solver.clear();
	}
}
