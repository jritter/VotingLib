
package ch.bfh.evoting.votinglib;

import java.util.List;

import ch.bfh.evoting.votinglib.entities.Option;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * List adapter to show the result as a list
 * @author von Bergen Philémon
 */
public class ResultListAdapter extends ArrayAdapter<Option> {

	private Context context;
	private List<Option> values;

	public ResultListAdapter(Context context,
			int textViewResourceId, List<Option> objects) {
		super(context, textViewResourceId, objects);
		this.context=context;
		this.values=objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View view;
		if (null == convertView) {
			view =  inflater.inflate(R.layout.list_item_result, parent, false);
		} else {
			view = convertView;
		}

		TextView resultOption =  (TextView)view.findViewById(R.id.result_lib_option);
		resultOption.setText(this.values.get(position).getText());
		
//		TextView resultVotes =  (TextView)view.findViewById(R.id.result_lib_received_votes);
//		String text = ""+this.values.get(position).getNumberOfVotes();
//		resultVotes.setText(text);
		
		//TODO
//		TextView resultPercentage =  (TextView)view.findViewById(R.id.result_lib_percentage);
//		String textPercentage = ""+this.values.get(position).getPercentage();
//		resultPercentage.setText(textPercentage);
		
		return view;
	}


}
