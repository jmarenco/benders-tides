package interfaz;

import com.google.ortools.Loader;

import tides.Instance;
import tides.Master;

public class EntryPoint
{
	public static void main(String[] args)
	{
	    Loader.loadNativeLibraries();
	    
	    Instance instance = new Instance("sample-instance/ships.dat", "sample-instance/tides.dat");
	    Master master = new Master(instance);
	    master.solve();
	}
}
