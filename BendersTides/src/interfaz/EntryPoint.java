package interfaz;

import com.google.ortools.Loader;

import tides.Instance;
import tides.BendersSolver;

public class EntryPoint
{
	private static String _version = "0.05";
	
	public static void main(String[] args)
	{
	    Loader.loadNativeLibraries();
	    ArgMap argmap = new ArgMap(args);
	    
	    if( argmap.containsArg("-help") )
	    	showArguments();

	    if( argmap.containsArg("-sm") )
	    	BendersSolver.setMaster(BendersSolver.MasterProblem.Simple);
	    
	    Instance instance = new Instance(argmap.stringArg("-s", ""), argmap.stringArg("-t", ""));
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
		System.out.println("-sm         Simple master subproblem");
	}
}
