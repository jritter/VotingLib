package ch.bfh.evoting.votinglib;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.entities.Utility;
import ch.bfh.evoting.votinglib.util.JavaSerialization;
import ch.bfh.evoting.votinglib.util.SerializationUtil;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

public class VoteActivity extends ListActivity {

	SparseBooleanArray boxChecked = new SparseBooleanArray();
	Poll poll;
	List<Option> options;
	String question;
	boolean isAdmin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote);


		Intent intent = this.getIntent();
		poll = (Poll) intent.getSerializableExtra("poll");
		isAdmin = intent.getBooleanExtra("isAdmin", false);

		options = poll.getOptions();
		question = poll.getQuestion();

		TextView questionView = (TextView)findViewById(R.id.poll_question);
		questionView.setText(question);

		Button cast = (Button)findViewById(R.id.cast_button);
		cast.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				castBallot();
			}
		});

		this.setListAdapter(new VoteOptionListAdapter(this, R.layout.list_item_vote, options));
		Utility.setListViewHeightBasedOnChildren(this.getListView());
		((ScrollView)this.findViewById(R.id.scrollview)).smoothScrollTo(0, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vote, menu);
		return true;
	}

	public void handleCheckBoxEvent(View v) {
		CheckBox cb = (CheckBox) v;

		//get position of the checkbox
		int position = Integer.parseInt(cb.getTag().toString());

		//Put the element in the list
		if (cb.isChecked()) {
			boxChecked.put(position, true);
		} else {
			boxChecked.delete(position);
		}
	}

	private void castBallot(){
		//TODO
		//get checked option in the SparseBooleanArray boxChecked
		//send vote
		if(this.boxChecked.size()>1){
			throw new RuntimeException("more than one checked");
		}

		//If is admin, returns to admin app wait activity
		if(isAdmin){
			//TODO put admin wait activity
			Intent intent = new Intent(this, WaitForVotesActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this, WaitForVotesActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			startActivity(intent);
		}
	}

}
