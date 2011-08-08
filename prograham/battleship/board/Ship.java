/***
 * Author: Graham Blanshard
 * ---
 * Ship object to represent ships on
 * the grid.
 */
package prograham.battleship.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Ship {

	private String name;
	private int size;
	private List<Coordinate> location;
	private List<Coordinate> hits;
	private boolean isSunk;
	
	/**
	 * Constructor
	 * @param name Ship's name
	 * @param size Ship's size
	 */
	public Ship(String name, int size)
	{
		this.name = name;
		this.size = size;
		location = new ArrayList<Coordinate>();
		hits = new ArrayList<Coordinate>();
		isSunk = false;
	}
	
	/**
	 * Attempts to set the ship position on the given grid using recursive method
	 * @param horz If the ship is placed horizontally or not
	 * @param grid The grid we're attempting to place the ship on
	 * @param x X position we're attempting to place the ship on
	 * @param y Y position we're attempting to place the ship on
	 * @param size The ship's size
	 * @return True if placed, false if not
	 */
	public boolean placeShip(boolean horz, Grid grid, String x, int y, int shipsize)
	{
		if (grid.isFree(x, y) != 0)
			return false;
		
		if (shipsize == 1) {
			location.add(new Coordinate(x,y));
			return true;
		}
		else {
			if (horz) {
				if (placeShip(horz,grid,Coordinate.toAlpha(Coordinate.toInt(x)+1),y,--shipsize)) {
					location.add(new Coordinate(x,y));
					return true;
				}
				else
					return false;
			}
			else {
				y++;
				if (placeShip(horz,grid,x,y,--shipsize)) {
					y--;
					location.add(new Coordinate(x,y));
					return true;
				}
				else
					return false;
			}
		}
	}
	
	/**
	 * Method for firing upon the ship.
	 * @param c Coordinate fired at
	 * @return True or false
	 */
	public boolean fireUpon(Coordinate c)
	{
		if (location.contains(c)) //If the shot is part of this ship's location(s)
		{
			location.remove(c); //Remove it from the list
			hits.add(c); //Add it to the "hits" list
			
			if (location.isEmpty()) //If all sections are hit it is sunk
				isSunk = true;
			
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public List<Coordinate> getCoords()
	{
		List<Coordinate> locations = new ArrayList<Coordinate>();
		locations.addAll(location);
		locations.addAll(hits);
		return locations;
	}

	public boolean sunk()
	{
		return isSunk;
	}
	
	public HashMap<String,String> shipCoordsToGrid()
	{
		HashMap<String,String> placements = new HashMap<String,String>();
		
		for (Coordinate c : location)
			placements.put(c.get(), "*");
		
		return placements;
	}

}
