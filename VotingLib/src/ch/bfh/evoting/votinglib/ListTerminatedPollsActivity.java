package ch.bfh.evoting.votinglib;

import java.util.List;

import ch.bfh.evoting.votinglib.adapters.PollAdapter;
import ch.bfh.evoting.votinglib.db.PollDbHelper;
import ch.bfh.evoting.votinglib.entities.Poll;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Activity listing all terminated polls
 * @author Philémon von Bergen
 *
 */
public class ListTerminatedPollsActivity extends Activity {
	
	private ListView lvPolls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_terminated_polls);
		setupActionBar();

		//get the poll and generate the list
		List<Poll> polls = PollDbHelper.getInstance(this).getAllTerminatedPolls();
		
		lvPolls = (ListView) findViewById(R.id.listview_polls);
		lvPolls.setAdapter(new PollAdapter(this, R.layout.list_item_poll, polls));

		//create a listener on each line
		final Context ctx = this;
		lvPolls.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> listview, View view, int position, long id) {
				Poll poll = (Poll) (lvPolls.getItemAtPosition(position));

				Intent intent = new Intent(ctx, DisplayResultActivity.class);
				intent.putExtra("poll", poll);
				intent.putExtra("saveToDb", false);
				startActivity(intent);
			}                 
		});

//		Button btnBackHome = (Button) findViewById(R.id.button_back_to_home);
//		btnBackHome.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				String packageName = getApplication().getApplicationContext().getPackageName();
//				if(packageName.equals("ch.bfh.evoting.voterapp")){
//					Intent i = new Intent("ch.bfh.evoting.voterapp.VoterAppMainActivity");
//					startActivity(i);
//				} else if (packageName.equals("ch.bfh.evoting.adminapp")){
//					Intent i = new Intent("ch.bfh.evoting.adminapp.AdminAppMainActivity");
//					startActivity(i);
//				}
//			}
//		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			String packageName = getApplication().getApplicationContext().getPackageName();
			if(packageName.equals("ch.bfh.evoting.voterapp")){
				Intent i = new Intent("ch.bfh.evoting.voterapp.VoterAppMainActivity");
				startActivity(i);
			} else if (packageName.equals("ch.bfh.evoting.adminapp")){
				Intent i = new Intent("ch.bfh.evoting.adminapp.AdminAppMainActivity");
				startActivity(i);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		//People can go back to calling activity
		super.onBackPressed();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	public void onItemClick(AdapterView<?> listview, View view, int position, long id) {

		// extract the object assigned to the position which has been clicked
		Poll poll = (Poll) listview.getAdapter().getItem(position);

		//Start the activity displaying the result of the given poll
		Intent intent = new Intent(this, DisplayResultActivity.class);
		intent.putExtra("poll", poll);
		intent.putExtra("saveToDb", false);
		startActivity(intent);
	}

}
