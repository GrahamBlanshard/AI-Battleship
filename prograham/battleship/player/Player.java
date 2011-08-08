package prograham.battleship.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import prograham.battleship.BattleshipGUI;
import prograham.battleship.board.Coordinate;
import prograham.battleship.board.Grid;
import prograham.battleship.board.Ship;


public abstract class Player {
	
	/**
	 * Instance variables
	 */
	protected Grid grid;
	protected List<Ship> ships;	
	protected boolean hasLost;
	protected BattleshipGUI parent;
	
	/**
	 * Abstract Methods
	 */
	/**
	 * Make an attack on the Grid
	 * @param guess Location we're making the attack on
	 * @param g Grid we're attacking
	 */
	public abstract void fire(Coordinate guess, Grid g);
	
	/**
	 * Place a ship on the player's grid
	 * @param name The ship's name
	 * @param size The ship's size
	 * @param isHorizontal Is it laying horizontally?
	 * @param parentGrid The grid it is placed upon
	 * @param x Its X coordinate
	 * @param y Its Y coordinate
	 * @return True if successful, false otherwise
	 */
	public abstract boolean placeShip(String name, int size, boolean isHorizontal, 
										Grid parentGrid, String x, Integer y);
	
	
	/**
	 * Public constructor
	 * @param parent
	 */
	public Player(BattleshipGUI parent)
	{
		this.parent = parent;
		hasLost = false;
		grid = new Grid(this);
		ships = new ArrayList<Ship>(5);
	}
	
	/**
	 * Get the Player's Grid
	 * @return Reference to the Grid object owned by this Player
	 */
	public Grid grid() { 
		return grid;
	}
	
	/**
	 * Find a single ship given its name
	 * @param name Name of the ship to find
	 * @return The Ship object if it exists, null otherwise
	 */
	public Ship getShip(String name)
	{
		for (Ship s : ships) {
			if (s.getName().equals(name))
				return s;
		}
		
		return null;
	}
	
	/**
	 * Gets a list of Ships on this grid
	 * @return Ships on this grid
	 */
	public List<Ship> getShips()
	{
		return ships;
	}
	
	/**
	 * Determine if the player has lost
	 * @return True if lost
	 */
	public boolean lost() 
	{ 
		return hasLost;
	}
	
	/**
	 * Set this player as a loser
	 */
	public void loser()
	{
		hasLost = true;
	}
	
	/**
	 * Fetches the coordinates of all ships placed on this Grid
	 * @return Coordinate map of all ship locations
	 */
	public HashMap<String,String> getAllCoords()
	{
		HashMap<String,String> placements = new HashMap<String,String>();
		
		for (Ship s : ships)
			placements.putAll(s.shipCoordsToGrid());
		
		return placements;
	}
}
