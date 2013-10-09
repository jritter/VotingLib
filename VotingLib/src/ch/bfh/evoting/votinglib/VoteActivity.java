package ch.bfh.evoting.votinglib;

import java.io.Serializable;
import java.util.List;

import ch.bfh.evoting.votinglib.adapters.VoteOptionListAdapter;
import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Participant;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.entities.VoteMessage;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import ch.bfh.evoting.votinglib.util.HelpDialogFragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.app.ListActivity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity displaying the vote options in the voting phase
 * @author Philémon von Bergen
 *
 */
public class VoteActivity extends ListActivity {

	//TODO remove static when no more needed
	static private Poll poll;
	static private List<Option> options;
	private String question;
	private VoteOptionListAdapter volAdapter;
	private int selectedPosition = -1;
	private boolean scrolled = false;
	private boolean demoScrollDone = false;

	static private int votesReceived = 0;
	static Context ctx;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vote);

		ctx=this;
		
		final ListView lv = (ListView)findViewById(android.R.id.list);

		//Get the data in the intent
		Intent intent = this.getIntent();
		poll = (Poll) intent.getSerializableExtra("poll");
		options = poll.getOptions();
		question = poll.getQuestion();

		//Set the question text
		TextView tvQuestion = (TextView)findViewById(R.id.textview_vote_poll_question);
		tvQuestion.setText(question);


		//Set a listener on the cast button
		Button btnCast = (Button)findViewById(R.id.button_cast_button);
		btnCast.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(!scrolled){
					Toast.makeText(VoteActivity.this, getString(R.string.scroll), Toast.LENGTH_SHORT).show();
				} else if (selectedPosition == -1){
					Toast.makeText(VoteActivity.this, getString(R.string.choose_one_option), Toast.LENGTH_SHORT).show();
				} else {
					castBallot();
				}
			}
		});

		//create the list of vote options
		volAdapter = new VoteOptionListAdapter(this, R.layout.list_item_vote, options);
		this.setListAdapter(volAdapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {

				selectedPosition = (Integer)view.getTag();

			}
		});

		lv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				//Check if the last view is visible
				Log.e("VoteActivity", "Scroll: firstVisible item="+firstVisibleItem+" visibleItemCount="+visibleItemCount+" totalItemCount="+totalItemCount);
				Log.e("VoteActivity", "DemoScrollDone: "+demoScrollDone);
				if (++firstVisibleItem + visibleItemCount > totalItemCount && demoScrollDone) {
					scrolled=true;
				}
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {}


		});

		//animate scroll
		new AsyncTask<Object, Object, Object>(){

			@Override
			protected Object doInBackground(Object... params) {
				SystemClock.sleep(300);
				if(lv.getLastVisiblePosition() < lv.getCount()-2){
					Log.d("VoteActivity", "Doing demo scroll");
					lv.smoothScrollToPositionFromTop(lv.getAdapter().getCount()-1, 0, 1000);
					SystemClock.sleep(1050);
					lv.smoothScrollToPositionFromTop(0, 0, 1000);
					SystemClock.sleep(1050);
					scrolled = false;
					demoScrollDone = true;
					return null;
				} else {
					Log.d("VoteActivity", "Demo scroll not needed");
					demoScrollDone = true;
					scrolled = true;
					return null;
				}
			}

		}.execute();

		this.startService(new Intent(this, VoteService.class));
	}


	@Override
	public void onBackPressed() {
		//do nothing because we don't want that people access to an anterior activity
	}

	/**
	 * Method called when cast button is clicked
	 */
	private void castBallot(){

		Option selectedOption = volAdapter.getItem(selectedPosition);

		if(selectedOption!=null){
			Log.e("vote", "Voted "+selectedOption.getText());
			AndroidApplication.getInstance().getNetworkInterface().sendMessage(new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_VOTE, selectedOption));
		} else {
			Log.e("vote", "Voted null");
			AndroidApplication.getInstance().getNetworkInterface().sendMessage(new VoteMessage(VoteMessage.Type.VOTE_MESSAGE_VOTE, null));
		}

		//Start activity waiting for other participants to vote
		//If is admin, returns to admin app wait activity
		String packageName = getApplication().getApplicationContext().getPackageName();
		if(packageName.equals("ch.bfh.evoting.adminapp")){
			Intent i = new Intent("ch.bfh.evoting.adminapp.AdminWaitForVotesActivity");
			i.putExtra("poll", (Serializable)poll);
			//TODO remove, for demo only
			i.putExtra("votesReceived", votesReceived);
			startActivity(i);
		} else {
			Intent intent = new Intent(this, WaitForVotesActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			//TODO remove, for demo only
			intent.putExtra("votesReceived", votesReceived);
			startActivity(intent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.help){
			HelpDialogFragment hdf = HelpDialogFragment.newInstance( getString(R.string.help_title_vote), getString(R.string.help_text_vote) );
			hdf.show( getFragmentManager( ), "help" );
			return true;
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vote, menu);
		return true;
	}
	
	//TODO remove: only for simulation
	public static class VoteService extends Service{

		boolean doWork = true;
		BroadcastReceiver voteReceiver;
		
		@Override
		public void onDestroy() {
			doWork = false;
			LocalBroadcastManager.getInstance(ctx).unregisterReceiver(voteReceiver);
			super.onDestroy();
		}

		@Override
		public int onStartCommand(Intent intent, int flags, int startId) {
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
					Log.e("Voteactivity", "voter "+voter);
					for(String s : poll.getParticipants().keySet()){
						Log.e("Voteactivity", "key "+s);
						Log.e("Voteactivity", "participant ip "+poll.getParticipants().get(s).getIpAddress());
					}
					Log.e("Voteactivity", "is contained "+poll.getParticipants().containsKey(voter));
					if(poll.getParticipants().containsKey(voter)){
						votesReceived++;
						poll.getParticipants().get(voter).setHasVoted(true);
					}
					
					new AsyncTask(){

						@Override
						protected Object doInBackground(Object... arg0) {
							while(doWork){
								Log.e("VoteActivity Async task", "sending "+votesReceived);
								Intent i = new Intent("UpdateNewVotes");
								i.putExtra("votes", votesReceived);
								i.putExtra("options", (Serializable)poll.getOptions());
								i.putExtra("participants", (Serializable)poll.getParticipants());
								LocalBroadcastManager.getInstance(ctx).sendBroadcast(i);
								SystemClock.sleep(1000);
							}
							return null;
						}
						
					}.execute();
				}
			};
			LocalBroadcastManager.getInstance(ctx).registerReceiver(voteReceiver, new IntentFilter(BroadcastIntentTypes.newVote));
			return super.onStartCommand(intent, flags, startId);
		}

		@Override
		public IBinder onBind(Intent arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		

	}

	
}
