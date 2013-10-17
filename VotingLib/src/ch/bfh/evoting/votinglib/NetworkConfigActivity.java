package ch.bfh.evoting.votinglib;

import java.io.Serializable;

import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import ch.bfh.evoting.votinglib.util.HelpDialogFragment;
import ch.bfh.evoting.votinglib.VoteActivity.VoteService;
import ch.bfh.evoting.votinglib.fragment.NetworkOptionsFragment;
import ch.bfh.evoting.votinglib.network.wifi.AdhocWifiManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity displaying the available networks
 * 
 * @author Philémon von Bergen
 * 
 */
public class NetworkConfigActivity extends Activity implements TextWatcher,
		OnClickListener {

	private WifiManager wifi;
	private AdhocWifiManager adhoc;

	private static final String PREFS_NAME = "network_preferences";
	private SharedPreferences preferences;
	private EditText etIdentification;
	private BroadcastReceiver serviceStartedListener;

	private Button btnScanQRCode;

	private boolean active;

	private AsyncTask<Object, Object, Object> rescanWifiTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_config);
		
		Fragment fg = new NetworkOptionsFragment();
        // adding fragment to relative layout by using layout id
        getFragmentManager().beginTransaction().add(R.id.fragment_container, fg).commit();
        
		// Show the Up button in the action bar.
		setupActionBar();

		// reading the identification from the preferences, if it is not there
		// it will try to read the name of the device owner
		preferences = getSharedPreferences(PREFS_NAME, 0);
		String identification = preferences.getString("identification", "");

		if (identification.equals("")) {
			identification = readOwnerName();
			// saving the identification field
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("identification", identification);
			editor.commit();
		}

//		btnScanQRCode = (Button) findViewById(R.id.button_scan_qrcode);
//		btnScanQRCode.setOnClickListener(this);

		etIdentification = (EditText) findViewById(R.id.edittext_identification);
		etIdentification.setText(identification);

		etIdentification.addTextChangedListener(this);

		serviceStartedListener = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				active = false;
				rescanWifiTask.cancel(true);
				LocalBroadcastManager.getInstance(NetworkConfigActivity.this)
						.unregisterReceiver(this);
				String packageName = getApplication().getApplicationContext().getPackageName();
				if(packageName.equals("ch.bfh.evoting.adminapp")){
					Intent i = new Intent(NetworkConfigActivity.this, NetworkInformationsActivity.class);
					i.putExtra("goToMain", true);
					startActivity(i);
				} else {
					startActivity(new Intent("ch.bfh.evoting.voterapp.CheckElectorateActivity"));
				}
			}
		};
		LocalBroadcastManager.getInstance(this).registerReceiver(
				serviceStartedListener,
				new IntentFilter(BroadcastIntentTypes.networkConnectionSuccessful));

		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		adhoc = new AdhocWifiManager(wifi);

		active = true;
		rescanWifiTask = new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... arg0) {

				while (active) {
					SystemClock.sleep(5000);
					wifi.startScan();
				}
				return null;
			}

		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	@Override
	protected void onPause() {
		active = false;
		rescanWifiTask.cancel(true);
		super.onPause();
	}

	@Override
	protected void onResume() {
		active = true;
		rescanWifiTask = new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... arg0) {

				while (active) {
					SystemClock.sleep(5000);
					wifi.startScan();
				}
				return null;
			}

		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		super.onResume();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.network_config, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home){
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
//			NavUtils.navigateUpFromSameTask(this);
			super.onBackPressed();
			return true;
		}else if(item.getItemId()==R.id.network_info){
			
			Intent i = new Intent(this,
					ch.bfh.evoting.votinglib.NetworkInformationsActivity.class);
			startActivity(i);
			LocalBroadcastManager.getInstance(this).unregisterReceiver(
					serviceStartedListener);
			return true;
		} else if (item.getItemId()==R.id.help){
			HelpDialogFragment hdf = HelpDialogFragment.newInstance(
					getString(R.string.help_title_network_config),
					getString(R.string.help_text_network_config));
			hdf.show(getFragmentManager(), "help");
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// saving the identification field
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("identification", etIdentification.getText()
				.toString());
		editor.commit();
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	/**
	 * This method is used to extract the name of the device owner
	 * 
	 * @return the name of the device owner
	 */
	private String readOwnerName() {

		Cursor c = getContentResolver().query(
				ContactsContract.Profile.CONTENT_URI, null, null, null, null);
		if (c.getCount() == 0) {
			return "";
		}
		c.moveToFirst();
		String displayName = c.getString(c.getColumnIndex("display_name"));
		c.close();

		return displayName;

	}

	@Override
	public void onClick(View view) {
		
//		if (view == btnScanQRCode) {
//			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
//			intent.setPackage(getApplication().getPackageName());
//			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//			startActivityForResult(intent, 0);
//		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String[] config = intent.getStringExtra("SCAN_RESULT").split(
						"\\|\\|");

				// saving the values that we got
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString("SSID", config[0]);
				editor.putString("password", config[1]);
				editor.commit();

				// connect to the network
				connect(config);

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

//	@Override
//	public void onBackPressed() {
//		// do nothing because we don't want that people access to an anterior
//		// activity
//	}

	/**
	 * This method initiates the connect process
	 * 
	 * @param config
	 *            an array containing the SSID and the password of the network
	 */
	private void connect(String[] config) {
		boolean connectedSuccessful = false;
		// check whether the network is already known, i.e. the password is
		// already stored in the device
		for (WifiConfiguration configuredNetwork : wifi.getConfiguredNetworks()) {
			if (configuredNetwork.SSID.equals("\"".concat(config[0]).concat(
					"\""))) {
				connectedSuccessful = true;
				adhoc.connectToNetwork(configuredNetwork.networkId, this);
				break;
			}
		}
		if (!connectedSuccessful) {
			for (ScanResult result : wifi.getScanResults()) {
				if (result.SSID.equals(config[0])) {
					connectedSuccessful = true;
					adhoc.connectToNetwork(config[0], config[1], this);
					break;
				}
			}
		}

		// display a message if the connection was not successful
		if (!connectedSuccessful) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("InstaCircle - Network not found");
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			alertDialog.setMessage("The network \"" + config[0]
					+ "\" is not available, cannot connect.");
			alertDialog.show();
		}
	}
	
	
}
