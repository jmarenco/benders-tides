package tides;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Instance
{
	public int _berths;
	
	public ArrayList<Integer> _attention;
	public ArrayList<Integer> _start;
	public ArrayList<Integer> _end;
	
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
		_attention = new ArrayList<Integer>();
		
		for(int i=1; in.hasNextInt(); ++i)
		{
			if( in.nextInt() != i )
			{
				in.close();
				throw new RuntimeException("File format error: " + file + ", expecting " + i);
			}
			
			_attention.add(in.nextInt());
		}
		
		in.close();
	}
	
	private void readTides(String file) throws Exception
	{
		FileInputStream fis = new FileInputStream(file);
		Scanner in = new Scanner(fis);
		
		_start = new ArrayList<Integer>();
		_end = new ArrayList<Integer>();
		
		for(int i=1; in.hasNextInt(); ++i)
		{
			if( in.nextInt() != i )
			{
				in.close();
				throw new RuntimeException("File format error: " + file + ", expecting " + i);
			}
			
			_start.add(in.nextInt());
			_end.add(in.nextInt());
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
	
	public int attention(int i)
	{
		return _attention.get(i);
	}

	public int start(int t)
	{
		return _start.get(t);
	}

	public int end(int t)
	{
		return _end.get(t);
	}
	
	public int mu()
	{
		return this.end(this.tides()-1);
	}
	
	@Override public String toString()
	{
		String ret = "Att: [" + String.join(", ", _attention.stream().map(i -> Integer.toString(i)).collect(Collectors.toList())) + "]\r\nTides: ";
		
		for(int t=0; t<tides(); ++t)
			ret += "(" + start(t) + "," + end(t) + ") ";
		
		return ret;
	}
}
