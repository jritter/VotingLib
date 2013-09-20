package ch.bfh.evoting.votinglib;

import ch.bfh.evoting.votinglib.entities.DatabaseException;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.entities.Utility;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class DisplayResultActivity extends ListActivity {

	public DisplayResultActivity(){}

	private int pollId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_result);

		final Context context = this.getApplicationContext();

		Button btnClose = (Button) this.findViewById(R.id.result_lib_display_result_close_button);
		btnClose.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(context, ListTerminatedPollsActivity.class));
			}
		});

		Poll poll = (Poll)this.getIntent().getSerializableExtra("poll");
		boolean saveToDbNeeded = this.getIntent().getBooleanExtra("saveToDb", false);
		if(poll.getId()>=0){
			pollId = poll.getId();
		} else {
			pollId = -1;
		}

		TextView question = (TextView)findViewById(R.id.result_lib_display_result_question);
		question.setText(poll.getQuestion());

//		TextView nbr_voters = (TextView)findViewById(R.id.result_lib_display_result_voters);
//		nbr_voters.setText(getString(R.string.result_lib_display_result_nbr_voters) + ": " + String.valueOf(poll.getNumberOfVoters()));

		setListAdapter(new ResultListAdapter(this, R.layout.list_item_result, poll.getOptions()));
		Utility.setListViewHeightBasedOnChildren(this.getListView());
		((ScrollView)this.findViewById(R.id.result_lib_display_result_scrollview)).smoothScrollTo(0, 0);
		
		if(saveToDbNeeded){
			try {
				int newPollId = (int)PollDbHelper.getInstance(this).savePoll(poll);
				this.pollId = newPollId;
			} catch (DatabaseException e) {
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
		
		if(item.getItemId()==R.id.action_delete) {
			if(this.pollId!=-1){
				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
				alertDialog.setTitle(R.string.results_app_display_delete_confirm_title);
				alertDialog.setMessage(getString(R.string.results_app_display_delete_confirm_text));
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
