import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * This class serves as a GUI for a checkers game,
 * which works with several instances of a class 
 * called Piece to enforce the rules of checkers.
 * 
 * Your task is to finish implementing the processInput
 * method and the Piece class to enforce the rules. 
 * The current behavior of the application allows users 
 * to take turns moving a piece to any available slot. 
 * The rules about legal moves, including capturing pieces, 
 * are not enforced.
 * 
 * The rest of this documentation overviews the code
 * that is here.
 * 
 * Instance variables: The GUI maintains a board (2D array 
 * of Piece objects), current player, and current piece. 
 * 
 * Construction: The Checkers class with a main method 
 * calls the GUI constructor, which initializes ivars 
 * and constructs 12 pieces per player and puts them 
 * on the board at the starting locations. 
 * (See setupPieces() and Piece constructor.) 
 * 
 * Run: When run() begins, various 1-time calls to static  
 * StdDraw methods configure the canvas (See initialize().) 
 * The initial configuration is displayed, and then a loop 
 * controlling the event-driven behavior of the game begins. 
 * When a user clicks the mouse, processInput() and then 
 * drawConfiguration() are called, and then gameOver() is 
 * checked to see whether to terminate the program.
 * 
 * Process input: Called whenever a mouse is clicked, this
 * method translates the click coordinates into board indices.
 * You should modify the if-else block to satisfy the 
 * selecting/moving requirements of the assignment handout. 
 * 
 * Draw configuration: Draws 32 alternating black tiles and  
 * a yellow banner at the top or bottom indicating whose 
 * turn it is, and it iterates through all the pieces on the
 * board calling draw (implemented in the Piece class).
 * 
 * Game over: This method currently just returns false. 
 * Implement it as an extension!
 */
public class CheckersGame {

	/** Display constants */
	private static final int CANVAS_SIZE = 800; // number of pixels
	private static final double BANNER = 0.1; // proportion of tile for border
	private static final int PAUSE_TIME = 16; // milliseconds 

	/** State of the application */
	private final Piece[][] board; 
	private boolean currentPlayer;
	private Piece currentPiece;
	private boolean gameEnded = false;

	/**
	 * Constructor to initialize instance variables.
	 */
	public CheckersGame() {
		// fill board with pieces
		board = new Piece[8][8];
		//setupPieces();
		currentPlayer = true; // dark ("true") player starts
		currentPiece = null; // nothing selected yet
	}

	/**
	 * Initialization of the GUI for the game - Setup GUI canvas, scale, and
	 * double buffering.
	 */
	public void initialize() {
		// Set dimensions (in pixels) for canvas
		StdDraw.setCanvasSize(CANVAS_SIZE, CANVAS_SIZE);
		// Scale canvas coordinate system to easily  
		// translate into array indices (0 to 7).
		// Lower left corner is (-0.1,-0.1)
		StdDraw.setXscale(-BANNER, 8+BANNER);
		StdDraw.setYscale(-BANNER, 8+BANNER);
		StdDraw.enableDoubleBuffering();
	}

	/**
	 * Basic game loop (process input, update, draw, show). This is like the real
	 * main function.
	 */
	public void run() {
		initialize();
		welcomeThenConstructPieces();
		drawConfiguration();
		StdDraw.show();

		while (true) {
			//check for click (mouse down and up without any movement between)
			if(StdDraw.hasNextMouseClicked()) {
				processInput(); // select/deselect/move
				StdDraw.clear();
				drawConfiguration(); // only need to redraw after click
				saveGame();
				StdDraw.show();
				if (gameOver()) break;
			}
			StdDraw.pause(PAUSE_TIME);
		}
	}
	

	public void welcomeThenConstructPieces() {
		StdDraw.setPenColor(StdDraw.GREEN);
		StdDraw.filledRectangle(4, 6, 4, 2-BANNER/2);
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.text(4, 6, "Start new game");
		
		StdDraw.setPenColor(StdDraw.ORANGE);
		StdDraw.filledRectangle(4, 2, 4, 2-BANNER/2);
		StdDraw.setPenColor(StdDraw.WHITE);
		StdDraw.text(4, 2, "Load saved game");
		
		StdDraw.show();
		
		while (!StdDraw.hasNextMouseClicked()) {
			StdDraw.pause(PAUSE_TIME);
		}
		
		if (StdDraw.nextMouseClicked().getY() > 4) {
			setupPieces();
		} else {
			loadSavedGame();
		}
		
		StdDraw.clear();
	}

	public void saveGame() {
		try {
			PrintWriter output = new PrintWriter("game.txt");
			output.println(currentPlayer);
			
			for (int i= 0; i<= 7; i++) {
				for (int j= 0; j<= 7; j ++) {
					if (board[i][j] != null) 
						output.println(board[i][j].toString());
				}
			}
			output.close();
			
		} catch (FileNotFoundException e){
			System.out.println(e);
			System.exit(1);
		}
	}

