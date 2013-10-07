package ch.bfh.evoting.votinglib;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import ch.bfh.evoting.votinglib.adapters.OptionListAdapter;
import ch.bfh.evoting.votinglib.db.PollDbHelper;
import ch.bfh.evoting.votinglib.entities.DatabaseException;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.util.HelpDialogFragment;
import ch.bfh.evoting.votinglib.util.OptionsComparator;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity displaying the results of a poll
 * @author Philémon von Bergen
 *
 */
public class DisplayResultActivity extends ListActivity {

	private int pollId;
	private boolean saveToDbNeeded;
	
	private float values[] = { 700, 400, 100, 500,600 };  

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_result);
		setupActionBar();

		ListView lv = (ListView)findViewById(android.R.id.list);
		LayoutInflater inflater = this.getLayoutInflater();

		View header = inflater.inflate(R.layout.result_header, null, false);
		
		
		// Displaying the graph
		LinearLayout layoutGraph = (LinearLayout) header.findViewById(R.id.layout_graph);
		values = calculateData(values);  
		MyGraphview graphview = new MyGraphview(this, values);  
		layoutGraph.addView(graphview);  
		
		
		lv.addHeaderView(header);

		AndroidApplication.getInstance().getNetworkInterface().disconnect();

		//Get the data in the intent
		Poll poll = (Poll)this.getIntent().getSerializableExtra("poll");
		saveToDbNeeded = this.getIntent().getBooleanExtra("saveToDb", false);
		if(poll.getId()>=0){
			pollId = poll.getId();
		} else {
			pollId = -1;
		}

		//Set GUI informations
		TextView tvQuestion = (TextView)header.findViewById(R.id.textview_poll_question);
		tvQuestion.setText(poll.getQuestion());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date resultdate = new Date(poll.getStartTime());

		TextView tvPollTime = (TextView)header.findViewById(R.id.textview_poll_start_time);
		tvPollTime.setText(getString(R.string.poll_start_time) + ": " + sdf.format(resultdate));

		//Order the options in descending order
		Collections.sort(poll.getOptions(), new OptionsComparator());
		//Create the list
		setListAdapter(new OptionListAdapter(this, R.layout.list_item_result, poll.getOptions()));

		//Save the poll to the DB if needed
		if(saveToDbNeeded){
			try {
				if(pollId>=0){
					PollDbHelper.getInstance(this).updatePoll(pollId,poll);
				} else {
					int newPollId = (int)PollDbHelper.getInstance(this).savePoll(poll);
					this.pollId = newPollId;
					poll.setId(newPollId);
				}
			} catch (DatabaseException e) {
				Log.e(this.getClass().getSimpleName(), "DB error: "+e.getMessage());
				e.printStackTrace();
			}
		}  

	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}


	@Override
	public void onBackPressed() {
		if(!this.saveToDbNeeded){
			super.onBackPressed();
		} else {
			//do nothing we don't want that people reaccess to an old activity
		}
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
		//		if(item.getItemId()==R.id.action_delete) {
		//			if(this.pollId!=-1){
		//				//Confirmation dialog
		//				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		//				alertDialog.setTitle(R.string.delete_poll_confirm_title);
		//				alertDialog.setMessage(getString(R.string.delete_poll_confirm_text));
		//				alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",new DialogInterface.OnClickListener() {
		//					public void onClick(DialogInterface dialog,	int which) {
		//						PollDbHelper.getInstance(ctx).deletePoll(pollId);
		//						dialog.dismiss();
		//						startActivity(new Intent(ctx, ListTerminatedPollsActivity.class));
		//					}
		//				});
		//				alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
		//					public void onClick(DialogInterface dialog,	int which) {
		//						dialog.dismiss();
		//					}
		//				});
		//				alertDialog.show();
		//			}

		if (item.getItemId() == android.R.id.home){
			String packageName = getApplication().getApplicationContext().getPackageName();
			//if ending a poll
			if(saveToDbNeeded){
				if(packageName.equals("ch.bfh.evoting.voterapp")){
					Intent i = new Intent("ch.bfh.evoting.voterapp.VoterAppMainActivity");
					startActivity(i);
				} else if (packageName.equals("ch.bfh.evoting.adminapp")){
					Intent i = new Intent("ch.bfh.evoting.adminapp.AdminAppMainActivity");
					startActivity(i);
				}
			} else {
				//if consulting an archive
				startActivity(new Intent(this, ListTerminatedPollsActivity.class));
			}
			return true;
		} else if (item.getItemId() == R.id.help){
			HelpDialogFragment hdf = HelpDialogFragment.newInstance( getString(R.string.help_title_display_results), getString(R.string.help_text_display_results) );
	        hdf.show( getFragmentManager( ), "help" );
	        return true;
		}
		return true;
	}
	
	private float[] calculateData(float[] data) {
		float total = 0;
		for (int i = 0; i < data.length; i++) {
			total += data[i];
		}
		for (int i = 0; i < data.length; i++) {
			data[i] = 360 * (data[i] / total);
		}
		return data;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_results, menu);
		return true;
	}
	
	public class MyGraphview extends View {
		private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private float[] value_degree;
		RectF rectf = new RectF(120, 120, 380, 380);
		float temp = 0;

		public MyGraphview(Context context, float[] values) {
			super(context);
			value_degree = new float[values.length];
			for (int i = 0; i < values.length; i++) {
				value_degree[i] = values[i];
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Random r;
			for (int i = 0; i < value_degree.length; i++) {
				if (i == 0) {
					r = new Random();
					int color = Color.argb(100, r.nextInt(256), r.nextInt(256),
							r.nextInt(256));
					paint.setColor(color);
					canvas.drawArc(rectf, 0, value_degree[i], true, paint);
				} else {
					temp += value_degree[i - 1];
					r = new Random();
					int color = Color.argb(255, r.nextInt(256), r.nextInt(256),
							r.nextInt(256));
					paint.setColor(color);
					canvas.drawArc(rectf, temp, value_degree[i], true, paint);
				}
			}
		}
	}
}
