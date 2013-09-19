package ch.bfh.evoting.votinglib.entities;

/**
 * Class representing a participant to the poll
 * @author Philémon von Bergen
 *
 */
public class Participant {
	
	private String identification;
	private String ipAddress;
	
	/**
	 * Construct a Participant object
	 * @param identification the identification of the participant
	 * @param ipAddress the IP address of the participant
	 */
	public Participant(String identification, String ipAddress){
		this.identification = identification;
		this.ipAddress = ipAddress;
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
	
}
