package ch.bfh.evoting.votinglib.entities;

public class DatabaseException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public DatabaseException(String message){
		super(message);
	}

}
