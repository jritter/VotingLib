package ch.bfh.evoting.votinglib;

import ch.bfh.evoting.votinglib.network.NetworkInterface;
import ch.bfh.evoting.votinglib.network.SimulatedNetworkInterface;
import ch.bfh.evoting.votinglib.util.JavaSerialization;
import ch.bfh.evoting.votinglib.util.SerializationUtil;
import android.app.Application;

public class AndroidApplication extends Application {

	private static AndroidApplication instance;
	private SerializationUtil su;
	private NetworkInterface ni;

	public static AndroidApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		instance.initializeInstance();
	}

	protected void initializeInstance() {
         su = new SerializationUtil(new JavaSerialization());
         ni = new SimulatedNetworkInterface(this.getApplicationContext());
    }
	
	public SerializationUtil getSerializationUtil(){
		return su;
	}
	
	public NetworkInterface getNetworkInterface(){
		return ni;
	}
}
