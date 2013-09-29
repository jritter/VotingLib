package ch.bfh.evoting.votinglib;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;
import ch.bfh.evoting.instacirclelib.Message;
import ch.bfh.evoting.instacirclelib.db.NetworkDbHelper;
import ch.bfh.evoting.votinglib.util.JavaSerialization;
import ch.bfh.evoting.votinglib.util.SerializationUtil;

public class NetworkInterface {
	
	private final SerializationUtil su;
	private final Context context;
	private final NetworkDbHelper dbHelper;
	
	public NetworkInterface (Context context) {
		this.context = context;
		
		su = new SerializationUtil(new JavaSerialization());
		dbHelper = NetworkDbHelper.getInstance(context);
		
		// Listening for arriving messages
		LocalBroadcastManager.getInstance(context).registerReceiver(
				mMessageReceiver, new IntentFilter("messageArrived"));
	}
	
	public void connectToNetwork(){
		
	}
	
	public void createNetwork () {
		
	}
	
	public String getNetworkName() {
		return null;
	}
	
	public String getConversationPassword() {
		return dbHelper.getCipherKey();
	}
	
	public ArrayList<String> getConversationParticipants(){
		ArrayList<String> participants = new ArrayList<String>(); 
		
		Cursor c = dbHelper.queryParticipants();
		
		if (c != null){
			while(c.moveToNext()){
				participants.add(c.getString(c.getColumnIndex("identification")));
			}
		}
		
		return participants;
	}
	
	/**
	 * This method can be used to send a broadcast message
	 * 
	 * @param votemessage The votemessage which should be sent
	 * @param sender The origin of the message
	 */
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
	public void sendMessage(VoteMessage votemessage, String sender, String destinationIPAddress){
		Message message = new Message(su.serialize(votemessage), Message.MSG_CONTENT, sender);
		Intent intent = new Intent("messageSend");
		intent.putExtra("message", message);
		intent.putExtra("ipAddress", destinationIPAddress);
		intent.putExtra("broadcast", false);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
