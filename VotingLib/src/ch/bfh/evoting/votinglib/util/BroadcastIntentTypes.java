package ch.bfh.evoting.votinglib.util;

/**
 * Class listing local broadcast intent types that are sent in the application
 * @author Philémon von Bergen
 *
 */
public class BroadcastIntentTypes {
	/**
	 * Intent type sent when a participant has changed his state i.e. has joined the network
	 * Extras:
	 * - nothing
	 */
	public static final String participantStateUpdate = "participantStateUpdate";
	/**
	 * Intent type sent when the admin sends the participants present in the network
	 * Extras:
	 * - "participants": List<Participant> list of Participant objects present in the network
	 */
	public static final String electorate = "electorate";
	/**
	 * Intent type sent when the admin sends the participants present in the network
	 * Extras:
	 * - "poll": Poll object containing all data of the poll
	 */
	public static final String pollToReview = "pollToReview";
	/**
	 * Intent type sent when the admin sends the start signal for the voting phase
	 * Extras:
	 * - nothing
	 */
	public static final String startVote = "startVote";
	/**
	 * Intent type sent when the admin sends the stop signal for the voting phase
	 * Extras:
	 * - nothing
	 */
	public static final String stopVote = "stopVote";
	/**
	 * Intent type sent when a new vote is received
	 * Extras:
	 * - "vote": the vote
	 * - "voter": ip address of voter
	 */
	public static final String newVote = "newVote";
	
	
	
	

}
