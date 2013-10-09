package ch.bfh.evoting.votinglib.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View view;
		if (null == convertView) {
			//when view is created
			view =  inflater.inflate(R.layout.list_item_vote, parent, false);			
		} else {
			view = convertView;
		}

		final RadioButton rbChoice = (RadioButton)view.findViewById(R.id.radiobutton_choice);
		final TextView tvContent = (TextView) view.findViewById(R.id.textview_content);

		tvContent.setText(this.values.get(position).getText());
		view.setTag(position);
		rbChoice.setTag(position);

		if (position == selected) {
			rbChoice.setChecked(true);
		} else {
			rbChoice.setChecked(false);
		}

		// set the click listener
		OnClickListener click = new OnClickListener() {
			@Override
			public void onClick(View v) {
				selected = position;
				VoteOptionListAdapter.this.notifyDataSetChanged();
			}
		};

		view.setOnClickListener(click);

		return view;
	}

	@Override
	public Option getItem(int position) {
		if (position >= this.getCount() || position < 0)
			return null;
		return super.getItem(position);
	}

	public Option getItemSelected(){
		return this.values.get(selected);
	}
	
	public int getSelectedPosition () {
		return selected;
	}
}
