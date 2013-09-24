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
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.widget.ProgressBar;

public class WaitForVotesActivity extends ListActivity {

	private int progressBarMaxValue = 0;
	private Poll poll;
	private List<Participant> participants;
	private ProgressBar pb;
	private int progress;
	private WaitParticipantListAdapter wpAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_for_votes);

		Intent intent = this.getIntent();
		poll = (Poll)intent.getSerializableExtra("poll");
		participants = poll.getParticipants();

		wpAdapter = new WaitParticipantListAdapter(this, R.layout.list_item_participant_wait, participants);
		this.setListAdapter(wpAdapter);
		
		progress = 0;
		pb=(ProgressBar)findViewById(R.id.progress_bar);
		progressBarMaxValue = pb.getMax();
		
		//Register a BroadcastReceiver on participantStateUpdate events
		LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				updateStatus(progress + progressBarMaxValue/poll.getParticipants().size());
			}
			
		}, new IntentFilter(BroadcastIntentTypes.participantStateUpdate));

		
		//TODO remove, only for simulation
		new AsyncTask<Object, Object, Object>(){
			@Override
			protected Object doInBackground(Object... arg0) {
				int pos = 0;
				while(progress<progressBarMaxValue){
					if(pos<poll.getParticipants().size()){
						poll.getParticipants().get(pos).setHasVoted(true);
						pos++;
					}
					LocalBroadcastManager.getInstance(WaitForVotesActivity.this).sendBroadcast(new Intent(BroadcastIntentTypes.participantStateUpdate));
					SystemClock.sleep(1000);
					
				}
				return null;
			}
		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.wait_for_votes, menu);
		return true;
	}

	public void updateStatus(int progress){
		this.progress = progress;
		pb.setProgress(progress);
		
		wpAdapter.notifyDataSetChanged();
		
		if(this.progress>=100){
			//TODO get through compute result and set result
			List<Option> options = poll.getOptions();
			for(Option option : options){
				option.setVotes(3);
				option.setPercentage(33.3);
			}
			
			poll.setTerminated(true);
			
			//go to result lib
			Intent intent = new Intent(this, DisplayResultActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			intent.putExtra("saveToDb", true);

			startActivity(intent);
		}
	}


}
