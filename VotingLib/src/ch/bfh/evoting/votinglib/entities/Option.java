package ch.bfh.evoting.votinglib.entities;

import java.io.Serializable;

/**
 * Class representing an option that can be chosen for a poll
 * @author Philémon von Bergen
 *
 */
public class Option implements Serializable {

	private String text;
	private int votes;
	private int pollId;
	private int id;
	
	/**
	 * Create an empty Option object
	 */
	public Option(){}
	
	/**
	 * Constructs an Option object
	 * @param text text of the option
	 * @param votes number of votes this option has received
	 * @param id id in the database
	 * @param pollId id of the poll to whom it belongs
	 */
	public Option(String text, int votes, int id, int pollId){
		this.text = text;
		this.votes = votes;
		this.id = id;
		this.pollId = pollId;
	}
	
	/**
	 * Get the text of the option
	 * @return the text of the option
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set text of the option
	 * @param text the text of the option
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Get the number of votes this option has received
	 * @return the number of votes this option has received
	 */
	public int getVotes() {
		return votes;
	}

	/**
	 * Set the number of votes this option has received
	 * @param votes the number of votes this option has received
	 */
	public void setVotes(int votes) {
		this.votes = votes;
	}
	
	/**
	 * Get the id of the poll to whom this option belongs
	 * @return id of the poll to whom it belongs
	 */
	public int getPollId() {
		return pollId;
	}

	/**
	 * Set the id of the poll to whom this options belongs
	 * @param pollId the id of the poll to whom it belongs
	 */
	public void setPollId(int pollId) {
		this.pollId = pollId;
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
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
}
