package ch.bfh.evoting.votinglib.network;

import java.util.Map;

import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.VoteMessage;

/**
 * Interface to the network component
 * @author Philémon von Bergen
 *
 */
public interface NetworkInterface {
	
	public String getNetworkName();

	public String getConversationPassword();
	
	public String getMyIpAddress();
	
	public Map<String,Participant> getConversationParticipants();
	
	/**
	 * This method can be used to send a broadcast message
	 * 
	 * @param votemessage The votemessage which should be sent
	 */
	public void sendMessage(VoteMessage votemessage);
	
	/**
	 * This method signature can be used to send unicast message to a specific ip address
	 * 
	 * 
	 * @param votemessage The votemessage which should be sent
	 * @param destinationIPAddress The destination of the message
	 */
	public void sendMessage(VoteMessage votemessage, String destinationIPAddress);	
}

