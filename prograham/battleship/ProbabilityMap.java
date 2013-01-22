/**
 * @author Graham Blanshard
 * 
 * This data structure is responsible for maintaining
 *   the probabilities of a ship existing in each
 *   square on the grid.
 *   
 * Probability is determined by looking at the remaining
 *   ships and deciding how many different ship
 *   orientations could potentially fit in that location
 *   
 *   e.g. a value of 12 means there is 12 possible
 *        positions for a ship to be placed on the given
 *        coordinate.
 *        
 * https://github.com/GrahamBlanshard/
 */
package prograham.battleship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import prograham.battleship.board.Coordinate;
import prograham.battleship.board.Grid;
import prograham.battleship.board.Ship;

public class ProbabilityMap {
	
	private boolean p,s,d,b,a; //Patrol boat, Submarine, Destroyer, Battleship, Aircraft Carrier
	private int highest;
	private Map<Coordinate,Integer> probabilities;
	private Grid userGrid;
	
	public ProbabilityMap(Grid g) {
		userGrid = g;
		//Ships aren't sunk yet!
		p = true;
		s = true;
		d = true;
		b = true;
		a = true;
		probabilities = new HashMap<Coordinate,Integer>();
		
		assessMap();
	}
	
	/**
	 * Currently borked -- use AssessMap until fixed.
	 */
	public void assessMap()
	{
		int[] sizes = userGrid.getShipSizes(); //List of surviving ships' sizes
		highest = 0;
		
		for (int i = 0; i < userGrid.getGridSize(); i++) {
			for (int j = 0; j < userGrid.getGridSize(); j++) {
				Coordinate coord = new Coordinate(i,j);
				int probability = 0;
				
				if (userGrid.probCheck(coord) == 0) { //Check current location.
					for (int k = 0; j < sizes.length; j++) {
						if (checkDirection(userGrid,sizes[k],i,j,"left"))
							probability++;
						if (checkDirection(userGrid,sizes[k],i,j,"right"))
							probability++;
						if (checkDirection(userGrid,sizes[k],i,j,"up"))
							probability++;
						if (checkDirection(userGrid,sizes[k],i,j,"down"))
							probability++;
					}
					
					//Do we have a new highest?
					if (isEdge(i,j)) {
						probability *= 1.25;
					} else if (isCorner(i,j)) {
						probability *= 1.5;
					}
					
					highest = (probability > highest) ? probability : highest;
					probabilities.put(new Coordinate(Helper.Alphabet[i],j), probability);
				}
			}
		}
	}
	
	/**
	 * Checks a ship's area to see if it occupies an inavlid location
	 * @param userGrid2 - copy of the usergrid we're scanning
	 * @param currentSize - current ship size we're scanning over
	 * @param i - x coord we're seraching
	 * @param j - y coord we're searching
	 * @param direction left/down/up/right -- direction we search in
	 * @return
	 */
	private boolean checkDirection(Grid userGrid2, int currentSize, int i, int j,String direction) {
		//System.out.println("Checking: " + new Coordinate(toAlpha(i),j).get());
		//System.out.println("Current Size: " + currentSize + " direction = " + direction);
		
		//for (int l = 1, adjustedSize = (currentSize-l); adjustedSize >= 0; adjustedSize = (currentSize-l)) {
		if (direction.equals("left")) {
			if (userGrid.probCheck(new Coordinate((i-currentSize-1),j)) != 0)
				return false;
		} else if (direction.equals("right")) {
			if (userGrid.probCheck(new Coordinate((i+currentSize-1),j)) != 0)
				return false;
		} else if (direction.equals("down")) {
			if (userGrid.probCheck(new Coordinate((i),j+currentSize-1)) != 0)
				return false;
		} else if (direction.equals("up")) {
			if (userGrid.probCheck(new Coordinate((i),j-currentSize-1)) != 0)
				return false;
		} else {
			throw new IllegalArgumentException("Incorrect call to checkDirection.");
		}
		//}
		return true;	
	}


