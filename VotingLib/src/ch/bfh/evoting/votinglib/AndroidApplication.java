package ch.bfh.evoting.votinglib;


import org.apache.log4j.Level;

import ch.bfh.evoting.votinglib.network.AllJoynNetworkInterface;
import ch.bfh.evoting.votinglib.network.InstaCircleNetworkInterface;
import ch.bfh.evoting.votinglib.network.NetworkInterface;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import ch.bfh.evoting.votinglib.util.JavaSerialization;
import ch.bfh.evoting.votinglib.util.SerializationUtil;
import ch.bfh.evoting.votinglib.util.Utility;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Class representing the application. This class is used to do some initializations and to share data.
 * @author Philémon von Bergen
 *
 */
public class AndroidApplication extends Application {

	public static final Level LEVEL = Level.DEBUG;;

	private static AndroidApplication instance;
	private SerializationUtil su;
	private NetworkInterface ni;
	private Activity currentActivity = null;


	/**
	 * Return the single instance of this class
	 * @return the single instance of this class
	 */
	public static AndroidApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		instance.initializeInstance();
		Utility.initialiseLogging();
		LocalBroadcastManager.getInstance(this).registerReceiver(mGroupEventReceiver, new IntentFilter(BroadcastIntentTypes.networkGroupDestroyedEvent));
	}

	/**
	 * Initialize the Serialization method and the Network Component to use
	 */
	private void initializeInstance() {
		su = new SerializationUtil(new JavaSerialization());
		ni = new AllJoynNetworkInterface(this.getApplicationContext());// new InstaCircleNetworkInterface(this.getApplicationContext());//new SimulatedNetworkInterface(this.getApplicationContext());
	}

	/**
	 * Get the serialization helper
	 * @return the serialization helper
	 */
	public SerializationUtil getSerializationUtil(){
		return su;
	}

	/**
	 * Get the network component
	 * @return the network component
	 */
	public NetworkInterface getNetworkInterface(){
		return ni;
	}

	public Activity getCurrentActivity(){
		return currentActivity;
	}

	public void setCurrentActivity(Activity currentActivity){
		this.currentActivity = currentActivity;
	}
	
	public void unregisterCurrentActivity(Activity activity){
        if (currentActivity != null && currentActivity.equals(activity))
            this.setCurrentActivity(null);
	}

	/**
	 * this broadcast receiver listens for incoming messages
	 */
	private BroadcastReceiver mGroupEventReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(currentActivity!=null){
				AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
				// Add the buttons
				builder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						String packageName = getPackageName();
						if(packageName.equals("ch.bfh.evoting.adminapp")){
							Intent i = new Intent("ch.bfh.evoting.adminapp.AdminAppMainActivity");
							currentActivity.startActivity(i);
						} else {
							Intent i = new Intent("ch.bfh.evoting.voterapp.VoterAppMainActivity");
							currentActivity.startActivity(i);
						}
					}
				});

				builder.setTitle(R.string.dialog_title_network_lost);
				builder.setMessage(R.string.dialog_network_lost);

				// Create the AlertDialog
				builder.create().show();
			}
		}
	};
	
	@Override
	public void onTerminate() {
		if(this.ni!=null)
			this.ni.disconnect();
		super.onTerminate();
	}

}
