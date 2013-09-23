package ch.bfh.evoting.votinglib;

import java.util.ArrayList;
import java.util.List;

import ch.bfh.evoting.votinglib.entities.DatabaseException;
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

//		this.createDummyPolls();
		
		List<Poll> polls = PollDbHelper.getInstance(this).getAllTerminatedPolls();

		setListAdapter(new PollListAdapter(this, R.layout.list_item_poll, polls));

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

		// extract the object assigned to the position which has been clicked
		Poll poll = (Poll) listview.getAdapter().getItem(position);

		Intent intent = new Intent(this, DisplayResultActivity.class);
		intent.putExtra("poll", poll);
		intent.putExtra("saveToDb", false);
		startActivity(intent);


	}
	
	private void createDummyPolls(){
		Poll poll1 = new Poll();
		poll1.setQuestion("Poll 1");
		List<Option> options1 = new ArrayList<Option>();
		Option option1 = new Option("Option 1", 4, 50.6, 0, 0);
		options1.add(option1);
		Option option2 = new Option("Option 2", 6, 50.6, 0, 0);
		options1.add(option2);
		poll1.setOptions(options1);

		poll1.setStartTime(System.currentTimeMillis());
		poll1.setTerminated(true);

		Poll poll2 = new Poll();
		poll2.setQuestion("Poll 2");
		List<Option> options2 = new ArrayList<Option>();
		Option option3 = new Option("Option 3", 4, 50.6, 0, 0);
		options2.add(option3);
		Option option4 = new Option("Option 4", 6, 50.6, 0, 0);
		options2.add(option4);
		poll2.setOptions(options2);

		poll2.setStartTime(System.currentTimeMillis());
		poll2.setTerminated(false);

		Poll poll3 = new Poll();
		poll3.setQuestion("Poll 3");
		List<Option> options3 = new ArrayList<Option>();
		Option option5 = new Option("Option 5", 4, 50.6, 0, 0);
		options3.add(option5);
		Option option6 = new Option("Option 6", 6, 50.6, 0, 0);
		options3.add(option6);
		poll3.setOptions(options3);

		poll3.setStartTime(System.currentTimeMillis());
		poll3.setTerminated(false);

		Poll poll4 = new Poll();
		poll4.setQuestion("Poll 4");
		List<Option> options4 = new ArrayList<Option>();
		Option option7 = new Option("Option 7", 4, 50.6, 0, 0);
		options4.add(option7);
		Option option8 = new Option("Option 8", 6, 50.6, 0, 0);
		options4.add(option8);
		poll4.setOptions(options4);

		poll4.setStartTime(System.currentTimeMillis());
		poll4.setTerminated(true);

		Poll poll5 = new Poll();
		poll5.setQuestion("Poll 5");
		List<Option> options5 = new ArrayList<Option>();
		Option option9 = new Option("Option 9", 4, 50.6, 0, 0);
		options5.add(option9);
		Option option10 = new Option("Option 10", 6, 50.6, 0, 0);
		options5.add(option10);
		poll5.setOptions(options5);
		
		poll5.setStartTime(System.currentTimeMillis());
		poll5.setTerminated(true);
		
		PollDbHelper db = PollDbHelper.getInstance(this);
		try {
			db.savePoll(poll1);
			db.savePoll(poll2);
			db.savePoll(poll3);
			db.savePoll(poll4);
			db.savePoll(poll5);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
