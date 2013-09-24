package ch.bfh.evoting.votinglib;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Participant;

public class WaitParticipantListAdapter extends ArrayAdapter<Participant> {

	private Context context;
	private List<Participant> values;
	
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
		
		if(this.values.get(position).hasVoted()==true){
			ImageView iv = (ImageView)view.findViewById(R.id.cast_img);
			iv.setImageResource(R.drawable.cast);
		}

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
