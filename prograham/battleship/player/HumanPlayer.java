/**
 * Java Battleship
 * Author: Graham Blanshard
 * --- --- --- --- ---
 * This file facilitates the HumanPlayer functions
 * with the game board. First version of the "fire"
 * method was written with the help of Kendra
 * Cunningham. 
 */
package prograham.battleship.player;

import prograham.battleship.BattleshipGUI;
import prograham.battleship.board.Coordinate;
import prograham.battleship.board.Grid;
import prograham.battleship.board.Ship;

public class HumanPlayer extends Player {

	public HumanPlayer(BattleshipGUI parent) {
		super(parent);
	}

	@Override
	public void fire(Coordinate guess, Grid g) {
		boolean hit = false;
		
		//Check all ships to see if we hit them
		for (Ship s : g.getOwner().getShips()) {
			if (s.fireUpon(guess)) {
				
				if (s.sunk())
					parent.printToPane("\tYou sunk my " + s.getName() + ".\n");
				
				hit = true;
				break;
			}
		}
		
		//We hit one!
		if (hit) {
			boolean allSunk = true;
			
			for (Ship s : g.getOwner().getShips()) {
				if (!s.sunk()) {
					allSunk = false; //One ship isn't sunk!
					break;
				}
			}
			
			if (allSunk) //They're all sunk!
				g.gameOver();
		}
		
	}

	@Override
	public boolean placeShip(String name, int size, boolean isHorizontal,
			Grid parentGrid, String x, Integer y) {
		Ship newShip = new Ship(name,size);
		//Try to place it
		if (newShip.placeShip(isHorizontal, parentGrid, x, y, size)) {
			ships.add(newShip);
			parentGrid.addShip(newShip.shipCoordsToGrid());
			return true;
		}
		else
			return false;
	}
	
	
}
