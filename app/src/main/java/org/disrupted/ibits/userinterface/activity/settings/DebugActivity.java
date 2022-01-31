package org.disrupted.ibits.userinterface.activity.settings;

import android.os.Bundle;

import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.disrupted.ibits.R;
import org.disrupted.ibits.app.EventLogger;
import org.disrupted.ibits.util.RumblePreferences;

/**
 * @author
 */
public class DebugActivity extends AppCompatActivity {

    private static final String TAG = "DebugActivity";

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_debug);
        setTitle(R.string.settings_debug);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        CheckBox checkBox = (CheckBox)findViewById(R.id.debug_check_box);
        checkBox.setChecked(RumblePreferences.isLogcatDebugEnabled(this));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                RumblePreferences.setLogcatDebugging(DebugActivity.this, isChecked);
                EventLogger.getInstance().init();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
    }
}
