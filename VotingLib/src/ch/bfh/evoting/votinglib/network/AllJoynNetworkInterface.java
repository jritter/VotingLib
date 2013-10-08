//package ch.bfh.evoting.votinglib.network;
//
//import java.io.Serializable;
//import java.util.Map;
//import java.util.TreeMap;
//
//
//
//import ch.bfh.evoting.alljoyn.BusHandler;
//import ch.bfh.evoting.votinglib.AndroidApplication;
//import ch.bfh.evoting.votinglib.WaitForVotesActivity;
//import ch.bfh.evoting.votinglib.entities.Participant;
//import ch.bfh.evoting.votinglib.entities.VoteMessage;
//import ch.bfh.evoting.votinglib.util.SerializationUtil;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.os.HandlerThread;
//import android.os.Message;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//
//public class AllJoynNetworkInterface extends AbstractNetworkInterface{
//
//	private BusHandler mBusHandler;
//	private String networkName;
//
//	public AllJoynNetworkInterface(Context context) {
//		super(context);
//		HandlerThread busThread = new HandlerThread("BusHandler");
//		busThread.start();
//		mBusHandler = new BusHandler(busThread.getLooper(), AndroidApplication.getInstance().getApplicationContext());
//
//		// Listening for arriving messages
//		LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("messageArrived"));
//	}
//
//	@Override
//	public String getNetworkName() {
//		return networkName;
//	}
//
//	@Override
//	public String getConversationPassword() {
//		return networkName;
//	}
//
//	@Override
//	public String getMyIpAddress() {
//		mBusHandler.getIdentification();
//		return null;
//	}
//
//	@Override
//	public Map<String, Participant> getConversationParticipants() {
//		TreeMap<String,Participant> parts = new TreeMap<String,Participant>();
//		for(String s : mBusHandler.getParticipants(this.networkName)){
//			parts.put(s, new Participant(s, s, false, false));
//		}
//		return parts;
//	}
//
//	@Override
//	public void sendMessage(VoteMessage votemessage) {
//		SerializationUtil su = AndroidApplication.getInstance().getSerializationUtil();
//		String string = su.serialize(votemessage);
//		Message msg = mBusHandler.obtainMessage(BusHandler.PING);
//		Bundle data = new Bundle();
//		data.putString("groupName", this.networkName);
//		data.putString("pingString", string);
//		msg.setData(data);
//		mBusHandler.sendMessage(msg);
//	}
//
//	@Override
//	public void sendMessage(VoteMessage votemessage, String destinationIPAddress) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void disconnect() {
//		mBusHandler.sendEmptyMessage(BusHandler.DISCONNECT);
//		this.networkName = null;
//	}
//
//	@Override
//	public void joinNetwork(String networkName) {
//		Log.e("Network password", networkName);
//		String packageName = AndroidApplication.getInstance().getApplicationContext().getPackageName();
//		if(packageName.equals("ch.bfh.evoting.adminapp")){
//			Message msg = mBusHandler.obtainMessage(BusHandler.CREATE_GROUP, networkName);
//			mBusHandler.sendMessage(msg);
//			this.networkName = networkName;
//		} else {
//			Message msg = mBusHandler.obtainMessage(BusHandler.JOIN_GROUP, networkName);
//			mBusHandler.sendMessage(msg);
//			this.networkName = networkName;
//		}
//		LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("NetworkServiceStarted"));
//	}
//
//	/**
//	 * this broadcast receiver listens for incoming instacircle messages
//	 */
//	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			SerializationUtil su = AndroidApplication.getInstance().getSerializationUtil();
//			handleReceivedMessage((VoteMessage) su.deserialize(intent.getStringExtra("message")));
//		}
//	};
//
//	/**
//	 * This method acts as a wrapper between instacircle messages and vote
//	 * messages by extracting the vote message and rebroadcast the notification.
//	 * 
//	 * @param message
//	 */
//	private void handleReceivedMessage(VoteMessage voteMessage) {
//		
//			this.transmitReceivedMessage(voteMessage);
//		
//	}
//
//}
