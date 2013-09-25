package ch.bfh.evoting.votinglib;

import java.util.List;
import ch.bfh.evoting.votinglib.entities.Poll;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * Activity listing all terminated polls
 * @author Philémon von Bergen
 *
 */
public class ListTerminatedPollsActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_terminated_polls);
		
		//get the poll and generate the list
		List<Poll> polls = PollDbHelper.getInstance(this).getAllTerminatedPolls();
		setListAdapter(new PollListAdapter(this, R.layout.list_item_poll, polls));

		//create a listener on each line
		final Context ctx = this;
		final ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> listview, View view, int position, long id) {
				Poll poll =(Poll) (lv.getItemAtPosition(position));

				Intent intent = new Intent(ctx, DisplayResultActivity.class);
				intent.putExtra("poll", poll);
				intent.putExtra("saveToDb", false);
				startActivity(intent);
			}                 
		});
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
