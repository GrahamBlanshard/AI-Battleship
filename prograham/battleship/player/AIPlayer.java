package prograham.battleship.player;

/**
 * Written By Graham Blanshard
 * https://github.com/GrahamBlanshard/
 * 
 * AI Player for the single player Java Battleship
 */

import java.util.List;
import java.util.Random;
import java.util.Stack;

import prograham.battleship.BattleshipGUI;
import prograham.battleship.Helper;
import prograham.battleship.ProbabilityMap;
import prograham.battleship.board.Coordinate;
import prograham.battleship.board.Grid;
import prograham.battleship.board.Ship;

public class AIPlayer extends Player {

	private Coordinate lastShot;
	private Stack<Coordinate> previousShots;
	private static ProbabilityMap AI;
	
	public AIPlayer(BattleshipGUI parent) {
		super(parent); 		
		AI = null;
		previousShots = new Stack<Coordinate>();
	    String[][] Listships = { 
	    		{"Aircraft Carrier", "5"},
				{"Battleship", "4"},
				{"Destroyer", "3"}, 
				{"Submarine", "3"}, 
				{"Patrol Boat", "2"} 
	    };
				
		for (int i = 0; i < Listships.length; i++)
			ships.add(new Ship(Listships[i][0],Integer.parseInt(Listships[i][1])));
	}
	
	public Coordinate getLastShot()
    {
        return lastShot;
    }
	
	/**
	 * Places the ships on the grid using a random number generation
	 */
	public void placeShip()
	{	
		Random generator = new Random();	//Used to simulate computer "guessing"
		Boolean successful = false;			
		boolean direction;
		
		// Iterate through ships and place them
		for (Ship c : ships) {
			do {
				Integer orientation = generator.nextInt(2);
	            if(orientation == 0)
	            	direction = true; // Horizontal
	            else
	            	direction = false; // Vertical
            
	            Coordinate shipSpot = new Coordinate(generator.nextInt(grid.getGridSize()),generator.nextInt(grid.getGridSize()));	            	            
			
				successful = c.placeShip(direction, grid, shipSpot.x(), shipSpot.y(), c.getSize());
				grid.addShip(c.shipCoordsToGrid());						
			} while(!successful);
		}
	}
	
	/**
	 * Creates and assigns the probability map
	 * @param g Grid to create the probability map for
	 */
	public void setMap(Grid g) {
		AI = new ProbabilityMap(g);
	}
	
