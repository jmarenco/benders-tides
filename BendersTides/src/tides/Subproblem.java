package tides;

import java.util.ArrayList;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class Subproblem
{
	private Instance _instance;
	private ArrayList<Double> _attention;

	private MPSolver _solver;
	private MPVariable[][] wl;
	private MPVariable[][] wr;
	private MPVariable[][] y;
	private MPVariable[] l;
	private MPVariable[] r;
	private MPVariable z;
	private ResultStatus _status;

	private boolean _verbose = false;
	private double _makespan;
	private double _start;
	private double _time;
	
	public Subproblem(Instance instance)
	{
		_instance = instance;
		_attention = new ArrayList<Double>();
	}
	
	public Subproblem(Instance instance, Cluster cluster)
	{
		_instance = instance;
		_attention = new ArrayList<Double>();
		
		for(int i=0; i<cluster.ships(); ++i)
			addShip(cluster.attention(i));
	}
	
	public void addShip(double attention)
	{
		_attention.add(attention);
	}
	
	public double solve()
	{
		if( _attention.size() == 0 )
			return 0;
		
		createSolver();
		createVariables();
		createAssignmentConstraints();
		createBindingConstraints();
		createAttentionConstraints();
		createNonOverlappingConstraints();
		createMakespanConstraints();
		createAntiparallelismConstraints();
		createObjective();
		solveModel();
		closeSolver();
		
		return _makespan;
	}
	
	private void createSolver()
	{
	    _solver = MPSolver.createSolver("SCIP");

	    if (_solver == null)
	    	throw new RuntimeException("Solver is null!");
	}

	private void createVariables()
	{
		wl = new MPVariable[_attention.size()][_instance.tides()];
		wr = new MPVariable[_attention.size()][_instance.tides()];
		y = new MPVariable[_attention.size()][_attention.size()];
		l = new MPVariable[_attention.size()];
		r = new MPVariable[_attention.size()];
		z = _solver.makeNumVar(0, 1000, "z");
		
		for(int i=0; i<_attention.size(); ++i)
		for(int t=0; t<_instance.tides(); ++t)
		{
			wl[i][t] = _solver.makeBoolVar("wl(" + i + "," + t + ")");
			wr[i][t] = _solver.makeBoolVar("wr(" + i + "," + t + ")");
		}

		for(int i=0; i<_attention.size(); ++i)
		for(int j=0; j<_attention.size(); ++j) if( i != j )
			y[i][j] = _solver.makeBoolVar("y(" + i + "," + j + ")");

		for(int i=0; i<_attention.size(); ++i)
		{
			l[i] = _solver.makeNumVar(0, 1000, "l(" + i + ")");
			r[i] = _solver.makeNumVar(0, 1000, "r(" + i + ")");
		}
	}
	
	private void createAssignmentConstraints()
	{
		for(int i=0; i<_attention.size(); ++i)
		{
			MPConstraint constrl = _solver.makeConstraint(1, 1);
			MPConstraint constrr = _solver.makeConstraint(1, 1);
			
			for(int t=0; t<_instance.tides(); ++t)
			{
				constrl.setCoefficient(wl[i][t], 1);
				constrr.setCoefficient(wr[i][t], 1);
			}
		}
	}

	private void createBindingConstraints()
	{
		for(int i=0; i<_attention.size(); ++i)
		{
			MPConstraint constr1 = _solver.makeConstraint(_instance.start(0), 10000);
			MPConstraint constr2 = _solver.makeConstraint(-10000, _instance.end(0));
			MPConstraint constr3 = _solver.makeConstraint(_instance.start(0), 10000);
			MPConstraint constr4 = _solver.makeConstraint(-10000, _instance.end(0));

			constr1.setCoefficient(l[i], 1);
			constr2.setCoefficient(l[i], 1);
			constr3.setCoefficient(r[i], 1);
			constr4.setCoefficient(r[i], 1);
			
			for(int t=1; t<_instance.tides(); ++t)
			{
				constr1.setCoefficient(wl[i][t], _instance.start(0) - _instance.start(t));
				constr2.setCoefficient(wl[i][t], _instance.end(0) - _instance.end(t));
				constr3.setCoefficient(wr[i][t], _instance.start(0) - _instance.start(t));
				constr4.setCoefficient(wr[i][t], _instance.end(0) - _instance.end(t));
			}
		}
	}
	
	private void createAttentionConstraints()
	{
		for(int i=0; i<_attention.size(); ++i)
		{
			MPConstraint constr = _solver.makeConstraint(_attention.get(i), 10000);
			constr.setCoefficient(r[i], 1);
			constr.setCoefficient(l[i], -1);
		}
	}
	
	private void createNonOverlappingConstraints()
	{
		for(int i=0; i<_attention.size(); ++i)
		for(int j=0; j<_attention.size(); ++j) if( i != j )
		{
			MPConstraint constr = _solver.makeConstraint(-10000, _instance.mu());
			constr.setCoefficient(r[i], 1);
			constr.setCoefficient(l[j], -1);
			constr.setCoefficient(y[i][j], _instance.mu());
		}
	}
	
	private void createMakespanConstraints()
	{
		for(int i=0; i<_attention.size(); ++i)
		{
			MPConstraint constr = _solver.makeConstraint(-10000, 0);
			constr.setCoefficient(r[i], 1);
			constr.setCoefficient(z, -1);
		}
	}
	
	private void createAntiparallelismConstraints()
	{
		for(int i=0; i<_attention.size(); ++i)
		for(int j=i+1; j<_attention.size(); ++j)
		{
			MPConstraint constr = _solver.makeConstraint(1, 1);
			constr.setCoefficient(y[i][j], 1);
			constr.setCoefficient(y[j][i], 1);
		}
	}
	
	private void createObjective()
	{
		MPObjective obj = _solver.objective();
		obj.setCoefficient(z, 1);
	}
	
	private void solveModel()
	{
		_start = System.currentTimeMillis();
		_status = _solver.solve();
		_time = (System.currentTimeMillis() - _start) / 1000.0;
		_makespan = _status == ResultStatus.OPTIMAL ? z.solutionValue() : Double.MAX_VALUE;
		
		if( _verbose == true )
		{
			System.out.println("Status: " + _status);
			if( _status == ResultStatus.OPTIMAL )
			{
				System.out.println("Makespan: " + z.solutionValue());
				System.out.println();
				
				_makespan = z.solutionValue();
		
				for(int i=0; i<_attention.size(); ++i)
					System.out.println(" - Ship " + i + ": [" + l[i].solutionValue() + ", " + r[i].solutionValue() + "]");
			}

			System.out.println();
		}
	}
	
	private void closeSolver()
	{
		_solver.clear();
	}

	public double makespan()
	{
		return _makespan;
	}
	
	public String status()
	{
		return _status.toString();
	}
	
	public double solvingTime()
	{
		return _time;
	}
}
