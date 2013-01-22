/**
 * Author: Graham Blanshard
 * *** *** *** ***
 * This code creates a coordinate value
 * which can be used on the grids
 * 
 *	https://github.com/GrahamBlanshard
 */
package prograham.battleship.board;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import prograham.battleship.Helper;

public class Coordinate {
	
	private String x;
	private Integer y;
	
	/**
	 * Constructor for a random coordinate
	 */
	public Coordinate()
	{
		Random r = new Random();
		this.x = Helper.Alphabet[r.nextInt(10)];
		this.y = r.nextInt(10);
	}
	
	/**
	 * Default constructor
	 * @param x X coordinate (A-J)
	 * @param y Y coordinate (1-10)
	 */
	public Coordinate(String x, Integer y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Coordinate(Integer x, Integer y) {
		this.x = Helper.toAlpha(x);
		this.y = y;
	}
	
	/**
	 * Constructor that takes 1 string (A1) and splits it
	 * @param shot The string coordinate
	 */
	public Coordinate(String shot)
	{
		Map<String, Integer> splitValue = split(shot);
		this.x = splitValue.keySet().toArray()[0].toString();
		this.y = splitValue.get(this.x);
	}
	
	/**
	 * Returns the X value
	 * @return X value
	 */
	public String x()
	{
		return x;
	}
	
	public int xint()
	{
		for (int i = 0; i < Helper.Alphabet.length; i++) {
			if (x.equals(Helper.Alphabet[i])) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Returns the Y value
	 * @return Y value
	 */
	public Integer y()
	{
		return y;
	}
	
	/**
	 * Returns the entire coordinate
	 * @return Entire coordinate, X and Y
	 */
	public String get()
	{
		return (x + y.toString());
	}
	
	/**
	 * Splits the given value into X and Y returning them in a HashMap
	 * @param value Coordinate to split up (i.e. A5)
	 * @return The values split into String and Integer stored in a HashMap ({A=5})
	 */
	public Map<String,Integer> split(String value)
	{
		Map<String, Integer> returnValue = new HashMap<String,Integer>();
		returnValue.put(value.substring(0,1),Integer.parseInt(value.substring(1)));
		
		return returnValue;
	}
	
	@Override public boolean equals(Object c)
	{
		Coordinate _c = null;
		try {	
			_c = (Coordinate)c;
		} catch (ClassCastException ce) {
			return false;
		}
		
		return (_c.x().equals(this.x) && _c.y().equals(this.y));
	}
	
	@Override public int hashCode()
	{
		return (x.hashCode() + y.hashCode());
	}	
}
