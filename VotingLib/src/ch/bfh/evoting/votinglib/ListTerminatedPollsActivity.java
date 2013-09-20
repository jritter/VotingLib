package ch.bfh.evoting.votinglib;

import java.util.ArrayList;
import java.util.List;

import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Poll;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListTerminatedPollsActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_terminated_polls);

		List<Poll> polls = PollDbHelper.getInstance(this).getAllTerminatedPolls();

		setListAdapter(new PollListAdapter(this, R.layout.list_item_poll, polls));


		//Only for creating object from here instead of receiving them by the app using the library
		Poll poll = new Poll();
		poll.setQuestion("question1: this is the question for the fisrt poll which is quite long, enough to fill more than one line, maybe two.question1: this is the question for the fisrt poll which is quite long, enough to fill more than one line, maybe two.question1: this is the question for the fisrt poll which is quite long, enough to fill more than one line, maybe two.question1: this is the question for the fisrt poll which is quite long, enough to fill more than one line, maybe two.");
		poll.setTerminated(true);
		
		List<Option> objects = new ArrayList<Option>();
		objects.add(new Option("result 1", 5, 506, 0));
		objects.add(new Option("result 2", 5, 506, 0));
		objects.add(new Option("result 3", 5, 506, 0));
		objects.add(new Option("result 4", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5, this result is also quite long in order to how it displays on more than one line, always display problems", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));
		objects.add(new Option("result 5", 5, 506, 0));

		poll.setOptions(objects);

		Intent intent = new Intent(this, DisplayResultActivity.class);
		intent.putExtra("poll", poll);
		intent.putExtra("saveToDb", true);
		startActivity(intent);

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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_terminated_polls, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	public void onItemClick(AdapterView<?> listview, View view, int position, long id) {

		/*if (listview.getAdapter().getCount() - 1 == position) {
			// handling the last item in the list, which is the "Create network"
			// item
			Intent intent = new Intent(this, CreateNetworkActivity.class);
			startActivity(intent);
		} else {*/
		// extract the object assigned to the position which has been clicked
		Poll poll = (Poll) listview.getAdapter().getItem(position);

		Intent intent = new Intent(this, DisplayResultActivity.class);
		intent.putExtra("poll", poll);
		intent.putExtra("saveToDb", false);
		startActivity(intent);


	}
}
