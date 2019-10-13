/**
 * This simple class creates an instance of a checkers game and 
 * launches it by calling its run method.
 */
public class Checkers {
	public static void main(String[] args) {
		CheckersGame g = new CheckersGame(); //Make a new application
		g.run(); //Run the application
		System.exit(0);          //Clean exit after finished execution
	}

}
