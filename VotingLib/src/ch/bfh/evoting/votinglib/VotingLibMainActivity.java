package ch.bfh.evoting.votinglib;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class VotingLibMainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voting_lib_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voting_lib_main, menu);
		return true;
	}

}
