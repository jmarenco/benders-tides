package tides;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Instance
{
	public int _berths;
	
	public ArrayList<Double> _attention;
	public ArrayList<Double> _start;
	public ArrayList<Double> _end;
	
	public Instance(String shipsFile, String tidesFile)
	{
		try
		{
			readShips(shipsFile);
			readTides(tidesFile);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void readShips(String file) throws Exception
	{
		FileInputStream fis = new FileInputStream(file);
		Scanner in = new Scanner(fis);
		
		_berths = in.nextInt();
		_attention = new ArrayList<Double>();
		
		for(int i=1; in.hasNextInt(); ++i)
		{
			if( in.nextInt() != i )
			{
				in.close();
				throw new RuntimeException("File format error: " + file + ", expecting " + i);
			}
			
			_attention.add(in.nextDouble());
		}
		
		in.close();
	}
	
	private void readTides(String file) throws Exception
	{
		FileInputStream fis = new FileInputStream(file);
		Scanner in = new Scanner(fis);
		
		_start = new ArrayList<Double>();
		_end = new ArrayList<Double>();
		
		for(int i=1; in.hasNextInt(); ++i)
		{
			if( in.nextInt() != i )
			{
				in.close();
				throw new RuntimeException("File format error: " + file + ", expecting " + i);
			}
			
			_start.add(in.nextDouble());
			_end.add(in.nextDouble());
		}
		
		in.close();
	}
	
	public int ships()
	{
		return _attention.size();
	}
	
	public int berths()
	{
		return _berths;
	}
	
	public int tides()
	{
		return _start.size();
	}
	
	public double attention(int i)
	{
		return _attention.get(i);
	}

	public double start(int t)
	{
		return _start.get(t);
	}

	public double end(int t)
	{
		return _end.get(t);
	}
	
	public double mu()
	{
		return this.end(this.tides()-1);
	}
	
	@Override public String toString()
	{
		String ret = "Att: [" + String.join(", ", _attention.stream().map(a -> String.format("%.2f", a)).collect(Collectors.toList())) + "]\r\nTides: ";
		
		for(int t=0; t<tides(); ++t)
			ret += "(" + start(t) + "," + end(t) + ") ";
		
		return ret;
	}
}
