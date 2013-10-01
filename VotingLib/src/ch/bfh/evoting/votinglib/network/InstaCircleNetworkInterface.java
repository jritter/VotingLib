package ch.bfh.evoting.votinglib.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import ch.bfh.evoting.instacirclelib.Message;
import ch.bfh.evoting.instacirclelib.db.NetworkDbHelper;
import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.VoteMessage;
import ch.bfh.evoting.votinglib.util.SerializationUtil;

public class InstaCircleNetworkInterface implements ch.bfh.evoting.votinglib.network.NetworkInterface {
	
	private final SerializationUtil su;
	private final Context context;
	private final NetworkDbHelper dbHelper;
	
	public InstaCircleNetworkInterface (Context context) {
		this.context = context;
		
		su = AndroidApplication.getInstance().getSerializationUtil();
		dbHelper = NetworkDbHelper.getInstance(context);
		
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
		return dbHelper.getCipherKey();
	}
	
	@Override
	public Map<String,Participant> getConversationParticipants(){
		ArrayList<Participant> participants = new ArrayList<Participant>(); 
		
		Cursor c = dbHelper.queryParticipants();
		
		if (c != null){
			while(c.moveToNext()){
				Participant p = new Participant(c.getString(c.getColumnIndex("identification")), c.getString(c.getColumnIndex("ipAddress")), false, false);
				participants.add(p);
			}
		}
		c.close();
		
		Map<String,Participant> map = new HashMap<String,Participant>();
		for(Participant p:participants){
			map.put(p.getIpAddress(), p);
		}
		
		return map;
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

	@Override
	public String getMyIpAddress(){
		return getIPAddress(true);
	}
	
	/**
	 * Get IP address from first non-localhost interface
	 * @param ipv4  true=return ipv4, false=return ipv6
	 * @return  address or empty string
	 * @author http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
	 */
	private String getIPAddress(boolean useIPv4) {
		
		WifiManager wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
		String ipString = null;

		if(new ch.bfh.evoting.instacirclelib.wifi.WifiAPManager().isWifiAPEnabled(wifiManager)){

			try{
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
						.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					if (intf.getName().contains("wlan")) {
						for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
								.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress()
									&& (inetAddress.getAddress().length == 4)) {
								ipString = inetAddress.getHostAddress();
								break;
							}
						}
					}
				}
			} catch (SocketException e){
				e.printStackTrace();
			}
		} else {

			WifiManager wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
			DhcpInfo dhcp = wifi.getDhcpInfo();

			InetAddress found_ip_address = null;
			int ip = dhcp.ipAddress;
			byte[] quads = new byte[4];
			for (int k = 0; k < 4; k++)
				quads[k] = (byte) (ip >> (k * 8));
			try {
				found_ip_address =  InetAddress.getByAddress(quads);
				ipString = found_ip_address.getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}

		return ipString;
	}
}
