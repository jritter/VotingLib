package ch.bfh.evoting.votinglib;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import ch.bfh.evoting.votinglib.entities.Option;

public class VoteOptionListAdapter extends ArrayAdapter<Option> {

	private Context context;
	private List<Option> values;
	
	public VoteOptionListAdapter(Context context, int textViewResourceId, List<Option> objects) {
		super(context, textViewResourceId, objects);
		this.context=context;
		this.values=objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View view;
		if (null == convertView) {
			//when view is created
			view =  inflater.inflate(R.layout.list_item_vote, parent, false);
			CheckBox cb = (CheckBox) view.findViewById (R.id.checkbox_vote);
			//Set a tag to get it again
			cb.setTag(position);
		} else {
			view = convertView;
		}

		TextView optionText =  (TextView)view.findViewById(R.id.vote_option);
		optionText.setText(this.values.get(position).getText());
		
		return view;
	}
	
	@Override
	public Option getItem (int position)
	{
		return super.getItem (position);
	}

}
