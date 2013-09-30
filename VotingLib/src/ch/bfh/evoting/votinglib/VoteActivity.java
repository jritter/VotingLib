package ch.bfh.evoting.votinglib;

import java.io.Serializable;
import java.util.List;

import ch.bfh.evoting.votinglib.adapters.VoteOptionListAdapter;
import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Poll;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity displaying the vote options in the voting phase
 * @author Philémon von Bergen
 *
 */
public class VoteActivity extends ListActivity {


	private Poll poll;
	private List<Option> options;
	private String question;
	private VoteOptionListAdapter volAdapter;
	private int selectedPosition = -1;
	private boolean scrolled = false;
	private boolean demoScrollDone = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote);


		final ListView lv = (ListView)findViewById(android.R.id.list);
		LayoutInflater inflater = this.getLayoutInflater();

		View header = inflater.inflate(R.layout.vote_header, null, false);
		lv.addHeaderView(header);
		//Not used anymore, but could be reused: user could not see he has to scroll to see all votes options
		//		View footer = inflater.inflate(R.layout.vote_footer, null, false);
		//		lv.addFooterView(footer);



		//Get the data in the intent
		Intent intent = this.getIntent();
		poll = (Poll) intent.getSerializableExtra("poll");
		options = poll.getOptions();
		question = poll.getQuestion();

		//Set the question text
		TextView tvQuestion = (TextView)/*header.*/findViewById(R.id.textview_vote_poll_question);
		tvQuestion.setText(question);


		//Set a listener on the cast button
		Button btnCast = (Button)findViewById(R.id.button_cast_button);
		btnCast.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(!scrolled){
					Toast.makeText(VoteActivity.this, getString(R.string.scroll), Toast.LENGTH_SHORT).show();
				} else {
					castBallot();
				}
			}
		});

		//create the list of vote options
		volAdapter = new VoteOptionListAdapter(this, R.layout.list_item_vote, options);
		this.setListAdapter(volAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				selectedPosition = (Integer)view.getTag();

			}
		});

		lv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//Check if the last view is visible
				if (++firstVisibleItem + visibleItemCount > totalItemCount && demoScrollDone) {
					scrolled=true;
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}


		});

		//animate scroll
		new AsyncTask<Object, Object, Object>(){

			@Override
			protected Object doInBackground(Object... params) {
				SystemClock.sleep(300);
				Log.e("test scroll", lv.getLastVisiblePosition() + " " + lv.getCount());
				if(lv.getLastVisiblePosition() < lv.getCount()-2){

					lv.smoothScrollToPositionFromTop(lv.getAdapter().getCount()-1, 0, 1000);
					SystemClock.sleep(1050);
					lv.smoothScrollToPositionFromTop(0, 0, 1000);
					SystemClock.sleep(1050);
					scrolled = false;
					demoScrollDone = true;
					return null;
				} else {
					scrolled = true;
					return null;
				}
			}

		}.execute();

}


@Override
public void onBackPressed() {
	//do nothing because we don't want that people access to an anterior activity
}

/**
 * Method called when cast button is clicked
 */
private void castBallot(){

	Option selectedOption = volAdapter.getItem(selectedPosition);

	//TODO send vote
	if(selectedOption!=null){
		Log.e("vote", "Voted "+selectedOption.getText());
	} else {
		Log.e("vote", "Voted null");
	}

	//Start activity waiting for other participants to vote
	//If is admin, returns to admin app wait activity
	String packageName = getApplication().getApplicationContext().getPackageName();
	if(packageName.equals("ch.bfh.evoting.adminapp")){
		Intent i = new Intent("ch.bfh.evoting.adminapp.AdminWaitForVotesActivity");
		i.putExtra("poll", (Serializable)poll);
		startActivity(i);
	} else {
		Intent intent = new Intent(this, WaitForVotesActivity.class);
		intent.putExtra("poll", (Serializable)poll);
		startActivity(intent);
	}
}

}
