package ch.bfh.evoting.votinglib;

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.bfh.evoting.votinglib.entities.DatabaseException;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.util.Utility;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Activity displaying the results of a poll
 * @author Philémon von Bergen
 *
 */
public class DisplayResultActivity extends ListActivity {

	private int pollId;

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_result);

		//Create the listener of the button
		final Context context = this.getApplicationContext();
		Button btnClose = (Button) this.findViewById(R.id.display_result_close_button);
		btnClose.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, ListTerminatedPollsActivity.class));
			}
		});

		//Get the data in the intent
		Poll poll = (Poll)this.getIntent().getSerializableExtra("poll");
		boolean saveToDbNeeded = this.getIntent().getBooleanExtra("saveToDb", false);
		if(poll.getId()>=0){
			pollId = poll.getId();
		} else {
			pollId = -1;
		}

		//Set GUI informations
		TextView question = (TextView)findViewById(R.id.poll_question);
		question.setText(poll.getQuestion());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date resultdate = new Date(poll.getStartTime());
        
		TextView poll_time = (TextView)findViewById(R.id.poll_start_time);
		poll_time.setText(getString(R.string.poll_start_time) + ": " + sdf.format(resultdate));
		
		//Create the list
		setListAdapter(new OptionListAdapter(this, R.layout.list_item_result, poll.getOptions()));
		Utility.setListViewHeightBasedOnChildren(this.getListView(), false);
		((ScrollView)this.findViewById(R.id.scrollview)).smoothScrollTo(0, 0);
		
		//Save the poll to the DB if needed
		if(saveToDbNeeded){
			try {
				int newPollId = (int)PollDbHelper.getInstance(this).savePoll(poll);
				this.pollId = newPollId;
				poll.setId(newPollId);
			} catch (DatabaseException e) {
				Log.e(this.getClass().getSimpleName(), "DB error: "+e.getMessage());
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_result, menu);
		return true;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		final Context ctx = this.getApplicationContext();
		
		//Delete option of the menu
		if(item.getItemId()==R.id.action_delete) {
			if(this.pollId!=-1){
				//Confirmation dialog
				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle(R.string.delete_poll_confirm_title);
				alertDialog.setMessage(getString(R.string.delete_poll_confirm_text));
				alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,	int which) {
						PollDbHelper.getInstance(ctx).deletePoll(pollId);
						dialog.dismiss();
						startActivity(new Intent(ctx, ListTerminatedPollsActivity.class));
					}
				});
				alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,	int which) {
						dialog.dismiss();
					}
				});
				alertDialog.show();
			}

		}
		return true;
	}

}
