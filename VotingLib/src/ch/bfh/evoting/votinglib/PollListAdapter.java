
package ch.bfh.evoting.votinglib;

import java.util.List;

import ch.bfh.evoting.votinglib.entities.Poll;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * List adapter to show the result as a list
 * @author von Bergen Philémon
 */
public class PollListAdapter extends ArrayAdapter<Poll> {

	private Context context;
	private List<Poll> values;

	public PollListAdapter(Context context,
			int textViewResourceId, List<Poll> objects) {
		super(context, textViewResourceId, objects);
		this.context=context;
		this.values=objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View view;
		if (null == convertView) {
			view =  inflater.inflate(R.layout.list_item_poll, parent, false);
		} else {
			view = convertView;
		}

		TextView poll_question =  (TextView)view.findViewById(R.id.result_lib_poll_list_question);
		poll_question.setText(this.values.get(position).getQuestion());
		
		return view;
	}

	public void onItemClick(AdapterView<?> listview, View view, int position, long id) {

		// extract the object assigned to the position which has been clicked
		Poll poll = (Poll) listview.getAdapter().getItem(position);

		Intent intent = new Intent(context, DisplayResultActivity.class);
		intent.putExtra("poll", poll);
		intent.putExtra("saveToDb", false);
		context.startActivity(intent);


	}

}
