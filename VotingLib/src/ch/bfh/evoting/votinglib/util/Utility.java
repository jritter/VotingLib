package ch.bfh.evoting.votinglib.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Utility class 
 * @author Philémon von Bergen
 *
 */
public class Utility {

	/**
	 * Method setting the height of a ListView when it is used in a ScrollView.
	 * Android doesn't support inserting ListView in ScrollView, what result in displaying only one line
	 * of the ListView. The method is a workaround which allows to display all lines of the ListView.
	 * @param listView
	 * @param wrapContent true if each item height has to wrap its content, false if the item height is fixed in the xml layout
	 * Inspired from: http://stackoverflow.com/questions/9484038/how-to-force-height-of-listview-to-be-size-so-that-every-row-is-visible
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView, boolean wrapContent) {
		ListAdapter listAdapter = listView.getAdapter(); 
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		if(wrapContent){
			//if item height is wrapContent
			int totalHeight = 0;
			for (int i = 0; i < listAdapter.getCount(); i++) {
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}
			params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		} else {
			//if item height is fixed in xml layout
			int itemHeight = listAdapter.getView(0, null, listView).getLayoutParams().height;
			params.height = (itemHeight*listAdapter.getCount()) + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		}
		listView.setLayoutParams(params);
		listView.requestLayout();
	}
}
