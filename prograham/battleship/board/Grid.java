/**
 * Console based Battleship game
 * Written by: Graham Blanshard
 * Written for: ENSE350 - University of Regina - Winter 2010
 * Additions:   ENSE440 - University of Regina - Something not Winter 2010
 * 				More advanced AI as well as some logic/game fixes in this update
 * 
 * Grid.java
 * This file contains functions for the users to work with the grid objects. 
 * 
 * Available functions:
 * draw 		- Print the grid out for the current player
 * draw(grid) 	- Print out player & opponent's grid
 * getCoord		- Retrieve the value for the given coordinate in HashMap
 * 					Can use both a single coordinate (A5) or separate values (x=A,y=5)
 * isFree		- Checks to see if the given location is free of obstructions (ships)
 * 					and that the selection is within grid boundaries.
 * 					Can use both a single coordinate (A5) or separate values (x=A,y=5)
 * addShip		- Adds a ship to the grid. Takes coordinates, direction and size
 * 					relies on isFree() to determine if the value is appropriate
 * fire			- Fire a shot on the grid. Checks for hit/miss
 * 
 * https://github.com/GrahamBlanshard/
 */

package prograham.battleship.board;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import prograham.battleship.Helper;
import prograham.battleship.player.Player;

public class Grid {

	
	private HashMap<String, String> coords;	
	private Player gridOwner;
	
	private static final int gridSize = 10;
	
	/**
	 * Class constructor, used for default grid size of 10
	 */
	public Grid(Player owner)
	{
		gridOwner = owner;
		coords = new HashMap<String, String>();
	}
	
	/**
	 * Returns the contents of a given coordinate
	 * @param x X value to search
	 * @param y Y value to search
	 * @return XY's contents
	 */
	public String getCoord(String x, Integer y)
	{		
		String fullcoord = x.toUpperCase()+y.toString();
		return coords.get(fullcoord);
	}
	
	/**
	 * Returns the coordinate's contents. Same as above function
	 * @param c Coordinate to search
	 * @return Coordinate's contents
	 */
	public String getCoord(String c)
	{
		String x = c.substring(0,1).toUpperCase();
		Integer y = Integer.parseInt(c.substring(1));
		
		return getCoord(x, y);
	}
	
	public int probCheck(Coordinate coordinate)
	{
		if (coordinate.x().equals("INVALID") || (coordinate.y() < 0) || 
		   (coordinate.y() > gridSize) ||	(coordinate.xint() > gridSize)) {
			return 1;
		}
		
		if(coords.containsKey(coordinate.get())) { 	
			if(coords.get(coordinate.get()).equals("*")) {			
				return 0;
			} else { //Already fired at that location.			
				return 1;
			}
		} else
		
		return 0;
	}
	/**
	 * Returns if the given x & y locations are in bounds and free of any ships
	 * @param x
	 * @param y
	 * @return 2 = out of bounds, 1 = occupied, 0 = free
	 */
	public int isFree(String x, Integer y)
	{
		//Find the location of X in the alphabet array:
		int xloc;
		
		for (xloc = 0; xloc < Helper.Alphabet.length; xloc++)
			if (xloc == Helper.toInt(x))
				break;
		
		//Is it outside the grid's bounds?
		if (xloc < 0 || xloc >= gridSize || y < 0 || y >= gridSize)
			return 2; //Out of bounds	
		else //Selection within bounds, verify its a free spot
			if (coords.containsKey(x+y.toString()))
				return 1; //Not free
			else //FREE!
				return 0;
	}
	
	/**
	 * Checks if the location given is:
	 * 	A) Within bounds
	 *  B) Free of any other ships
	 * @param c	The ship location in coordinate format
	 * @return	True or false
	 */
	public int isFree(String c)
	{
		String x = c.substring(0,1).toUpperCase();
		Integer y = Integer.parseInt(c.substring(1));
		
		return isFree(x, y);
	}
	
	/**
	 * Adds a ship to the grid
	 * @param x	Ship initial X point
	 * @param y	Ship initial Y point
	 * @param dir	True = Horizontal, False = Vertical
	 * @param size	The ship's size
	 * @return True or false - whether the ship was placed successfully
	 */
	public boolean addShip(String x, Integer y, boolean dir, int size, String name)
	{
		return gridOwner.placeShip(name, size, dir, this, x, y);
	}
	
	/**
	 * Adds a ship to the grid. Override of the above function allowing user to input a
	 * single coordinate value (i.e. A4) instead of separate values for X and Y
	 * @param c	The coordinate
	 * @param dir	True = Horizontal, False = Vertical
	 * @param size	The size of the ship being placed
	 * @return	Successful placement or failure
	 */
	public boolean addShip(String c, boolean dir, int size, String name)
	{
		String x = c.substring(0,1).toUpperCase();
		Integer y = Integer.parseInt(c.substring(1));
			
		return addShip(x,y,dir,size,name);
	}
	