	/**
	 * Firing algorithm for the AI
	 * @param g Grid to fire upon
	 * @return True or false if hit was a success
	 */
	public boolean AIFire(Grid g)
	{
		if (AI == null)
			setMap(g);
		
		int validShot = 0; 		//0 = Bad shot (Repeated shot), 1 = miss, 2 = hit
		Coordinate guess = null;
		Random generator = new Random();	//Simulates "guessing"		
		boolean swapped = false;	//If the path the AI is firing down needs to be reversed

		if (previousShots.isEmpty()) {
			do {			
				AI.printMap(); //Provide a visual printout
				guess = AI.getBest();//new Coordinate(Alphabet[generator.nextInt(g.getGridSize())],generator.nextInt(g.getGridSize()));
				validShot = g.fire(guess);		//Shoots!
				System.out.println("\t"+guess.get());
			}while(validShot == 0); //Repeat the process if the random selected a repeated shot
		} else { //We've already scored a hit. Shoot "intelligently"	
			if(previousShots.size() > 1) { //We've scored at least 2 hits already
				do  { //Loop until we fire at an open location
					boolean direction; 	//Hit in same direction as before		
					//Grab the last two hits to construct a 'path'
					Coordinate lastShot = previousShots.pop();
					Coordinate secondLastShot = previousShots.peek();
					previousShots.push(lastShot);
		
					//Grab X & Y for each shot
					String x1 = lastShot.x();
					String x2 = secondLastShot.x();
					Integer y1 = lastShot.y();
					Integer y2 = secondLastShot.y();
		
					//Check what direction the last shot was...
					if (x1.equals(x2)) {//We know it is vertical					
						direction = true;
						if (y1 > y2) { //Down
							if ((y1+1) < g.getGridSize()) { //Ensure we're within grid bounds
								guess = new Coordinate(x1,(y1+1));
								validShot = g.fire(guess);								
							}
						} else {//Up
							if ((y1-1) >= 0) {
								guess = new Coordinate(x1,(y1-1));
								validShot = g.fire(guess);
							}						
						}			
					} else { //We know it is horizontal		
						direction = false;
						int xloc1 = Helper.toInt(x1,g.getGridSize());	//We need to convert the X coordinates to integers
						int xloc2 = Helper.toInt(x2,g.getGridSize());
			
						if (xloc1 > xloc2) { //Right		
							if ((xloc1+1) < g.getGridSize()) {
								guess = new Coordinate(xloc1+1,y1);
								validShot = g.fire(guess);
							}	
						} else { //Left			
							if ((xloc1-1) >= 0) {				
								guess = new Coordinate(xloc1-1,y1);
								validShot = g.fire(guess);
							}
						}						
					}
					//Shot was tried and failed. (Hit a boundary or a miss) 
					//Reverse list and retry
					if (validShot == 0) {
						if (!swapped) { //We only want to reverse the list once
							//We need to reverse the stack
							previousShots = reverseStack(previousShots);
							swapped = true;
						} else { //We've already swapped the list once
							//Decide new direction: Left/Down or Up/Right
							int LDorUR = generator.nextInt(2); 
							previousShots = reverseStack(previousShots);
				
							//Vertical guesses failed. 
							//Switch to a horizontal path from last known hit
							if(direction) {
								do {					
									int xloc = Helper.toInt(x1,g.getGridSize());											
									if(LDorUR == 0) { //Go Left
										if((xloc-1) < 0) {
											validShot = 0;
											LDorUR = 1;
										} else { //Left was invalid, go right instead
											guess = new Coordinate(xloc-1,y1);
											validShot = g.fire(guess);
											if (validShot == 0) //That was an invalid guess!
												LDorUR = 1;
										}
									} else { //Go right						
										if ((xloc+1) >= g.getGridSize()) {
											validShot = 0;
											LDorUR = 0;
										} else {//Right failed, go left							
											guess = new Coordinate(xloc+1,y1);
											validShot = g.fire(guess);
											if (validShot == 0) //That was an invalid guess!
												LDorUR = 0;
										}
									}
								} while(validShot == 0);
							} else { //Horizontal												
								do {					
									if(LDorUR == 0) { //Go Down						
										if ((y1+1) >= g.getGridSize()) {
											validShot = 0;
											LDorUR = 1;
										} else {
											guess = new Coordinate(x1,(y1+1));
											validShot = g.fire(guess);
											if (validShot == 0) //That was an invalid guess!
												LDorUR = 1;
										}
									} else { //Go Right						
										if ((y1-1) < 0) {
											validShot = 0;
											LDorUR = 0;
										} else {
											guess = new Coordinate(x1,(y1-1));
											validShot = g.fire(guess);
											if (validShot == 0) //That was an invalid guess!
												LDorUR = 0;
										}
									}										
								} while(validShot == 0);
							}
						}
					}					
				}while(validShot == 0);
			} else {  //We only have one valid hit stored
				Coordinate lastShot = previousShots.peek();
				do { // Try new direction
					Integer newDir = generator.nextInt(4);
					int xloc = 0;
					Integer y = 0;
		
					switch(newDir) { 		
					case 0: // Left
						xloc = Helper.toInt(lastShot.x(),g.getGridSize());
			
						if ((xloc-1) >= 0) { //Is in bounds, set new X			
							guess = new Coordinate(xloc-1,lastShot.y());
							validShot = g.fire(guess);								 
						}																				
						break;
					case 1: // Right
						xloc = Helper.toInt(lastShot.x(),g.getGridSize());
			
						if ((xloc+1) < g.getGridSize()) {//Is in bounds, set new X
							guess = new Coordinate(xloc+1,lastShot.y());
							validShot = g.fire(guess);							 
						}		
						break;
					case 2: // Up
						y = lastShot.y();
						if ((y-1) >= 0) {//Is in bounds, set new Y
							guess = new Coordinate(lastShot.x(), (y-1));
							validShot = g.fire(guess);							 
						}
						break;
					case 3: // Down
						y = lastShot.y();
						if ((y+1) < g.getGridSize()) { //Is in bounds, set new Y
							guess = new Coordinate(lastShot.x(),(y+1));
							validShot = g.fire(guess);							 
						}
						break;
					}					
				} while(validShot == 0);
			}
		}
		AI.assessMap();
		//Our shot was taken successfully
		return validateGuess(guess,g,validShot);
	}
	