	/**
	 * Uses a probability function to map each coordinate on the grid
	 */
	public void AssessMap()
	{
		highest = 0;
		for (int i = 0; i < userGrid.getGridSize(); i++) {
			for (int j = 0; j < userGrid.getGridSize(); j++) {
				//Start at top and work our way around...
				Coordinate coord = new Coordinate(i,j);
				int probability = 0;
				
				if (userGrid.probCheck(coord) == 0) { //Check current location.
					//Check left, up, down, right	
					//Patrol Boat
					if (p) {
						if (userGrid.probCheck(new Coordinate((i-1),j)) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i+1),j)) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i),(j-1))) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i),(j+1))) == 0)
							probability++;
					}
					//Destroyer
					if (d) {
						if (userGrid.probCheck(new Coordinate((i-2),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i-2),j)) == 0	)
							probability++;
						if (userGrid.probCheck(new Coordinate((i+2),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i+2),j)) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i),(j-2))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j-2))) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i),(j+2))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j+2))) == 0)
							probability++;
					}
					//Submarine
					if (s) {
						if (userGrid.probCheck(new Coordinate((i-2),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i-1),j)) == 0	)
							probability++;
						if (userGrid.probCheck(new Coordinate((i+2),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i+1),j)) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i),(j-2))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j-1))) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i),(j+2))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j+1))) == 0)
							probability++;
					}
					//Battleship
					if (b) {
						if (userGrid.probCheck(new Coordinate((i-3),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i-2),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i-1),j)) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i+3),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i+2),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i+1),j)) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i),(j-3))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j-2))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j-1))) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i),(j+3))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j+2))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j+1))) == 0)
							probability++;
					}
					//Aircraft carrier
					if (a) {
						if (userGrid.probCheck(new Coordinate((i-4),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i-3),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i-2),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i-1),j)) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i+4),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i+3),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i+2),j)) == 0 &&
								userGrid.probCheck(new Coordinate((i+1),j)) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i),(j-4))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j-3))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j-2))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j-1))) == 0)
							probability++;
						if (userGrid.probCheck(new Coordinate((i),(j+4))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j+3))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j+2))) == 0 &&
								userGrid.probCheck(new Coordinate((i),(j+1))) == 0)
							probability++;
					}
				}
				//Do we have a new highest?
				if (isEdge(i,j)) {
					probability *= 1.25;
				} else if (isCorner(i,j)) {
					probability *= 1.5;
				}
				
				highest = (probability > highest) ? probability : highest;
				probabilities.put(coord, probability);
			}
		}
	}
	
	/**
	 * Marks a ship sunk and adjusts the probabilities
	 * @param s Ship that has sunk
	 */
	public void sunk(Ship s)
	{
		String name = s.getName();
		
		if (name.equals("Patrol Boat")) {
			p = false;
		} else if (name.equals("Destroyer")) {
			d = false;
		} else if (name.equals("Submarine")) {
			this.s = false;
		} else if (name.equals("Battleship")) {
			b = false;
		} else if (name.equals("Aircraft Carrier")) {
			a = false;
		} else {
			System.err.println("ERROR! Invalid ship name on ProbabilityMap");
		}
		
		assessMap();
	}
	
	/**
	 * Gets a single Coordinate that is deemed best location to fire upon
	 * @return Best location to fire at
	 */
	public Coordinate getBest()
	{
		List<Coordinate> best = new ArrayList<Coordinate>();
		
		for (int x = 0; x < userGrid.getGridSize(); x++) {
			for (int y = 0; y < userGrid.getGridSize(); y++) {
				Coordinate c = new Coordinate(Helper.Alphabet[x],y);
				
				int probability = probabilities.get(c);
				
				if (probability >= highest)
					best.add(c);
			}
		}
			
		if (best.size() == 1)
			return best.get(0);
		else if (best.size() == 0) {
			System.err.println("ERROR");
			return null;
		}
		else {
			Random r = new Random();
			int next = r.nextInt(best.size());
			return best.get(next);
		}
	}
	
	/**
	 * Debug function : prints the probabilities to command line for inspection
	 */
	public void printMap()
	{
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 10; x++) {
				Coordinate c = new Coordinate(x,y);
				System.out.print(probabilities.get(c) + "\t");
			}
			System.out.println();
		}
		System.out.println("-------------------------------------");
	}
	
	private boolean isEdge(int i, int j)
	{
		//Is it along a side but NOT the corner?
		return ((i == 0 || 
				i == userGrid.getGridSize()-1 || 
				j == 0 || 
				j == userGrid.getGridSize()-1)
			&&
			!(i == 0 && j == 0) ||
			(i == 0 && j == userGrid.getGridSize()-1) ||
			(i == userGrid.getGridSize()-1 && j == 0) ||
			(i == userGrid.getGridSize()-1 && j == userGrid.getGridSize()-1));
	}
	
	private boolean isCorner(int i, int j)
	{
		return ((i == 0 && j == 0) ||
				(i == 0 && j == userGrid.getGridSize()-1) ||
				(i == userGrid.getGridSize()-1 && j == 0) ||
				(i == userGrid.getGridSize()-1 && j == userGrid.getGridSize()-1));
	}
}
