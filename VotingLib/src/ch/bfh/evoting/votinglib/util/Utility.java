package ch.bfh.evoting.votinglib.util;

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
	 * Source: http://stackoverflow.com/questions/9484038/how-to-force-height-of-listview-to-be-size-so-that-every-row-is-visible
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter(); 
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
