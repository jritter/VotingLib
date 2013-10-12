package ch.bfh.evoting.votinglib.util;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import ch.bfh.evoting.votinglib.entities.Option;
import ch.bfh.evoting.votinglib.entities.Poll;
import android.content.Context;
import android.graphics.Color;
/**
 * This is a content class which has hard coded values for color and value
 * variables that make up the slices in a pie chart. At the moment this is only
 * used to diplay the income, cost and sub total in the results view.
 * 
 * @author Daniel Kvist
 * 
 */
public class PieChartView extends GraphicalView {

	private static int BASE_COLOR;
	
	private static int BASE_COLOR_RED;
	private static int BASE_COLOR_GREEN;
	private static int BASE_COLOR_BLUE;

	private static int LABEL_COLOR;
	/**
	 * 
	 * http://danielkvist.net/code/piechart-with-achartengine-in-android
	 * 
	 * Constructor that only calls the super method. It is not used to
	 * instantiate the object from outside of this class.
	 * 
	 * @param context
	 * @param abstractChart
	 */
	private PieChartView(Context context, AbstractChart abstractChart) {
		super(context, abstractChart);
	}

	/**
	 * This method returns a new graphical view as a pie chart view. It uses the
	 * income and costs and the static color constants of the class to create
	 * the chart.
	 * 
	 * @param context
	 *            the context
	 * @param income
	 *            the total income
	 * @param costs
	 *            the total cost
	 * @return a GraphicalView object as a pie chart
	 */
	public static GraphicalView getNewInstance(Context context, Poll poll) {
		
		BASE_COLOR = context.getResources().getColor(android.R.color.holo_orange_dark);
		BASE_COLOR_RED = Color.red(BASE_COLOR);
		BASE_COLOR_GREEN = Color.green(BASE_COLOR);
		BASE_COLOR_BLUE = Color.blue(BASE_COLOR);
		
		LABEL_COLOR = context.getResources().getColor(android.R.color.black);
		
		return ChartFactory.getPieChartView(context,
				getDataSet(context, poll), getRenderer(poll));
	}

	/**
	 * Creates the renderer for the pie chart and sets the basic color scheme
	 * and hides labels and legend.
	 * 
	 * @return a renderer for the pie chart
	 */
	private static DefaultRenderer getRenderer(Poll poll) {

		int alpha = 255;
		DefaultRenderer defaultRenderer = new DefaultRenderer();
		for (int i = 0; i < poll.getOptions().size(); i++){
			SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
			simpleRenderer.setColor(Color.argb(alpha, BASE_COLOR_RED, BASE_COLOR_GREEN, BASE_COLOR_BLUE));
			defaultRenderer.addSeriesRenderer(simpleRenderer);
			
			alpha = alpha - (255 / poll.getOptions().size());
		}
		defaultRenderer.setLabelsColor(LABEL_COLOR);
		defaultRenderer.setLabelsTextSize(20);
		defaultRenderer.setShowLabels(true);
		defaultRenderer.setShowLegend(false);
		
		// Start at the 12 o clock position with drawing the slices
		defaultRenderer.setStartAngle(270);
		defaultRenderer.setAntialiasing(true);
		
		// Disable pan and zoom
		defaultRenderer.setPanEnabled(false);
		defaultRenderer.setZoomEnabled(false);
		
		defaultRenderer.setClickEnabled(true);
		return defaultRenderer;
	}

	/**
	 * Creates the data set used by the piechart by adding the string
	 * represantation and it's integer value to a CategorySeries object. Note
	 * that the string representations are hard coded.
	 * 
	 * @param context
	 *            the context
	 * @param income
	 *            the total income
	 * @param costs
	 *            the total costs
	 * @return a CategorySeries instance with the data supplied
	 */
	private static CategorySeries getDataSet(Context context, Poll poll) {
		CategorySeries series = new CategorySeries("Chart");
		
		for (Option option : poll.getOptions()){
			series.add(option.getText(), option.getPercentage());
		}
		
		return series;
	}
}
