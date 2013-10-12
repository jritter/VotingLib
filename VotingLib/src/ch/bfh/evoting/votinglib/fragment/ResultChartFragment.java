/**
 * 
 */
package ch.bfh.evoting.votinglib.fragment;


import org.achartengine.GraphicalView;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import ch.bfh.evoting.votinglib.R;
import ch.bfh.evoting.votinglib.entities.Poll;
import ch.bfh.evoting.votinglib.util.PieChartView;

/**
 * @author tgdriju1
 *
 */
public class ResultChartFragment extends Fragment {
	
	String [] labels;
	float [] values;
	
	private Poll poll;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		poll = (Poll) getActivity().getIntent().getSerializableExtra("poll");
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result_chart, container,
				false);
		
		// Displaying the graph
		LinearLayout layoutGraph = (LinearLayout) v.findViewById(R.id.layout_result_chart);
		//values = calculateData(values);
		GraphicalView chartView = PieChartView.getNewInstance(getActivity(), poll);
		chartView.setClickable(true);
		
		
		chartView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ResultChartDialogFragment.newInstance().show(getFragmentManager(), "resultChartDialog");
			}
			
		});
		
		layoutGraph.addView(chartView);
		
		return v;
	}

	private float[] calculateData(float[] data) {
		float total = 0;
		for (int i = 0; i < data.length; i++) {
			total += data[i];
		}
		for (int i = 0; i < data.length; i++) {
			data[i] = 360 * (data[i] / total);
		}
		return data;
	}
	
	
}
