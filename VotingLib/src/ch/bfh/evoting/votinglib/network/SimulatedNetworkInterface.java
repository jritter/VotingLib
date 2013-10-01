package ch.bfh.evoting.votinglib.network;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import ch.bfh.evoting.instacirclelib.Message;
import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.VoteMessage;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import ch.bfh.evoting.votinglib.util.NetworkSimulator;
import ch.bfh.evoting.votinglib.util.SerializationUtil;

public class SimulatedNetworkInterface implements NetworkInterface{

	private final SerializationUtil su;
	private final Context context;
	
	private NetworkSimulator ns;

	public SimulatedNetworkInterface (Context context) {
		this.context = context;

		su = AndroidApplication.getInstance().getSerializationUtil();

		// Listening for arriving messages
		LocalBroadcastManager.getInstance(context).registerReceiver(
				mMessageReceiver, new IntentFilter("messageArrived"));

		 ns = new NetworkSimulator(context);
	}


	@Override
	public String getNetworkName() {
		return "Simulated network";
	}

	@Override
	public String getConversationPassword() {
		return "1234";
	}

	@Override
	public Map<String,Participant> getConversationParticipants(){
		return ns.createDummyParticipants();
	}

	/**
	 * This method can be used to send a broadcast message
	 * 
	 * @param votemessage The votemessage which should be sent
	 * @param sender The origin of the message
	 */
	@Override
	public void sendMessage(VoteMessage votemessage){
		Message message = new Message(su.serialize(votemessage), Message.MSG_CONTENT, this.getMyIpAddress());
		Intent intent = new Intent("messageSend");
		intent.putExtra("message", message);
		intent.putExtra("broadcast", true);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	/**
	 * This method signature can be used to send unicast message to a specific ip address
	 * 
	 * 
	 * @param votemessage The votemessage which should be sent
	 * @param sender The origin of the message
	 * @param destinationIPAddress The destination of the message
	 */
	@Override
	public void sendMessage(VoteMessage votemessage, String destinationIPAddress){
		Message message = new Message(su.serialize(votemessage), Message.MSG_CONTENT, this.getMyIpAddress());
		Intent intent = new Intent("messageSend");
		intent.putExtra("message", message);
		intent.putExtra("ipAddress", destinationIPAddress);
		intent.putExtra("broadcast", false);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	@Override
	public String getMyIpAddress() {

		String packageName = context.getPackageName();
		if(packageName.equals("ch.bfh.evoting.voterapp")){
			int higher = 15;
			int lower = 2;
			int random = (int)(Math.random() * (higher-lower)) + lower;
			return "192.168.1."+random;
		} else if (packageName.equals("ch.bfh.evoting.adminapp")){
			return "192.168.1.1";
		} else {
			return "";
		}
	}

	/**
	 * This method acts as a wrapper between instacircle messages and vote
	 * messages by extracting the vote message and rebroadcast the notification.
	 * 
	 * @param message
	 */
	private void handleReceivedMessage(Message message) {
		// Extract the votemessage out of the message
		VoteMessage voteMessage = (VoteMessage) su.deserialize(message.getMessage());
		
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

	/**
	 * this broadcast receiver listens for incoming instacircle messages
	 */
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			handleReceivedMessage((Message) intent.getSerializableExtra("message"));
		}
	};


}
