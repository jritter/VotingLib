package ch.bfh.evoting.votinglib;

import java.io.Serializable;
import java.util.List;

import ch.bfh.evoting.votinglib.adapters.VoteOptionListAdapter;
import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.entities.VoteMessage;
import ch.bfh.evoting.votinglib.util.BroadcastIntentTypes;
import ch.bfh.evoting.votinglib.util.HelpDialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity displaying the vote options in the voting phase
 * @author Philémon von Bergen
 *
 */
public class VoteActivity extends Activity {

	//TODO remove static when no more needed
	static private Poll poll;
	static private List<Option> options;
	private String question;
	private VoteOptionListAdapter volAdapter;
	private boolean scrolled = false;
	private boolean demoScrollDone = false;

	private ListView lvChoices;
	private BroadcastReceiver stopReceiver;
	
	private Dialog dialogConfirmVote = null;

	static Context ctx;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplication.getInstance().setCurrentActivity(this);

		setContentView(R.layout.activity_vote);

		ctx=this;

		lvChoices = (ListView)findViewById(R.id.listview_choices);

		//Get the data in the intent
		Intent intent = this.getIntent();
		poll = (Poll) intent.getSerializableExtra("poll");
		options = poll.getOptions();
		question = poll.getQuestion();

		//Set the question text
		TextView tvQuestion = (TextView)findViewById(R.id.textview_vote_poll_question);
		tvQuestion.setText(question);




		//create the list of vote options
		volAdapter = new VoteOptionListAdapter(this, R.layout.list_item_vote, options);
		lvChoices.setAdapter(volAdapter);

		lvChoices.setOnScrollListener(new OnScrollListener() {

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
				if(lvChoices.getLastVisiblePosition() < lvChoices.getCount()-2){
					Log.d("VoteActivity", "Doing demo scroll");
					lvChoices.smoothScrollToPositionFromTop(lvChoices.getAdapter().getCount()-1, 0, 1000);
					SystemClock.sleep(1050);
					lvChoices.smoothScrollToPositionFromTop(0, 0, 1000);
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

		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


//		//Set a listener on the cast button
//		Button btnCast = (Button)findViewById(R.id.button_castvote);
//		btnCast.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				if(!scrolled){
//					Toast.makeText(VoteActivity.this, getString(R.string.scroll), Toast.LENGTH_SHORT).show();
//				} else if (volAdapter.getSelectedPosition() == -1){
//					Toast.makeText(VoteActivity.this, getString(R.string.choose_one_option), Toast.LENGTH_SHORT).show();
//				} else {
//					castBallot();
//				}
//			}
//		});

		//Register a BroadcastReceiver on stop poll order events
		stopReceiver = new BroadcastReceiver(){

			private int numberOfVotes = 0;
			@Override
			public void onReceive(Context arg0, Intent intent) {
				stopService(new Intent(VoteActivity.this, VoteService.class));
				//go through compute result and set percentage result
				List<Option> options = poll.getOptions();
				for(Option option : options){
					numberOfVotes += option.getVotes();	
				}
				for(Option option : options){
					if(numberOfVotes!=0){
						option.setPercentage(option.getVotes()*100/numberOfVotes);
					} else {
						option.setPercentage(0);
					}
				}

				poll.setTerminated(true);

				//start to result activity
				Intent i = new Intent(VoteActivity.this, DisplayResultActivity.class);
				i.putExtra("poll", (Serializable)poll);
				i.putExtra("saveToDb", true);
				startActivity(i);
				LocalBroadcastManager.getInstance(VoteActivity.this).unregisterReceiver(this);
			}
		};
		LocalBroadcastManager.getInstance(this).registerReceiver(stopReceiver, new IntentFilter(BroadcastIntentTypes.stopVote));


		this.startService(new Intent(this, VoteService.class));
	}


	@Override
	public void onBackPressed() {
		//do nothing because we don't want that people access to an anterior activity
	}

	/**
	 * Method called when cast button is clicked
	 */
	public void castBallot(){

		Option selectedOption = volAdapter.getItemSelected();

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
			i.putExtra("votes", VoteService.getInstance().getVotes());
			startActivity(i);
		} else {
			Intent intent = new Intent(this, WaitForVotesActivity.class);
			intent.putExtra("poll", (Serializable)poll);
			intent.putExtra("votes", VoteService.getInstance().getVotes());
			startActivity(intent);
		}
	}

	public boolean getScrolled(){
		return scrolled;
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
		AsyncTask<Object, Object, Object> sendVotesTask;
		private int votesReceived = 0;
		private static VoteService instance;

		@Override
		public void onCreate() {
			instance = this;
			super.onCreate();
		}

		@Override
		public void onDestroy() {
			LocalBroadcastManager.getInstance(ctx).unregisterReceiver(voteReceiver);
			votesReceived = 0;
			doWork=false;
			sendVotesTask.cancel(true);
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
					if(poll.getParticipants().containsKey(voter)){
						votesReceived++;
						poll.getParticipants().get(voter).setHasVoted(true);
					}

					sendVotesTask = new AsyncTask<Object, Object, Object>(){

						@Override
						protected Object doInBackground(Object... arg0) {
							while(doWork){
								Intent i = new Intent("UpdateNewVotes");
								i.putExtra("votes", votesReceived);
								i.putExtra("options", (Serializable)poll.getOptions());
								i.putExtra("participants", (Serializable)poll.getParticipants());
								LocalBroadcastManager.getInstance(ctx).sendBroadcast(i);
								SystemClock.sleep(1000);
							}
							return null;
						}

					}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			};
			LocalBroadcastManager.getInstance(ctx).registerReceiver(voteReceiver, new IntentFilter(BroadcastIntentTypes.newVote));
			return super.onStartCommand(intent, flags, startId);
		}

		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}

		public static VoteService getInstance(){
			return instance;
		}

		public int getVotes(){
			return this.votesReceived;
		}

	}

	protected void onResume() {
		super.onResume();
		AndroidApplication.getInstance().setCurrentActivity(this);
	}
	protected void onPause() {
		AndroidApplication.getInstance().setCurrentActivity(null);
		super.onPause();
	}
	protected void onDestroy() {        
		AndroidApplication.getInstance().setCurrentActivity(null);
		super.onDestroy();
	}
}
