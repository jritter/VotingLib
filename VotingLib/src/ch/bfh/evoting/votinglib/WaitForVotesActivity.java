package ch.bfh.evoting.votinglib;

import android.app.ListActivity;
import android.os.Bundle;

/**
 * Activity show when the participant has already submitted her vote but other voters are still voting
 * @author Philémon von Bergen
 *
 */
public class WaitForVotesActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_for_votes);
	}

	@Override
	public void onBackPressed() {
		//do nothing because we don't want that people access to an anterior activity
	}

}
