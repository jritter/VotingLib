package ch.bfh.evoting.votinglib.util;

/**
 * Class listing local broadcast intent types that are sent in the application
 * @author Philémon von Bergen
 *
 */
public class BroadcastIntentTypes {
	/**
	 * Intent type sent when the a participant has changed his state i.e. has voted
	 * Extras:
	 * - nothing
	 */
	public static final String participantStateUpdate = "participantStateUpdate";
	/**
	 * Intent type sent when the admin sends the participants present in the network
	 * Extras:
	 * - "participants": List<Participant> list of Participant objects present in the network
	 */
	public static final String sendNetworkParticipants = "sendNetworkParticipants";
	/**
	 * Intent type sent when the admin sends the participants present in the network
	 * Extras:
	 * - "poll": Poll object containing all data of the poll
	 */
	public static final String sendPollToReview = "sendPollToReview";
	/**
	 * Intent type sent when the admin sends the start signal for the voting phase
	 * Extras:
	 * - nothing
	 */
	public static final String goToVote = "goToVote";
	
	
	
	

}
