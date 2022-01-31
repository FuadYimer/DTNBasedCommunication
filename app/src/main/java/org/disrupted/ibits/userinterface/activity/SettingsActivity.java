package org.disrupted.ibits.userinterface.activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.disrupted.ibits.R;
import org.disrupted.ibits.userinterface.activity.settings.AboutActivity;
import org.disrupted.ibits.userinterface.activity.settings.DebugActivity;
import org.disrupted.ibits.userinterface.activity.settings.LicenceActivity;
import org.disrupted.ibits.userinterface.activity.settings.MiscellaneousActivity;
import org.disrupted.ibits.userinterface.activity.settings.StatisticActivity;
import org.disrupted.ibits.userinterface.activity.settings.StorageActivity;

/**
 * @author
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "Settings";

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        LinearLayout storage = (LinearLayout)findViewById(R.id.setting_storage);
        storage.setOnClickListener(openActivity(StorageActivity.class));

        LinearLayout stat = (LinearLayout)findViewById(R.id.setting_statistic);
        stat.setOnClickListener(openActivity(StatisticActivity.class));

        LinearLayout misc = (LinearLayout)findViewById(R.id.setting_misc);
        misc.setOnClickListener(openActivity(MiscellaneousActivity.class));

        LinearLayout about = (LinearLayout)findViewById(R.id.setting_about);
        about.setOnClickListener(openActivity(AboutActivity.class));

        LinearLayout debug = (LinearLayout)findViewById(R.id.setting_debug);
        debug.setOnClickListener(openActivity(DebugActivity.class));

        LinearLayout licence = (LinearLayout)findViewById(R.id.setting_licence);
        licence.setOnClickListener(openActivity(LicenceActivity.class));
    }

    public View.OnClickListener openActivity(final Class<?> cls) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeActivity = new Intent(SettingsActivity.this, cls);
                startActivity(homeActivity);
            }
        };
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
