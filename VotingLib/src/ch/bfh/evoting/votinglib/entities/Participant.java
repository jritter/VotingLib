package ch.bfh.evoting.votinglib.entities;

import java.io.Serializable;

/**
 * Class representing a participant to the poll
 * @author Philémon von Bergen
 *
 */
public class Participant implements Serializable{

	private static final long serialVersionUID = 1L;
	private String identification;
	private String ipAddress;
	private boolean hasVoted;
	private boolean isSelected;
	
	/**
	 * Construct a Participant object
	 * @param identification the identification of the participant
	 * @param ipAddress the IP address of the participant
	 * @param isSelected indicate if the participant in the network is selected as member of the electorate
	 * @param hasVoted indicated if the participant already has submitted a vote
	 */
	public Participant(String identification, String ipAddress, boolean isSelected, boolean hasVoted){
		this.identification = identification;
		this.ipAddress = ipAddress;
		this.hasVoted = hasVoted;
		this.isSelected = isSelected;
	}
	
	/**
	 * Get the identification the identification of the participant
	 * @return the identification the identification of the participant
	 */
	public String getIdentification() {
		return identification;
	}

	/**
	 * Set the identification the identification of the participant
	 * @param identification the identification the identification of the participant
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}

	/**
	 * Get the IP address of the participant
	 * @return the IP address of the participant
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Set the IP address of the participant
	 * @param ipAddress the IP address of the participant
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	/**
	 * Indicates if the participant has already cast her ballot
	 * @return true if casted
	 */
	public boolean hasVoted() {
		return hasVoted;
	}

	/**
	 * Set if the participant has already cast her ballot
	 * @param hasVoted true if casted
	 */
	public void setHasVoted(boolean hasVoted) {
		this.hasVoted = hasVoted;
	}

	/**
	 * Indicate if the participant in the network is selected as member of the electorate
	 * @return true if she belongs to the electorate
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * Set if the participant in the network is selected as member of the electorate
	 * @param isSelected true if she belongs to the electorate
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
}
