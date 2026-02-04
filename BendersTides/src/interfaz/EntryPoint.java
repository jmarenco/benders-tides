package interfaz;

import com.google.ortools.Loader;

import tides.Instance;
import tides.BendersSolver;

public class EntryPoint
{
	private static String _version = "0.06";
	
	public static void main(String[] args)
	{
	    Loader.loadNativeLibraries();
	    ArgMap argmap = new ArgMap(args);
	    
	    if( argmap.containsArg("-help") )
	    	showArguments();

    	BendersSolver.setMaster(argmap.containsArg("-sm") ? BendersSolver.MasterProblem.Simple : BendersSolver.MasterProblem.Stretched);
	    BendersSolver.setTimeLimit(argmap.doubleArg("-time", 3600));
	    
	    Instance instance = new Instance(argmap.stringArg("-s", ""), argmap.stringArg("-t", ""));
	    
	    if( argmap.containsArg("-showinst") )
	    	System.out.println(instance + "\r\n");
	    
	    BendersSolver solver = new BendersSolver(instance);
	    solver.solve();
	}
	
	private static void showArguments()
	{
		System.out.println("BendersTides v" + _version);
		System.out.println();
		System.out.println("-s [s]      Ships .dat file");
		System.out.println("-t [s]      Tides .dat file");
		System.out.println("-time [f]   Time limit in seconds");
		System.out.println("-sm         Simple master subproblem");
		System.out.println("-showinst   Output instance data");
	}
	
	public static String version()
	{
		return _version;
	}
}
