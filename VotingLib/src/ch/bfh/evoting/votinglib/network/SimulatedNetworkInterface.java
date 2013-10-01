package ch.bfh.evoting.votinglib.network;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import ch.bfh.evoting.instacirclelib.Message;
import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.VoteMessage;
import ch.bfh.evoting.votinglib.util.SerializationUtil;

public class SimulatedNetworkInterface implements NetworkInterface{
	
	private final SerializationUtil su;
	private final Context context;
	
	public SimulatedNetworkInterface (Context context) {
		this.context = context;
		
		su = AndroidApplication.getInstance().getSerializationUtil();
		
		// Listening for arriving messages
		LocalBroadcastManager.getInstance(context).registerReceiver(
				mMessageReceiver, new IntentFilter("messageArrived"));
	}
	
	@Override
	public String getNetworkName() {
		return null;
	}
	
	@Override
	public String getConversationPassword() {
		return "1234";
	}
	
	@Override
	public List<Participant> getConversationParticipants(){
		
		List<Participant> participants = new ArrayList<Participant>();
		Participant p1 = new Participant("Participant 1 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p1);
		Participant p2 = new Participant("Participant 2 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p2);
		Participant p3 = new Participant("Participant 3 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p3);
		Participant p4 = new Participant("Participant 4 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p4);
		Participant p5 = new Participant("Participant 5 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p5);
		Participant p6 = new Participant("Participant 6 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p6);
		Participant p7 = new Participant("Participant 7 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p7);
		Participant p8 = new Participant("Participant 8 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p8);
		Participant p9 = new Participant("Participant 9 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p9);
		Participant p10 = new Participant("Participant 10 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p10);
		Participant p11 = new Participant("Participant 11 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p11);
		Participant p12 = new Participant("Participant 12 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p12);
		Participant p13 = new Participant("Participant 13 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p13);
		Participant p14 = new Participant("Participant 14 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p14);
		Participant p15 = new Participant("Participant 15 with very very very very very very very very very very very very long name", "", false, false);
		participants.add(p15);
		
		return participants;
	}
	
	/**
	 * This method can be used to send a broadcast message
	 * 
	 * @param votemessage The votemessage which should be sent
	 * @param sender The origin of the message
	 */
	@Override
	public void sendMessage(VoteMessage votemessage, String sender){
		Message message = new Message(su.serialize(votemessage), Message.MSG_CONTENT, sender);
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
	public void sendMessage(VoteMessage votemessage, String sender, String destinationIPAddress){
		Message message = new Message(su.serialize(votemessage), Message.MSG_CONTENT, sender);
		Intent intent = new Intent("messageSend");
		intent.putExtra("message", message);
		intent.putExtra("ipAddress", destinationIPAddress);
		intent.putExtra("broadcast", false);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
	
	@Override
	public String getMyIpAddress() {
		
		return "";
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
		
		// notify the UI that new message has been arrived
		Intent messageArrivedIntent = new Intent("voteMessageArrived");
		messageArrivedIntent.putExtra("voteMessage", voteMessage);
		LocalBroadcastManager.getInstance(context).sendBroadcast(messageArrivedIntent);
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
