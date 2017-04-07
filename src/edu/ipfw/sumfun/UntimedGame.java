package edu.ipfw.sumfun;

import java.util.ArrayList;
import java.util.Observable;

/**
 * The UntimedGame class controls the untimed version of the game
 * @author Jake
 */
public class UntimedGame extends Observable {//start UntimedGame class
	
	private int movesRemaining;//counter of number of moves remaining, decrements with each successful move
	private ArrayList<TileModel> tileQueue = new ArrayList<>();//holds the queue of tiles
	private GameBoard gameBoard;//handles details of board state
	private int points;//holds the player's score
	private static final int LOW_THRESHOLD = 0;//the lowest number that be randomly generated
	private static final int HIGH_THRESHOLD = 9;//the highest number that be randomly generated
	private static final int MAX_MOVES = 50;//the max moves allowed for a game
	private static final int INITIAL_POINTS = 0;//the number of points the user starts with
	private static final int QUEUE_LENGTH = 5;//the length of the queue
	private static final int MIN_NUM_TILES_TO_SCORE = 3;//the minimum number of tiles removed need to score points
	private static final int TILE_SCORE_VALUE = 10;//the value given per tile when points are scored
	private static final int MOD_VALUE = 10;//the modulus value to calculate with
	
	/**
	 * Constructor method for UntimedGame class
	 */
	public UntimedGame() {
		movesRemaining = MAX_MOVES;
		points = INITIAL_POINTS;
		gameBoard = new GameBoard();
		populateQueue();
	}//end UntimedGame constructor method
	
	/**
	 * Access method for field points
	 * @return points, a member field containing the user's score
	 */
	public int getPoints() {
		return points;
	}//end getPoints
	
	/**
	 * Access method for field movesRemaining
	 * @return movesRemaining, a member field 
	 */
	public int getMovesRemaining() {
		return movesRemaining;
	}//end getMovesRemaining
	
	/*
	 * Returns the head of the queue
	 * @param i - the index of the tile
	 * @returns TileModel - the tile selected
	 */
	public TileModel selectQueueTile(int i){//start selectQueueTile method
		return tileQueue.get(i);
	}//start selectQueueTile method
	
	/**
	 * Places a tile at the given location by simply replacing the value
	 * of the current tile with the value of the selected tile
	 * @param i,j - the row and column values of where the tile is placed
	 */
	public void placeTile(int i, int j){//start placeTile method
		gameBoard.setTile(i, j, tileQueue.get(0).getValue());
		calculateSum(i, j, tileQueue.get(0).getValue());
		setChanged();
		notifyObservers();
	}//end placeTile method
	
	/**
	 * Populates the tileQueue with some values
	 */
	public void populateQueue(){//initializes tileQueue with random values in the allowed range (inclusive)
		int num;
		for(int i = 0; i < QUEUE_LENGTH ; i++){
			num = LOW_THRESHOLD + (int) (Math.random() * ((HIGH_THRESHOLD - LOW_THRESHOLD) + 1));
			TileModel t = new TileModel(i - 1);
			t.setValue(num);
			tileQueue.add(t);
		}
		setChanged();
		notifyObservers();
	}//end populateQueue
	/*
	 * Adds a new tile to the queue after one has been removed
	 */
	public void pushQueue(){//start pushQueue start
		TileModel t;//the tile to be added to the queue
		tileQueue.remove(0);
		int num = LOW_THRESHOLD + (int) (Math.random() * ((HIGH_THRESHOLD - LOW_THRESHOLD) + 1));
		t = new TileModel(num);
		tileQueue.add(t);
		setChanged();
		notifyObservers();
		//this method is assuming that the GUI will automatically update itself with the queue methods. If it doesn't, this method will have to adjust. It has been tested with printouts, and does work, however.
	}//end pushQueue method
	
