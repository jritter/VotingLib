package ch.bfh.evoting.votinglib.network;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.alljoyn.bus.Status;



import ch.bfh.evoting.alljoyn.BusHandler;
import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.WaitForVotesActivity;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.VoteMessage;
import ch.bfh.evoting.votinglib.network.wifi.WifiAPManager;
import ch.bfh.evoting.votinglib.util.SerializationUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class AllJoynNetworkInterface extends AbstractNetworkInterface{

	private static final String PREFS_NAME = "network_preferences";

	private BusHandler mBusHandler;
	private String networkName;

	public AllJoynNetworkInterface(Context context) {
		super(context);
		HandlerThread busThread = new HandlerThread("BusHandler");
		busThread.start();
		mBusHandler = new BusHandler(busThread.getLooper(), AndroidApplication.getInstance().getApplicationContext());

		// Listening for arriving messages
		LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("messageArrived"));
	}

	@Override
	public String getNetworkName() {
		SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, 0);
		preferences = context.getSharedPreferences(PREFS_NAME, 0);
		return preferences.getString("SSID", "");
	}

	@Override
	public String getConversationPassword() {
		return networkName;
	}

	@Override
	public String getMyIpAddress() {
		return mBusHandler.getIdentification();
	}

	@Override
	public Map<String, Participant> getConversationParticipants() {
		TreeMap<String,Participant> parts = new TreeMap<String,Participant>();
		for(String s : mBusHandler.getParticipants(this.networkName)){
			parts.put(s, new Participant(s, s, false, false));
		}
		return parts;
	}

	@Override
	public boolean sendMessage(VoteMessage votemessage) {
		votemessage.setSenderIPAdress(getMyIpAddress());
		SerializationUtil su = AndroidApplication.getInstance().getSerializationUtil();
		String string = su.serialize(votemessage);
		
		//Since message sent through AllJoyn are not sent to the sender, we do it here
		this.transmitReceivedMessage(votemessage);
		
		Message msg = mBusHandler.obtainMessage(BusHandler.PING);
		Bundle data = new Bundle();
		data.putString("groupName", this.networkName);
		data.putString("pingString", string);
		msg.setData(data);
		return mBusHandler.sendMessage(msg);
	}

	@Override
	public boolean sendMessage(VoteMessage votemessage, String destinationIPAddress) {
		throw new UnsupportedOperationException("Unicast is not supported with AllJoyn Network interface");
	}

	@Override
	public boolean disconnect() {
		Status status1 = null, status2 = Status.OK;
		status1 = mBusHandler.doLeaveGroup(networkName);
		String packageName = AndroidApplication.getInstance().getApplicationContext().getPackageName();
		if(packageName.equals("ch.bfh.evoting.adminapp")){
			status2 = mBusHandler.doDestroyGroup(networkName);
			mBusHandler.doDisconnect();
		}
		this.networkName = null;
		if(status1 != Status.OK || status2 != Status.OK){
			return false;
		}
		return true;
	}

	@Override
	public boolean joinNetwork(String networkName) {
		String packageName = AndroidApplication.getInstance().getApplicationContext().getPackageName();
		Status status;
		boolean apOn = new WifiAPManager().isWifiAPEnabled((WifiManager) context.getSystemService(Context.WIFI_SERVICE));

		if(packageName.equals("ch.bfh.evoting.adminapp") || apOn){
			status = mBusHandler.doCreateGroup(networkName);
			this.networkName = networkName;
		} else {
			status = mBusHandler.doJoinGroup(networkName);
			this.networkName = networkName;
		}
		if(status != Status.OK){
			Log.e("ni", "satus "+ status);
			return false;
		} else {
			LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("NetworkServiceStarted"));
			return true;
		}
	}

	/**
	 * this broadcast receiver listens for incoming messages
	 */
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SerializationUtil su = AndroidApplication.getInstance().getSerializationUtil();
			transmitReceivedMessage((VoteMessage) su.deserialize(intent.getStringExtra("message")));
		}
	};

}
