package tides;

public class BendersSolver
{
	private Instance _instance;
	private Master _master;
	
	public BendersSolver(Instance instance)
	{
		_instance = instance;
		_master = new Master(instance);
	}
	
	public void solve()
	{
		_master.solve();
		
		for(int i=0; i<_instance.berths(); ++i)
			subproblem(i).solve();
	}
	
	private Subproblem subproblem(int berth)
	{
		Subproblem ret = new Subproblem(_instance);
		
		for(int i=0; i<_instance.ships(); ++i) if( _master.berth(i) == berth )
			ret.addShip(_instance.attention(i));
		
		return ret;
	}

}
