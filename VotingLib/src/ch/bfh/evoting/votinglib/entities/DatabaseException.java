package ch.bfh.evoting.votinglib.entities;

/**
 * Exception thrown by an error in the Database 
 * @author Philémon von Bergen
 *
 */
public class DatabaseException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public DatabaseException(String message){
		super(message);
	}

}
