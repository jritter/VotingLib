/**
 * 
 */
package ch.bfh.evoting.votinglib.fragment;

import java.util.Random;

import ch.bfh.evoting.votinglib.R;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * @author tgdriju1
 *
 */
public class ResultChartFragment extends Fragment {
	
	private float values[] = { 700, 400, 100, 500, 600 };  
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_result_chart, container,
				false);
		
		// Displaying the graph
		LinearLayout layoutGraph = (LinearLayout) v.findViewById(R.id.layout_result_chart);
		values = calculateData(values);
		MyGraphView graphview = new MyGraphView(getActivity(), values);
		layoutGraph.addView(graphview);
		
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
	
	public class MyGraphView extends View {
		private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private float[] value_degree;
		RectF rectf = new RectF(0, 0, 200, 200);
		float temp = 0;

		public MyGraphView(Context context, float[] values) {
			super(context);
			this.setMinimumHeight(200);
			this.setMinimumWidth(200);
			
			value_degree = new float[values.length];
			for (int i = 0; i < values.length; i++) {
				value_degree[i] = values[i];
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Random r;
			for (int i = 0; i < value_degree.length; i++) {
				if (i == 0) {
					r = new Random();
					int color = Color.argb(100, r.nextInt(256), r.nextInt(256),
							r.nextInt(256));
					paint.setColor(color);
					canvas.drawArc(rectf, 0, value_degree[i], true, paint);
				} else {
					temp += value_degree[i - 1];
					r = new Random();
					int color = Color.argb(255, r.nextInt(256), r.nextInt(256),
							r.nextInt(256));
					paint.setColor(color);
					canvas.drawArc(rectf, temp, value_degree[i], true, paint);
				}
			}
		}
	}
}
