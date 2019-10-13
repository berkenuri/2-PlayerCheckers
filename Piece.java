
/**
 * This class encapsulates the core data and functionality 
 * of the main objects in a checkers game -- the pieces.
 * Each piece keeps track of the player it belongs to, its
 * location, and whether it is selected, capturing, or crowned.
 * Each piece can also see the board instance variable 
 * maintained by the GUI to check for move validity. 
 * A piece knows how to draw itself (implemented for you)
 * and toggle its selected boolean. 
 * 
 * Your main task with this class is to implement a move method
 * that respects the rules of the game. You'll probably want
 * to implement other public and private methods along the way.
 */
public class Piece {
	// initialized by constructor and never changed
	public final boolean player; // true="dark" team (red)
	private final Piece[][] board;

	// state variables
	private int row;
	private int col;
	private boolean selected;
	private boolean capturing;
	private boolean king;
	private boolean validity;

	/**
	 * Construct a new piece object with specified player identifier,
	 * board, location, and default starting states.
	 * @param player
	 * @param board
	 * @param row
	 * @param col
	 */
	public Piece(boolean player, Piece[][] board, int row, int col) {
		this.player = player;
		this.board = board;
		this.row = row;
		this.col = col;
		selected = false;
		capturing = false;
		king = false;
	}

	/**
	 * Draw correctly-colored circle at correct coordinates.
	 * Circle should be outlined in yellow if selected 
	 * or magenta if jumping.
	 */
	public void draw() {
		double y = row+.5, x = col+.5; // add .5 to center
		if (selected) { // draw slightly larger yellow circle underneath
			StdDraw.setPenColor(StdDraw.YELLOW);
			if (capturing) StdDraw.setPenColor(StdDraw.MAGENTA);
			StdDraw.filledCircle(x,y,.45);						
		}
		// draw a circle at the correct place with correct color
		StdDraw.setPenColor(player?StdDraw.RED:StdDraw.LIGHT_GRAY);
		StdDraw.filledCircle(x,y,.4);

		// draw image over kings
		if (king) 
			StdDraw.picture(x,y,"crown.png",.5,.5);

	}
	
	public String toString() {
		return this.player + " " + this.row + " " + this.col + " " + selected + " " +
				capturing + " " + king ;
	}
	
	
	/** 
	 * Stub method for moving:  check whether the target
	 * spot is available, and if so, update location and
	 * possibly become a king.   
	 */
	public void move(int newRow, int newCol) {
		if (board[newRow][newCol] == null) {
			// move from current to new spot on the board
			board[row][col] = null;
			board[newRow][newCol] = this;
			// update location instance variables
			row = newRow;
			col = newCol;
			// become king if reach far side
			if (newRow==(player?7:0)) 
				king = true;
		}  
	
	}

	//checks if the user clicked a valid place for a basic move
	public boolean isValid(int newRow, int newCol) {
		validity = false;
		
		if (!king) {
			if ( (newRow == (player ? row+1 : row-1)) && ((newCol == col+1) || newCol == col-1)
					&& (board[newRow][newCol] == null) )
				validity = true;
		} else {
			if ( (newRow == row+1 || newRow == row-1) && (newCol == col+1 || newCol == col-1) 
					&& (board[newRow][newCol] == null) )
				validity = true;
		}
		
		return validity;
	}
	
	//checks if the user clicked a valid place for a capture
	public boolean capturingValid(int newRow, int newCol) {
		if (!king) {
			if ( (newRow == (player ? row + 2 : row - 2)) && ((newCol == col + 2) || newCol == col - 2)
					&& (board[newRow][newCol] == null) && capturing == true )
				return true;
			else
				return false;
		} else {
			if ( (newRow == row+2 || newRow == row-2) && (newCol == col+2 || newCol == col-2)
					&& board[newRow][newCol] == null && capturing == true)
				return true;
			else
				return false;
		}
	}
	
	//captures the piece in between
	public void capturePiece(int newRow, int newCol) {
		board[ (row+newRow)/2 ][ (col+newCol)/2 ] = null;
	}
	
	//checks if capturing is true or not, this will be used for multiple capturing condition
	public boolean captureAvailable() {
		if (capturing)
		return true;
		
		return false;
	}
	
	//checks if capturing is available ans changes the capturing value before the user clicks to the new location 
	public void captureCheck() {	
		capturing = false;
	
		if (!king) {
			if (player) {
				if (row < 6) {
					if (col < 2) {
						if (CaptureCheckUpperRight())
							capturing = true;
					} else if (col > 5) {
						if (CaptureCheckUpperLeft())
							capturing = true;
					} else {
						if (CaptureCheckUpperRight() || CaptureCheckUpperLeft())
							capturing = true;
					}
				}
			} else {
				if (row > 1) {
					if (col < 2) {
						if (CaptureCheckLowerRight())
							capturing = true;
					} else if (col > 5) {
						if (CaptureCheckLowerLeft())
							capturing = true;
					} else {
						if (CaptureCheckLowerRight() || CaptureCheckLowerLeft())
							capturing = true;
					}
				}
			}
		} else {
			if (row > 5) {
				if (col < 2) {
					if (CaptureCheckLowerRight())
						capturing = true;
				} else if (col > 5) {
					if (CaptureCheckLowerLeft())
						capturing = true;
				} else {
					if (CaptureCheckLowerRight() || CaptureCheckLowerLeft())
						capturing = true;
				}			
			} else if (row < 2) {
				if (col < 2) {
					if (CaptureCheckUpperRight())
						capturing = true;
				} else if (col > 5) {
					if (CaptureCheckUpperLeft())
						capturing = true;
				} else {
					if (CaptureCheckUpperRight() || CaptureCheckUpperLeft())
						capturing = true;
				}
			} else {
				if (col < 2) {
					if (CaptureCheckUpperRight() || CaptureCheckLowerRight())
						capturing = true;
				} else if (col > 5) {
					if (CaptureCheckUpperLeft() || CaptureCheckLowerLeft())
						capturing = true;
				} else {
					if (CaptureCheckUpperRight() || CaptureCheckLowerRight() ||
							CaptureCheckUpperLeft() || CaptureCheckLowerLeft())
						capturing = true;
				}	
			}
		}
		
	}
	
	//helper methods
	public boolean CaptureCheckUpperRight() {
		if (board[row + 1][col + 1] != null && board[row + 2][col + 2] == null && 
				player != board[row + 1][col + 1].player)
		return true;
		
		return false;
	}
	
	public boolean CaptureCheckUpperLeft() {
		if (board[row + 1][col - 1] != null && board[row + 2][col - 2] == null && 
				player != board[row + 1][col - 1].player)
		return true;
		
		return false;
	}
	
	public boolean CaptureCheckLowerRight() {
		if (board[row - 1][col + 1] != null && board[row - 2][col + 2] == null && 
				player != board[row - 1][col + 1].player)
		return true;
		
		return false;
	}
	
	public boolean CaptureCheckLowerLeft() {
		if (board[row - 1][col - 1] != null && board[row - 2][col - 2] == null && 
				player != board[row - 1][col - 1].player)
		return true;
		
		return false;
	}
	
	/** Select the piece */
	public void select() {
		selected = true;
	}

	/** Deselect the piece */
	public void deselect() {
		selected = false;
	}

}
