package ch.bfh.evoting.votinglib.network;

import java.util.List;

import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.VoteMessage;



public interface NetworkInterface {
	
	
	
	public void connectToNetwork();
	
	public void createNetwork ();
	
	public String getNetworkName();
	
	public String getConversationPassword();
	
	public String getMyIpAddress();
	
	public List<Participant> getConversationParticipants();
	
	/**
	 * This method can be used to send a broadcast message
	 * 
	 * @param votemessage The votemessage which should be sent
	 * @param sender The origin of the message
	 */
	public void sendMessage(VoteMessage votemessage, String sender);
	
	/**
	 * This method signature can be used to send unicast message to a specific ip address
	 * 
	 * 
	 * @param votemessage The votemessage which should be sent
	 * @param sender The origin of the message
	 * @param destinationIPAddress The destination of the message
	 */
	public void sendMessage(VoteMessage votemessage, String sender, String destinationIPAddress);	
}

