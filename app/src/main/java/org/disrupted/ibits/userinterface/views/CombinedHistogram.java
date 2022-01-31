
package org.disrupted.ibits.userinterface.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import org.disrupted.ibits.R;

/**
 * @author
 */
public class CombinedHistogram extends RelativeLayout {

    private final String TAG = "CombinedHistogram";

    private long appSize;
    private long dbSize;
    private long fileSize;

    private RelativeLayout relativeLayout;
    private View appSizeView;
    private View dbSizeView;
    private View fileSizeView;

    public CombinedHistogram(Context context) {
        super(context);
        appSize = 10;
        dbSize = 10;
        fileSize = 20;
        init();
    }

    public CombinedHistogram(Context context, AttributeSet attrs) {
        super(context, attrs);
        appSize = 1;
        dbSize = 1;
        fileSize = 1;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_combined_histogram, this);
        relativeLayout = (RelativeLayout)findViewById(R.id.combined_histogram_relative_layout);
        appSizeView    = relativeLayout.findViewById(R.id.app_size);
        dbSizeView     = relativeLayout.findViewById(R.id.db_size);
        fileSizeView   = relativeLayout.findViewById(R.id.file_size);
    }

    public void setSize(long appSize, long dbSize, long fileSize) {
        this.appSize  = appSize;
        this.dbSize   = dbSize;
        this.fileSize = fileSize;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed) {
            int widthLayout = relativeLayout.getMeasuredWidth();
            int heightLayout = relativeLayout.getMeasuredHeight();
            long totalSize = appSize + dbSize + fileSize;
            totalSize = (totalSize == 0) ? 1 : totalSize;

            int appWidth = (int) (appSize * widthLayout / totalSize);
            appWidth = (appWidth == 0) ? 2 : appWidth;
            int dbWidth = (int) (dbSize * widthLayout / totalSize);
            dbWidth = (dbWidth == 0) ? 2 : dbWidth;
            int fileWidth = (widthLayout - dbWidth - appWidth);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(appWidth, heightLayout);
            lp.leftMargin = 0;
            appSizeView.setLayoutParams(lp);

            lp = new RelativeLayout.LayoutParams(dbWidth, heightLayout);
            lp.leftMargin = appWidth;
            dbSizeView.setLayoutParams(lp);

            lp = new RelativeLayout.LayoutParams(fileWidth, heightLayout);
            lp.leftMargin = appWidth + dbWidth;
            fileSizeView.setLayoutParams(lp);
        }
    }

}
