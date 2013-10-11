package ch.bfh.evoting.votinglib.network;

import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.VoteMessage;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import ch.bfh.evoting.votinglib.util.SerializationUtil;

public abstract class AbstractNetworkInterface implements NetworkInterface {

	


	protected Context context;
	protected final SerializationUtil su;


	public AbstractNetworkInterface(Context context){
		this.context = context;
		su = AndroidApplication.getInstance().getSerializationUtil();		
	}
	

	@Override
	public abstract boolean joinNetwork(String networkName);
	
	@Override
	public abstract String getNetworkName();

	@Override
	public abstract String getConversationPassword();

	@Override
	public abstract String getMyIpAddress();

	@Override
	public abstract Map<String, Participant> getConversationParticipants();

	@Override
	public abstract boolean sendMessage(VoteMessage votemessage);

	@Override
	public abstract boolean sendMessage(VoteMessage votemessage, String destinationIPAddress);
	
	@Override
	public abstract boolean disconnect();
	
	
	/**
	 * This method checks the message type and inform the application of the new incoming message.
	 * 
	 * @param voteMessage
	 */
	protected final void transmitReceivedMessage(VoteMessage voteMessage) {
		
		Intent messageArrivedIntent;
		switch(voteMessage.getMessageType()){
		case VOTE_MESSAGE_ELECTORATE:
			// notify the UI that new message has arrived
			messageArrivedIntent = new Intent(BroadcastIntentTypes.electorate);
			messageArrivedIntent.putExtra("participants", voteMessage.getMessageContent());
			LocalBroadcastManager.getInstance(context).sendBroadcast(messageArrivedIntent);
			break;
		case VOTE_MESSAGE_POLL_TO_REVIEW:
			// notify the UI that new message has arrived
			messageArrivedIntent = new Intent(BroadcastIntentTypes.pollToReview);
			messageArrivedIntent.putExtra("poll", voteMessage.getMessageContent());
			messageArrivedIntent.putExtra("sender", voteMessage.getSenderIPAddress());
			LocalBroadcastManager.getInstance(context).sendBroadcast(messageArrivedIntent);
			break;
		case VOTE_MESSAGE_ACCEPT_REVIEW:
			// notify the UI that new message has arrived
			messageArrivedIntent = new Intent(BroadcastIntentTypes.acceptReview);
			messageArrivedIntent.putExtra("participant", voteMessage.getSenderIPAddress());
			LocalBroadcastManager.getInstance(context).sendBroadcast(messageArrivedIntent);
			break;
		case VOTE_MESSAGE_START_POLL:
			// notify the UI that new message has arrived
			messageArrivedIntent = new Intent(BroadcastIntentTypes.startVote);
			LocalBroadcastManager.getInstance(context).sendBroadcast(messageArrivedIntent);
			break;
		case VOTE_MESSAGE_STOP_POLL:
			// notify the UI that new message has arrived
			messageArrivedIntent = new Intent(BroadcastIntentTypes.stopVote);
			LocalBroadcastManager.getInstance(context).sendBroadcast(messageArrivedIntent);
			break;
		case VOTE_MESSAGE_VOTE:
			// notify the UI that new message has arrived
			messageArrivedIntent = new Intent(BroadcastIntentTypes.newVote);
			messageArrivedIntent.putExtra("vote", voteMessage.getMessageContent());
			messageArrivedIntent.putExtra("voter", voteMessage.getSenderIPAddress());
			LocalBroadcastManager.getInstance(context).sendBroadcast(messageArrivedIntent);
			break;
		}
	}

	

}
