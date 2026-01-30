package interfaz;

import com.google.ortools.Loader;

import tides.Instance;
import tides.BendersSolver;

public class EntryPoint
{
	public static void main(String[] args)
	{
	    Loader.loadNativeLibraries();
	    
	    Instance instance = new Instance("sample-instance/barcos2.dat", "sample-instance/mareas2.dat");
	    System.out.println(instance + "\r\n");
	    
	    BendersSolver solver = new BendersSolver(instance);
	    solver.solve();
	}
}
