package ch.bfh.evoting.votinglib;

import java.io.Serializable;
import java.util.List;

import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Activity show when the participant has already submitted her vote but other voters are still voting
 * @author Philémon von Bergen
 *
 */
public class WaitForVotesActivity extends ListActivity {

	private int progressBarMaxValue = 0;
	private Poll poll;
	private List<Participant> participants;
	private ProgressBar pb;
	private int numberOfVotes;
	private WaitParticipantListAdapter wpAdapter;
	
	private  TextView castVotes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_for_votes);

		//Get data in the intent
		Intent intent = this.getIntent();
		poll = (Poll)intent.getSerializableExtra("poll");
		participants = poll.getParticipants();

		//Create the adapter for the ListView
		wpAdapter = new WaitParticipantListAdapter(this, R.layout.list_item_participant_wait, participants);
		this.setListAdapter(wpAdapter);
		
		//Initialize the progress bar 
		numberOfVotes = 0;
		pb=(ProgressBar)findViewById(R.id.progress_bar);
		progressBarMaxValue = pb.getMax();
				
		castVotes = (TextView)findViewById(R.id.cast_votes);
		castVotes.setText(getString(R.string.cast_votes, 0, participants.size()));
		
		
		//Register a BroadcastReceiver on participantStateUpdate events
		LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				numberOfVotes++;
				updateStatus(numberOfVotes);
			}
			
		}, new IntentFilter(BroadcastIntentTypes.participantStateUpdate));

		
		//TODO remove, only for simulation
		new AsyncTask<Object, Object, Object>(){
			@Override
			protected Object doInBackground(Object... arg0) {
				int pos = 0;
				while(numberOfVotes<participants.size()){
					SystemClock.sleep(2000);
					if(pos<poll.getParticipants().size()){
						poll.getParticipants().get(pos).setHasVoted(true);
						pos++;
						LocalBroadcastManager.getInstance(WaitForVotesActivity.this).sendBroadcast(new Intent(BroadcastIntentTypes.participantStateUpdate));
					}
					
					
				}
				return null;
			}
		}.execute();
	}
	
	@Override
	public void onBackPressed() {
		//do nothing because we don't want that people access to an anterior activity
	}

	/**
	 * Update the state of the progress bar, change the image of the participants when they have voted
	 * and start the activity which displays the results
	 * @param progress
	 */
	private void updateStatus(int numberOfReceivedVotes){
		//update progress bar and participants list
		int progress = numberOfReceivedVotes*progressBarMaxValue/poll.getParticipants().size();
		
		pb.setProgress(progress);
		wpAdapter.notifyDataSetChanged();
		castVotes.setText(getString(R.string.cast_votes, numberOfReceivedVotes, participants.size()));

		
		if(progress>=100){
			//TODO get through compute result and set result
			List<Option> options = poll.getOptions();
			for(Option option : options){
				option.setVotes(3);
				option.setPercentage(33.3);
			}
			
			poll.setTerminated(true);
			
			//start to result activity
			Intent intent = new Intent(this, DisplayResultActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			intent.putExtra("saveToDb", true);
			startActivity(intent);
		}
	}


}
