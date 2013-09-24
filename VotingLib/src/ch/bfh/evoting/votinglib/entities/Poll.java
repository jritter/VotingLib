package ch.bfh.evoting.votinglib.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Class representing a poll
 * @author Philémon von Bergen
 *
 */
public class Poll implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String question;
	private List<Option> options;
	private List<Participant> participants;
	private long startTime;
	private boolean isTerminated;
	
	/**
	 * Construct an empty Poll object
	 */
	public Poll(){}
	
	/**
	 * Constructs a poll object
	 * @param id id defined in the database
	 * @param question text of the poll's question
	 * @param startTime time when the poll has begun
	 * @param options list of the possible options for the voter
	 * @param isTerminated true if Poll is terminated
	 */
	public Poll(int id, String question, long startTime, List<Option> options, boolean isTerminated){
		this.id = id;
		this.question = question;
		this.startTime = startTime;
		this.options = options;
		this.isTerminated = isTerminated;
	}
	
	/**
	 * Get the id in the database
	 * @return the id in the database
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the id in the database
	 * @param id the id in the database
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the poll's question
	 * @return the poll's question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * Set the poll's question
	 * @param question the poll's question
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * Get the list of options for the voter
	 * @return list of Option objects
	 */
	public List<Option> getOptions() {
		return options;
	}

	/**
	 * Set the list of options for the voter
	 * @param options list of Option objects
	 */
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	
	/**
	 * Get the participants to the poll
	 * @return a list of participant objects
	 */
	public List<Participant> getParticipants() {
		return participants;
	}

	/**
	 * Set the participants to the poll. 
	 * Be careful, the participants are not stored in the DB since they are dependent on the network
	 * @param participants a list of participant objects
	 */
	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}

	/**
	 * Get the time when the poll has begun
	 * @return the time when the poll has begun in milliseconds
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * Set the time when the poll has begun
	 * @param startTime the time when the poll has begun in milliseconds
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	/**
	 * Ask if poll is terminated
	 * @return true if Poll is terminated else otherwise
	 */
	public boolean isTerminated() {
		return isTerminated;
	}

	/**
	 * Set if poll is terminated
	 * @param isTerminated true if Poll is terminated else otherwise
	 */
	public void setTerminated(boolean isTerminated) {
		this.isTerminated = isTerminated;
	}
	
}
