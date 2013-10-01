package ch.bfh.evoting.votinglib.fragment;

import java.io.Serializable;
import java.util.List;

import ch.bfh.evoting.votinglib.DisplayResultActivity;
import ch.bfh.evoting.votinglib.R;
import ch.bfh.evoting.votinglib.adapters.WaitParticipantListAdapter;
import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import android.app.Activity;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Fragment displaying the progress of the votes received
 * 
 */
public class WaitForVotesFragment extends ListFragment {
	
	private Poll poll;
	private int progressBarMaxValue = 0;
	private List<Participant> participants;
	private ProgressBar pb;
	private int numberOfVotes;
	private WaitParticipantListAdapter wpAdapter;
	private  TextView tvCastVotes;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_wait_for_votes, container,
				false);
		
		Intent intent = getActivity().getIntent();
		poll = (Poll)intent.getSerializableExtra("poll");
		
		if(poll==null) throw new RuntimeException("No poll defined in WaitForVotes fragment!");
		
		participants = poll.getParticipants();

		//Create the adapter for the ListView
		wpAdapter = new WaitParticipantListAdapter(this.getActivity(), R.layout.list_item_participant_wait, participants);
		this.setListAdapter(wpAdapter);

		//Initialize the progress bar 
		numberOfVotes = 0;
		pb=(ProgressBar)v.findViewById(R.id.progress_bar_votes);
		progressBarMaxValue = pb.getMax();

		tvCastVotes = (TextView)v.findViewById(R.id.textview_cast_votes);
		tvCastVotes.setText(getString(R.string.cast_votes, 0, participants.size()));

		//Register a BroadcastReceiver on participantStateUpdate events
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent intent) {
				numberOfVotes++;
				boolean stop = intent.getBooleanExtra("stop", false);
				updateStatus(numberOfVotes, stop);
			}

		}, new IntentFilter(BroadcastIntentTypes.participantStateUpdate));

		simulate();
		
		return v;
	}
	
	/**
	 * Update the state of the progress bar, change the image of the participants when they have voted
	 * and start the activity which displays the results
	 * @param progress
	 * @param stopOrder order sent from admin to stop the poll
	 */
	private void updateStatus(int numberOfReceivedVotes, boolean stopOrder){
		//update progress bar and participants list
		int progress = numberOfReceivedVotes*progressBarMaxValue/poll.getParticipants().size();

		pb.setProgress(progress);
		wpAdapter.notifyDataSetChanged();
		tvCastVotes.setText(getString(R.string.cast_votes, numberOfReceivedVotes, participants.size()));


		if(progress>=100 || stopOrder){
			//TODO get through compute result and set result
			List<Option> options = poll.getOptions();
			for(Option option : options){
				option.setVotes(3);
				option.setPercentage(33.3);
			}

			poll.setTerminated(true);

			//start to result activity
			Intent intent = new Intent(this.getActivity(), DisplayResultActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			intent.putExtra("saveToDb", true);
			startActivity(intent);
		}
	}

	//TODO remove, only for simulation
	private void simulate(){
		new AsyncTask<Object, Object, Object>(){
			@Override
			protected Object doInBackground(Object... arg0) {
				int pos = 0;
				while(numberOfVotes<participants.size()){
					SystemClock.sleep(2000);
					if(pos<poll.getParticipants().size()){
						poll.getParticipants().get(pos).setHasVoted(true);
						pos++;
						LocalBroadcastManager.getInstance(WaitForVotesFragment.this.getActivity()).sendBroadcast(new Intent(BroadcastIntentTypes.participantStateUpdate));
					}


				}
				return null;
			}
		}.execute();
	}
}