	public void loadSavedGame() {
		try {
			Scanner load = new Scanner( new File("game.txt") );
			load.nextLine();
			
			while(load.hasNextLine()) {
				boolean player = load.nextBoolean();
				int row = load.nextInt();
				int col = load.nextInt();
				boolean selected = load.nextBoolean();
				boolean capturing = load.nextBoolean();
				boolean king = load.nextBoolean();
				
				board[row][col] = new Piece (player, board, row, col);
				load.nextLine();
			}
			
			load.close();
			
		} catch (FileNotFoundException e){
			System.out.println(e);
			System.exit(1);
		}
		
	}
	
	/**
	 * Constructs new pieces for both teams and stores 
	 * them in the board array at initial locations.
	 * Called once right after initializing canvas.
	 */
	public void setupPieces() {	
		// dark pieces at the bottom
		for (int row=0; row<3; row++) {
			for (int col=row%2; col<8; col+=2) {
				board[row][col] = new Piece(true,board,row,col);
			}
		}
		// light pieces at the top
		for (int row=5; row<8; row++) {
			for (int col=row%2; col<8; col+=2) {
				board[row][col] = new Piece(false,board,row,col);
			}
		}
	}

	/** Reports whether the current player has any moves */
	public boolean gameOver() {
		return gameEnded;
	}

	/**
	 * Process mouse and key presses - user input.
	 * Note how to use mouse here in different ways. Click actions are defined by
	 * a press and release of the left mouse button without moving the mouse in
	 * between. Mouse press events can be used to determine if the left mouse
	 * button is down. Mouse movement can also be tracked without pressing the
	 * button. Keyboard button presses are also shown. Here you write code
	 * corresponding to altering the state of the game based upon these actions.
	 */
	public void processInput() {
		// Get the mouse click and its coordinates
		StdDraw.MouseClick m = StdDraw.nextMouseClicked();
		double x = m.getX(), y = m.getY();

		// translate coordinates and get selected piece
		int row = (int)y, col = (int)x;

		if (row >= 0 && row <= 7 && col >= 0 && col <= 7) {
			int captureCount1 = 0;
			int captureCount2 = 0;
			
			if(captureCount1 == 12 || captureCount2 == 12)
				gameEnded = true;
			
			if (currentPiece == null) {
				// select new piece if it belongs to the correct player
				Piece newPiece = board[row][col];
				if (newPiece != null && newPiece.player == currentPlayer) {
					newPiece.captureCheck();
					newPiece.select();
					currentPiece = newPiece;
				}
			} else {
				if (currentPiece.isValid(row, col)) {
					// move the current piece to the clicked location
					currentPiece.move(row, col);
					// deselect and get ready for next player's turn
					currentPiece.deselect();
					currentPiece = null;
					currentPlayer = !currentPlayer;
				} else if (currentPiece.capturingValid(row, col)) {
					// move the current piece to the clicked location and capture the piece in
					// between
					currentPiece.capturePiece(row, col);
					if (currentPlayer)
						captureCount1 += 1;
					if (!currentPlayer)
						captureCount2 += 1;
					currentPiece.move(row, col);
					currentPiece.captureCheck();
					//move again if capturing is still available
					if (currentPiece.captureAvailable()) {
						currentPiece.move(row, col);
					} else {
						currentPiece.deselect();
						currentPiece = null;
						currentPlayer = !currentPlayer;
					}
				} else {
					currentPiece.deselect();
					currentPiece = null;
				}
			}
		}
	}
	
	/**
	 * Draw the configuration of the pieces.
	 */
	public void drawConfiguration() {
		// draw the tiles of the board
		StdDraw.setPenColor(StdDraw.BLACK);
		for (int x=0; x<8; x++) {
			for (int y=0; y<8; y++) {
				if ((x+y)%2==0)
					StdDraw.filledRectangle(x+0.5,y+0.5,0.5,0.5);
			}
		}

		// draw yellow banner indicating current player
		StdDraw.setPenColor(StdDraw.YELLOW);
		if (currentPlayer) {
			StdDraw.filledRectangle(4, -BANNER/2, 4+BANNER, BANNER/2);
		} else {
			StdDraw.filledRectangle(4, 8+BANNER/2, 4+BANNER, BANNER/2);			
		}

		// tell pieces to draw themselves
		for (int y=0; y<8; y++) {
			for (int x=0; x<8; x++) {
				Piece piece = board[y][x];
				if (piece!=null) {
					piece.draw();
				}
			}
		}
	}
	
}