	/**
	 * Calculates the sum of the tiles around the one at the given coordinates, and compares it to the given queue value, mod
	 * @param i the row of the clicked tile
	 * @param j the column of the clicked tile
	 * @param mod the queue value placed
	 */
	public void calculateSum(int i, int j, int mod){//start calculateSum method
		
		//decrement movesRemaining
		movesRemaining--;
		
		//Retrieval of tile referenced by i and j
		TileModel t = gameBoard.getTile(i, j);
		
		//Running total of neighboring tile values
		int total = 0;
		
		//Running count of neighboring tiles for point value (x10)
		int score = 0;
		
		//Checking all potential neighbors (omitting with value -1 so they don't mess up the total)
		if(t.getEast() != null && t.getEast().getValue()!=-1){
			total += t.getEast().getValue();
			score++;
		}
		if(t.getWest() != null && t.getWest().getValue()!=-1){
			total += t.getWest().getValue();
			score++;
		}
		if(t.getNorth() != null && t.getNorth().getValue()!=-1){
			total += t.getNorth().getValue();
			score++;
		}
		if(t.getSouth() != null && t.getSouth().getValue()!=-1){
			total += t.getSouth().getValue();
			score++;
		}
		if(t.getNorthEast() != null && t.getNorthEast().getValue()!=-1){
			total += t.getNorthEast().getValue();
			score++;
		}
		if(t.getSouthEast() != null && t.getSouthEast().getValue()!=-1){
			total += t.getSouthEast().getValue();
			score++;
		}
		if(t.getNorthWest() != null && t.getNorthWest().getValue()!=-1){
			total += t.getNorthWest().getValue();
			score++;
		}
		if(t.getSouthWest() != null && t.getSouthWest().getValue()!=-1){
			total += t.getSouthWest().getValue();
			score++;
		}
		
		//If the MIN_NUM_TILES_TO_SCORE or more tiles have been removed, calculate score
		//Otherwise, no score awarded
		if(score >= MIN_NUM_TILES_TO_SCORE) {
			score = score * TILE_SCORE_VALUE;
		} else {
			score = 0;
		}
		
		//Check if value of placed tile = total neighboring tile values modulo MOD_VALUE
		if((total % MOD_VALUE) == mod){
			
			//Update points with new score
			this.points += score;
			
			//Update tile and adjacent tiles
			removeAdjacentTiles(t);
			
			//Test print statement
			//System.out.println("Move successful");
			
			//Make calls to notifyObservers, etc
		}	
		
		setChanged();
		notifyObservers();
		
	}//end calculateSum method
	
	/**
	 * set value of current tile and all adjacent tiles to -1
	 * @param t, the tile that has been placed
	 */
	private void removeAdjacentTiles(TileModel t) {
		//Set value of placed tile to -1
		t.setValue(-1);
		
		//Set all adjacent tiles to -1, if they exist
		//checking all potential neighbors
		if(t.getEast() != null){
			t.getEast().setValue(-1);
		}
		if(t.getWest() != null){
			t.getWest().setValue(-1);
		}
		if(t.getNorth() != null){
			t.getNorth().setValue(-1);
		}
		if(t.getSouth() != null){
			t.getSouth().setValue(-1);
		}
		if(t.getNorthEast() != null){
			t.getNorthEast().setValue(-1);
		}
		if(t.getSouthEast() != null){
			t.getSouthEast().setValue(-1);
		}
		if(t.getNorthWest() != null){
			t.getNorthWest().setValue(-1);
		}
		if(t.getSouthWest() != null){
			t.getSouthWest().setValue(-1);
		}
	}//end updateTiles
	
	/**
	 * Accessor method for gameBoard
	 * @return gameBoard
	 */
	public GameBoard getGameBoard(){//start getGameBoard method
		return gameBoard;
	}//end getGameBoard method
	
	/**
	 * Decrement movesRemaining by 1
	 */
	public void decrementMoves() {
		System.out.println("decrementMoves called");
		movesRemaining--;
		setChanged();
		notifyObservers();
	}//end decrementMoves
	
}//end UntimedGame class