	/**
	 * Adds multiple locations to the grid at a time
	 * @param c
	 */
	public void addShip(HashMap<String,String> c)
	{
		coords.putAll(c);
	}
	
	/**
	 * Given a Stack of hits the method checks the ships to see if any are sunk
	 * @param s	The stack of hits
	 * @return	Array of coordinates if a ship is sunken. Null if not.
	 */
	public List<Coordinate> checkShip(Stack<Coordinate> s) {
		Stack<Coordinate> shots = new Stack<Coordinate>();	//Set stack to preserve s
		shots.addAll(s);	//Copy the stack
		
		List<Coordinate> AC = gridOwner.getShip("Aircraft Carrier").getCoords();	//Gather all the points of ships
		List<Coordinate> BS = gridOwner.getShip("Battleship").getCoords();
		List<Coordinate> D  = gridOwner.getShip("Destroyer").getCoords();
		List<Coordinate> SM = gridOwner.getShip("Submarine").getCoords();
		List<Coordinate> PB = gridOwner.getShip("Patrol Boat").getCoords();
		
		int AChits = 0;	//5		Count for hits on AC. Up to 5
		int BShits = 0;	//4		Count for hits on BS
		int Dhits = 0;	//3		Count for hits on C
		int SMhits = 0;	//3		Count for hits on SM
		int PBhits = 0;	//2		Count for hits on PB
		
		for (int i = 0; i < s.size(); i++) {//Repeat for every shot		
			Coordinate shot = shots.pop();
			boolean found = false;
			
			//Check Aircraft carrier	
			if (!gridOwner.getShip("Aircraft Carrier").sunk()) {
				for (int j = 0; j < AC.size(); j++) {						
					if (AC.get(j).equals(shot)) {					
						AChits++;
						found = true;
					}
				}
			}
			//Check battleship			
			if (!gridOwner.getShip("Battleship").sunk()) {
				for (int j = 0; j < BS.size() && !found; j++) { 	
					if (BS.get(j).equals(shot)) {
						BShits++;
						found = true;
					}
				}
			}
			//Check destroyer
			if (!gridOwner.getShip("Destroyer").sunk()) {
				for (int j = 0; j < D.size() && !found; j++) { 
					if (D.get(j).equals(shot)) {
						Dhits++;
						found = true;
					}
				}
			}
			//Check Submarine
			if (!gridOwner.getShip("Submarine").sunk()) {
				for (int j = 0; j < SM.size() && !found; j++) {			
					if (SM.get(j).equals(shot)) {
						SMhits++;
						found = true;
					}
				}
			}
			//Check patrol boat
			if (!gridOwner.getShip("Patrol Boat").sunk()) {
				for (int j = 0; j < PB.size() && !found; j++) {			
					if (PB.get(j).equals(shot)) {
						PBhits++;
						found = true;
					}
				}
			}
		}

		//Count the hits
		if ((!gridOwner.getShip("Aircraft Carrier").sunk()) && AChits == AC.size()) {		
			return AC;
		} else if ((!gridOwner.getShip("Battleship").sunk()) && BShits == BS.size()) {
			return BS;
		} else if ((!gridOwner.getShip("Destroyer").sunk()) && Dhits == D.size()) {
			return D;
		} else if ((!gridOwner.getShip("Submarine").sunk()) && SMhits == SM.size()) {
			return SM;
		} else if ((!gridOwner.getShip("Patrol Boat").sunk()) && PBhits == PB.size()) {
			return PB;
		} else //Nothing sunk
			return null;
	}
	
	/**
	 * Fire upon the grid
	 * @param coordinate	The coordinate to fire upon
	 * @return	0 = repeat shot | 1 = miss | 2 = hit
	 */
	public int fire(Coordinate coordinate) 
	{ 
		if(coords.containsKey(coordinate.get())) { 	
			if(coords.get(coordinate.get()).equals("*")) {			
				coords.put(coordinate.get(), "X");
				return 2;
			} else { //Already fired at that location.			
				return 0;
			}
		} else
			coords.put(coordinate.get(), "O");
		
		return 1;
	}
	
	/**
	 * Returns the size of the grid
	 * @return	size of the grid
	 */
	public int getGridSize()
	{
		return gridSize;
	}
	
	public void gameOver()
	{
		gridOwner.loser();
	}

	public Player getOwner()
	{
		return gridOwner;
	}
	
	/**
	 * Gets an array of all the active ship's sizes
	 * @return an array of each ship's size
	 */
	public int[] getShipSizes()
	{
		List<Ship> ships = gridOwner.getShips();
		int aliveShips = 0;
		int[] sizes = new int[ships.size()];
		
		for (Ship s : ships) {
			sizes[aliveShips] = s.getSize();
			
			if (!s.sunk()) {
				aliveShips++;
			}
		}
		
		int[] finalSizes = new int[aliveShips];
		
		for (int i = 0; i < aliveShips; i++) {
			finalSizes[i] = sizes[i];
		}
	
		return finalSizes;
	}
}