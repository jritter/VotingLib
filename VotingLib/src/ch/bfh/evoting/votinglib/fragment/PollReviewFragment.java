package ch.bfh.evoting.votinglib.fragment;

import java.io.Serializable;

import ch.bfh.evoting.votinglib.R;
import ch.bfh.evoting.votinglib.VoteActivity;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Fragment displaying the review of a poll
 * 
 */
public class PollReviewFragment extends ListFragment {

	private Poll poll;

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
		View v = inflater.inflate(R.layout.fragment_poll_review, container,
				false);

		ListView lv = (ListView)v.findViewById(android.R.id.list);

		View header = inflater.inflate(R.layout.review_header, null, false);
		lv.addHeaderView(header);
		View footer = inflater.inflate(R.layout.review_footer, null, false);
		lv.addFooterView(footer);

		String[] array = {};
		int[] toViews = {android.R.id.text1};
		lv.setAdapter(new SimpleCursorAdapter(this.getActivity(), android.R.layout.simple_list_item_1, null, array, toViews, 0));

		poll = (Poll)getActivity().getIntent().getSerializableExtra("poll");

		TextView tv_question = (TextView) header.findViewById(R.id.textview_poll_question);
		tv_question.setText(poll.getQuestion());

		//Create options table
		TableLayout optionsTable = (TableLayout)header.findViewById(R.id.layout_options);

		for(Option op : poll.getOptions()){
			TableRow tableRow= new TableRow(this.getActivity());
			tableRow.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

			View vItemOption = inflater.inflate(R.layout.list_item_option_poll, null);
			TextView tv_option = (TextView)vItemOption.findViewById(R.id.textview_poll_option_review);
			tv_option.setText(op.getText());

			tableRow.addView(vItemOption);
			tableRow.setBackgroundResource(R.drawable.borders);

			optionsTable.addView(tableRow);
		}

		//Create participants table
		TableLayout participantsTable = (TableLayout)footer.findViewById(R.id.layout_participants);

		for(Participant part : poll.getParticipants()){
			TableRow tableRow= new TableRow(this.getActivity());
			tableRow.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

			View vItemParticipant = inflater.inflate(R.layout.list_item_participant_poll, null);
			TextView tv_option = (TextView)vItemParticipant.findViewById(R.id.textview_participant_identification);
			tv_option.setText(part.getIdentification());

			tableRow.addView(vItemParticipant);
			tableRow.setBackgroundResource(R.drawable.borders);

			participantsTable.addView(tableRow);
		}

		String packageName = getActivity().getApplication().getApplicationContext().getPackageName();
		if(!packageName.equals("ch.bfh.evoting.adminapp")){
			LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {

					Intent i = new Intent(PollReviewFragment.this.getActivity(), VoteActivity.class);
					i.putExtra("poll", (Serializable) poll);
					startActivity(i);

				}
			}, new IntentFilter(BroadcastIntentTypes.goToVote));
		}
		
		simulate();

		return v;
	}


	//TODO remove, only for simulation
	private void simulate(){
		new AsyncTask<Object, Object, Object>(){
			@Override
			protected Object doInBackground(Object... arg0) {


				SystemClock.sleep(5000);

				//Send participants
				Intent intent = new Intent(BroadcastIntentTypes.goToVote);
				LocalBroadcastManager.getInstance(PollReviewFragment.this.getActivity()).sendBroadcast(intent);


				return null;
			}
		}.execute();
	}

}