	/**
	 * Checks the status of the shot placed
	 * @param guess location of the guess
	 * @param g grid guess was placed upon
	 * @param validShot if the shot was valid or not
	 * @return false if a miss, true if a hit
	 */
	private boolean validateGuess(Coordinate guess, Grid g, int validShot)
	{
		lastShot = guess;
		
		if(validShot == 1) //Miss
			return false;
		else {//Hit	
			previousShots.push(guess); //Add the shot to our stack
			//Check if it sunk a ship. If it did the shots involved in the 'sinking' are returned
			List<Coordinate> returnArray = g.checkShip(previousShots);
			
			if(returnArray != null) { //A ship was sunk
				int loopSize = previousShots.size(); //Loop for every shot in our stack
				Stack<Coordinate> temp = new Stack<Coordinate>();
				
				for (int i = 0; i < loopSize; i++) {		
					Coordinate comp = previousShots.pop(); //Pop it off
					boolean found = false;
					//Check if the shot was involved in the sinking		
					for (int j = 0; j < returnArray.size(); j++) {
						if (returnArray.get(j).equals(comp)) {
							found = true;
							break;
						}
					}
					if (!found) //If it wasn't then store it in our temporary stack
						temp.push(comp);					
				}		
				previousShots = temp; //Assign all the previous shots back to the parent stack
			}
			markShots(guess, g);
			return true;
		}
	}
	
	/**
	 * Marks the ships coordinates as hit or not
	 * @param guessP Coordiante we'll mark
	 * @param g Grid ships exist on
	 */
	private void markShots(Coordinate guessP, Grid g)
	{
		Coordinate guess = guessP;
		//Check all ships to see if we hit them
		for (Ship s : g.getOwner().getShips()) {
			if (s.fireUpon(guess)) {
				if (s.sunk()) {
					AI.sunk(s);
					parent.printToPane("\tI sunk your " + s.getName() + ".\n");
				}
				break;
			}
		}
		boolean allSunk = true;
		for (Ship s : g.getOwner().getShips()) {
			if (!s.sunk()) {
				allSunk = false; //One ship isn't sunk!
				break;
			}
		}
		
		if (allSunk) { //They're all sunk!
			g.gameOver();
		}
	}
	
	/**
	 * Reverses the stack object
	 * @param s Stack to reverse
	 * @return Reversed stack
	 */
	private Stack<Coordinate> reverseStack(Stack<Coordinate> s)
	{
		//We need to reverse the stack
		Stack<Coordinate> temp = new Stack<Coordinate>();	

		while(!s.isEmpty()) //Reverse list				
			temp.push(s.pop());
		
		return temp;
		
	}

	/**
	 * Wrapper method to fire
	 */
	@Override
	public void fire(Coordinate guess, Grid g) {
		//Uninstantiated in the AI version
	}

	@Override
	public boolean placeShip(String name, int size, boolean isHorizontal,
			Grid parentGrid, String x, Integer y) {
		//Uninstantiated in the AI version.... currently
		return false;
	}
}
