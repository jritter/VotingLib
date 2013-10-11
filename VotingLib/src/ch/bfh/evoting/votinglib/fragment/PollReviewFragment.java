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
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
	private View header;
	private View footer;
	private LayoutInflater inflater;
	private Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		this.ctx = this.getActivity();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater= inflater; 
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.fragment_poll_review, container,
				false);

		ListView lv = (ListView)v.findViewById(android.R.id.list);

		header = inflater.inflate(R.layout.review_header, null, false);
		lv.addHeaderView(header);
		footer = inflater.inflate(R.layout.review_footer, null, false);
		lv.addFooterView(footer);

		String[] array = {};
		int[] toViews = {android.R.id.text1};
		lv.setAdapter(new SimpleCursorAdapter(this.getActivity(), android.R.layout.simple_list_item_1, null, array, toViews, 0));

		poll = (Poll)getActivity().getIntent().getSerializableExtra("poll");
		String sender = getActivity().getIntent().getStringExtra("sender");
		poll.getParticipants().get(sender).setHasAcceptedReview(true);

		updateView();

		String packageName = getActivity().getApplication().getApplicationContext().getPackageName();
		if(!packageName.equals("ch.bfh.evoting.adminapp")){
			LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {

					Intent i = new Intent(PollReviewFragment.this.getActivity(), VoteActivity.class);
					poll.setStartTime(System.currentTimeMillis());
					i.putExtra("poll", (Serializable) poll);
					startActivity(i);
					LocalBroadcastManager.getInstance(PollReviewFragment.this.getActivity()).unregisterReceiver(this);

				}
			}, new IntentFilter(BroadcastIntentTypes.startVote));
		}

		//broadcast receiving the poll if it was modified
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {

				poll = (Poll)intent.getSerializableExtra("poll");
				//Poll is not in the DB, so reset the id
				poll.setId(-1);
				String sender = intent.getStringExtra("sender");
				Log.e("PollReviewFragment", sender);
				poll.getParticipants().get(sender).setHasAcceptedReview(true);
				updateView();
			}
		}, new IntentFilter(BroadcastIntentTypes.pollToReview));

		//broadcast receiving the poll review acceptations
		BroadcastReceiver reviewAcceptsReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String participantAccept = intent.getStringExtra("participant");
				poll.getParticipants().get(participantAccept).setHasAcceptedReview(true);
				updateView();
			}
		};
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(reviewAcceptsReceiver, new IntentFilter(BroadcastIntentTypes.acceptReview));

		return v;
	}

	private void updateView(){
		TextView tv_question = (TextView) header.findViewById(R.id.textview_poll_question);
		tv_question.setText(poll.getQuestion());

		//Create options table
		TableLayout optionsTable = (TableLayout)header.findViewById(R.id.layout_options);
		optionsTable.removeAllViews();

		for(Option op : poll.getOptions()){
			TableRow tableRow= new TableRow(ctx);
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
		participantsTable.removeAllViews();

		for(Participant part : poll.getParticipants().values()){
			TableRow tableRow= new TableRow(ctx);
			tableRow.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));

			View vItemParticipant = inflater.inflate(R.layout.list_item_participant_poll, null);
			TextView tv_participant = (TextView)vItemParticipant.findViewById(R.id.textview_participant_identification);
			tv_participant.setText(part.getIdentification());
			
			ImageView ivAcceptImage = (ImageView)vItemParticipant.findViewById(R.id.imageview_accepted_img);
			ProgressBar pgWaitForAccept = (ProgressBar)vItemParticipant.findViewById(R.id.progress_bar_waitforaccept);
			
			//set the correct image
			if(part.hasAcceptedReview()){
				pgWaitForAccept.setVisibility(View.GONE);
				ivAcceptImage.setVisibility(View.VISIBLE);
			} else {
				pgWaitForAccept.setVisibility(View.VISIBLE);
				ivAcceptImage.setVisibility(View.GONE);
			}

			tableRow.addView(vItemParticipant);
			tableRow.setBackgroundResource(R.drawable.borders);

			participantsTable.addView(tableRow);
		}
	}
}
