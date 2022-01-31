package org.disrupted.ibits.userinterface.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.disrupted.ibits.R;
import org.disrupted.ibits.database.objects.Group;
import org.disrupted.ibits.userinterface.events.UserJoinGroup;
import org.disrupted.ibits.util.Log;

import de.greenrobot.event.EventBus;

/**
 * @author
 */
public class PopupInputGroupKey extends Activity {

    private static final String TAG = "PopupCreateGroup";

    private LinearLayout  dismiss;
    private EditText      groupKey;
    private ImageButton   inputGroupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_input_group_key);

        dismiss              = (LinearLayout)(findViewById(R.id.new_group_dismiss));
        groupKey             = (EditText)(findViewById(R.id.popup_group_key));
        inputGroupButton     = (ImageButton)(findViewById(R.id.popup_button_input_group));

        dismiss.setOnClickListener(onDiscardClick);
        inputGroupButton.setOnClickListener(onInputGroup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    View.OnClickListener onDiscardClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(groupKey.getWindowToken(), 0);
            finish();
        }
    };

    View.OnClickListener onInputGroup = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Activity activity = PopupInputGroupKey.this;
            try {
                if (groupKey.getText().toString().equals(""))
                    return;
                Group group = Group.getGroupFromBase64ID(groupKey.getText().toString());
                if(group == null) {
                    //Snackbar.make(coordinatorLayout, "no group were added", Snackbar.LENGTH_SHORT)
                    //        .show();
                } else {
                    // add Group to database
                    EventBus.getDefault().post(new UserJoinGroup(group));
                    //Snackbar.make(coordinatorLayout, "the group " + group.getName() + " has been added", Snackbar.LENGTH_SHORT)
                    //        .show();
                }
            } catch (Exception e) {
                Log.e(TAG, "[!] " + e.getMessage());
            } finally {
                groupKey.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(groupKey.getWindowToken(), 0);
                finish();
            }
        }
    };

}
