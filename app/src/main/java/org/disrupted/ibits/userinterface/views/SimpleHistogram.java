
package org.disrupted.ibits.userinterface.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import org.disrupted.ibits.R;

/**
 * @author
 */
public class SimpleHistogram extends RelativeLayout {

    private final String TAG = "CombinedHistogram";

    private long size;
    private long total;

    private RelativeLayout relativeLayout;
    private View dataView;

    public SimpleHistogram(Context context) {
        super(context);
        size = 10;
        total=100;
        init();
    }

    public SimpleHistogram(Context context, AttributeSet attrs) {
        super(context, attrs);
        size = 10;
        total=100;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_simple_histogram, this);
        relativeLayout = (RelativeLayout)findViewById(R.id.combined_histogram_relative_layout);
        dataView = relativeLayout.findViewById(R.id.data);
    }

    public void setSize(long dataSize, long totalSize) {
        this.size  = dataSize;
        this.total = totalSize;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed) {
            int widthLayout = relativeLayout.getMeasuredWidth();
            int heightLayout = relativeLayout.getMeasuredHeight();
            int dataWidth = (int) (size * widthLayout / total);
            dataWidth = (dataWidth == 0) ? 2 : dataWidth;

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(dataWidth, heightLayout);
            dataView.setLayoutParams(lp);
        }
    }

    public void setColor(int resourceID) {
        dataView.setBackgroundColor(getResources().getColor(resourceID));
    }
}
