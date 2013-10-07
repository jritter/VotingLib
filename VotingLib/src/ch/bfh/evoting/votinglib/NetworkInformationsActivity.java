package ch.bfh.evoting.votinglib;

import ch.bfh.evoting.votinglib.AndroidApplication;
import ch.bfh.evoting.votinglib.util.HelpDialogFragment;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class NetworkInformationsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_informations);
		setupActionBar();

		String SSID = AndroidApplication.getInstance().getNetworkInterface().getNetworkName();
		String password = AndroidApplication.getInstance().getNetworkInterface().getConversationPassword();
		if(password==null){
			SSID = getString(R.string.not_connected);
			password = getString(R.string.not_connected);
		}

		TextView tv_network_name = (TextView) findViewById(R.id.textview_network_name);
		tv_network_name.setText(SSID);

		TextView tv_network_password = (TextView) findViewById(R.id.textview_network_password);
		tv_network_password.setText(password);

		//TODO QR-code and NFC
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.network_informations, menu);
		return true;
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			if(this.getIntent().getBooleanExtra("goToMain", false)){
				NavUtils.navigateUpFromSameTask(this);
			} else {
				super.onBackPressed();
			}
			return true;
		}	
		else if (item.getItemId() == R.id.help){
			HelpDialogFragment hdf = HelpDialogFragment.newInstance( getString(R.string.help_title_network_info), getString(R.string.help_text_network_info) );
			hdf.show( getFragmentManager( ), "help" );
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
