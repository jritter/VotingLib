package ch.bfh.evoting.votinglib.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import android.os.Bundle;
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
	private Map<String,Participant> participants;
	private ProgressBar pb;
	private WaitParticipantListAdapter wpAdapter;
	private TextView tvCastVotes;
	private int votesReceived = 0;
//	private Map<String, String> receivedVotes = new HashMap<String, String>();

	private BroadcastReceiver voteReceiver;
	private BroadcastReceiver stopReceiver;


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
		wpAdapter = new WaitParticipantListAdapter(this.getActivity(), R.layout.list_item_participant_wait, new ArrayList<Participant>(participants.values()));
		this.setListAdapter(wpAdapter);

		//Initialize the progress bar 
		pb=(ProgressBar)v.findViewById(R.id.progress_bar_votes);
		progressBarMaxValue = pb.getMax();

		tvCastVotes = (TextView)v.findViewById(R.id.textview_cast_votes);
		tvCastVotes.setText(getString(R.string.cast_votes, 0, participants.size()));

		//TODO for demo only
		votesReceived += intent.getIntExtra("votesReceived", 0);
		
		//Register a BroadcastReceiver on new incoming vote events
		voteReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent intent) {
				Option vote = (Option)intent.getSerializableExtra("vote");
				for(Option op : poll.getOptions()){
					if(op.equals(vote)){
						op.setVotes(op.getVotes()+1);
					}
				}
				String voter = intent.getStringExtra("voter");
				if(participants.containsKey(voter)){
					votesReceived++;
					participants.get(voter).setHasVoted(true);
				}
				updateStatus(votesReceived, false);
			}

		};
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(voteReceiver, new IntentFilter(BroadcastIntentTypes.newVote));

		//Register a BroadcastReceiver on stop poll order events
		stopReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context arg0, Intent intent) {
				updateStatus(votesReceived, true);
			}
		};
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(stopReceiver, new IntentFilter(BroadcastIntentTypes.stopVote));

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
			//go through compute result and set percentage result
			List<Option> options = poll.getOptions();
			for(Option option : options){
				if(numberOfReceivedVotes!=0){
					option.setPercentage(option.getVotes()*100/numberOfReceivedVotes);
				} else {
					option.setPercentage(0);
				}
			}

			poll.setTerminated(true);

			//start to result activity
			Intent intent = new Intent(this.getActivity(), DisplayResultActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			intent.putExtra("saveToDb", true);
			startActivity(intent);
			LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(stopReceiver);
			LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(voteReceiver);
		}
	}
}
