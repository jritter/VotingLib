package ch.bfh.evoting.votinglib.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import ch.bfh.evoting.votinglib.R;
import ch.bfh.evoting.votinglib.entities.Option;

/**
 * Adapter listing the different vote options that can be chosen in the vote
 * This class is used in the Android ListView
 * @author Philémon von Bergen
 *
 */
public class VoteOptionListAdapter extends ArrayAdapter<Option> {

	private Context context;
	private List<Option> values;
	private int selected = -1;

	/**
	 * Create an adapter object
	 * @param context android context
	 * @param textViewResourceId id of the layout that must be inflated
	 * @param objects list of options that can be chosen in the vote
	 */
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
		} else {
			view = convertView;
		}

		CheckedTextView ctv = (CheckedTextView)view.findViewById(R.id.radiobutton_vote);
		ctv.setText(this.values.get(position).getText());
		ctv.setTag(position);

		return view;
	}

	@Override
	public Option getItem (int position)
	{
		if(position >= this.getCount() || position < 0) return null;
		return super.getItem (position);
	}
	
	public Option getItemSelected(){
		return this.values.get(selected);
	}

}
