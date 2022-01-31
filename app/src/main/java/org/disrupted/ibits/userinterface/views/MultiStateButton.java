
package org.disrupted.ibits.userinterface.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.disrupted.ibits.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author
 */
public class MultiStateButton extends LinearLayout {

    private final String TAG = "MultiStateButton";

    public interface OnMultiStateClickListener {
        void onMultiStateClick(int oldState, int newState);
    }

    protected TableLayout     mTableLayout;
    protected TableRow        mTableRow;
    protected int             widthTableRow;
    protected int             widthItem;
    protected int             heightItem;
    protected List<ImageView> buttonList;
    protected ImageView       mToggleSelector;
    protected int currentState;
    protected OnMultiStateClickListener onMultiStateClickListener;

    public MultiStateButton(Context context) {
        super(context);
        init();
    }

    public MultiStateButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_multistate_button_layout, this);
        mTableLayout    = (TableLayout)findViewById(R.id.multistate_table_layout);
        mTableRow       = (TableRow)findViewById(R.id.multi_state_description);
        mToggleSelector = (ImageView)findViewById(R.id.toggle_selector);

        buttonList = new ArrayList<>();
        onMultiStateClickListener = null;
        currentState = 0;
    }

    public void setSelected(int newState) {
        RelativeLayout.LayoutParams lp =
                (RelativeLayout.LayoutParams)mToggleSelector.getLayoutParams();
        lp.topMargin  = (mTableLayout.getMeasuredHeight()-heightItem)/2;
        lp.leftMargin = newState*widthItem + mTableLayout.getPaddingLeft();
        mToggleSelector.setLayoutParams(lp);

        if (Build.VERSION.SDK_INT < 11) {
            AlphaAnimation animation = new AlphaAnimation((float)0.2, (float)0.2);
            animation.setDuration(0);
            animation.setFillAfter(true);
            for(ImageView view : buttonList) {
                view.startAnimation(animation);
            }
            currentState = newState;
            animation = new AlphaAnimation(1, 1);
            animation.setDuration(0);
            animation.setFillAfter(true);
            buttonList.get(currentState).startAnimation(animation);
        } else {
            for(ImageView view : buttonList) {
                view.setAlpha((float) 0.2);
            }
            currentState = newState;
            buttonList.get(currentState).setAlpha((float) 1);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(changed){
            widthTableRow = mTableLayout.getMeasuredWidth()-mTableLayout.getPaddingLeft()-mTableLayout.getPaddingRight();
            widthItem  = widthTableRow / buttonList.size();
            heightItem = mTableLayout.getMeasuredHeight()-mTableLayout.getPaddingTop()-mTableLayout.getPaddingBottom();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(widthItem, heightItem);
            mToggleSelector.setLayoutParams(lp);
            setSelected(currentState);
        }
    }

    public void addState(int resource) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout buttonItem = (RelativeLayout)inflater.inflate(R.layout.item_multistate_button, null);
        ImageView button   = (ImageView)buttonItem.findViewById(R.id.item_image);
        button.setImageResource(resource);
        mTableRow.addView(buttonItem);
        buttonList.add(button);

        final int pos = (buttonList.size()-1);
        buttonItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onMultiStateClickListener != null) {
                    onMultiStateClickListener.onMultiStateClick(currentState, pos);
                }
            }
        });
    }

    public void setOnMultiStateClickListener(OnMultiStateClickListener callback) {
        this.onMultiStateClickListener = callback;
    }


    public void setStateResource(int state, int resource) {
        ImageView button   = buttonList.get(state);
        button.setImageResource(resource);
    }


    /*
     * disable and enable are suppose to lock the button when switching state can take a while
     */
    public void disable() {
    }

    public void enable () {
    }

    /*
     * tried to slide the button but it is not very convenient
    public int _xDelta;
    OnTouchListener dragSelector = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            RelativeLayout.LayoutParams lParams =
                    (RelativeLayout.LayoutParams) mToggleSelector.getLayoutParams();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    _xDelta = X - lParams.leftMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    if((X - _xDelta > 0) && ((X - _xDelta) < (widthTableRow-widthItem))){
                        lParams.leftMargin = X - _xDelta;
                        mToggleSelector.setLayoutParams(lParams);
                    }
                    break;
            }
            return true;
        }
    };
     */

}
