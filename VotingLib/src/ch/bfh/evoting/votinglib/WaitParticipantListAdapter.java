package ch.bfh.evoting.votinglib;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ch.bfh.evoting.votinglib.entities.Participant;

/**
 * Adapter listing the participants that already have voted and those who are still voting
 * This class is used in the Android ListView
 * @author Philémon von Bergen
 *
 */
public class WaitParticipantListAdapter extends ArrayAdapter<Participant> {

	private Context context;
	private List<Participant> values;
	
	/**
	 * Create an adapter object
	 * @param context android context
	 * @param textViewResourceId id of the layout that must be inflated
	 * @param objects list of participants that have to be displayed
	 */
	public WaitParticipantListAdapter(Context context, int textViewResourceId, List<Participant> objects) {
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
			view =  inflater.inflate(R.layout.list_item_participant_wait, parent, false);
		} else {
			view = convertView;
		}
		
		ImageView iv = (ImageView)view.findViewById(R.id.cast_img);
		//set the correct image
		if(this.values.get(position).hasVoted()==true){
			iv.setImageResource(R.drawable.cast);
		} else {
			iv.setImageResource(R.drawable.wait);
		}

		//set the participant identification
		TextView optionText =  (TextView)view.findViewById(R.id.participant_identification);
		optionText.setText(this.values.get(position).getIdentification());
		
		return view;
	}
	
	@Override
	public Participant getItem (int position)
	{
		return super.getItem (position);
	}

}
