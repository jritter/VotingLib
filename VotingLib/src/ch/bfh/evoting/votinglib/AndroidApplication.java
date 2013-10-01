package ch.bfh.evoting.votinglib;

import ch.bfh.evoting.votinglib.network.NetworkInterface;
import ch.bfh.evoting.votinglib.network.SimulatedNetworkInterface;
import ch.bfh.evoting.votinglib.util.JavaSerialization;
import ch.bfh.evoting.votinglib.util.SerializationUtil;
import android.app.Application;

/**
 * Class representing the application. This class is used to do some initializations and to share data.
 * @author Philémon von Bergen
 *
 */
public class AndroidApplication extends Application {

	private static AndroidApplication instance;
	private SerializationUtil su;
	private NetworkInterface ni;

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
	}

	/**
	 * Initialize the Serialization method and the Network Component to use
	 */
	private void initializeInstance() {
         su = new SerializationUtil(new JavaSerialization());
         ni = new SimulatedNetworkInterface(this.getApplicationContext());
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
}
