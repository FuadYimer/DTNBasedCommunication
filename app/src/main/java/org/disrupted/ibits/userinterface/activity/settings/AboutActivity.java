package org.disrupted.ibits.userinterface.activity.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.disrupted.ibits.R;
import org.disrupted.ibits.app.RumbleApplication;

/**
 * @author
 */
public class AboutActivity extends AppCompatActivity {

    private static final String TAG = "AboutActivity";

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_about);
        setTitle(R.string.settings_about);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        TextView aboutVersion = (TextView)findViewById(R.id.about_version);
        aboutVersion.setText(RumbleApplication.BUILD_VERSION);


        TextView aboutProject = (TextView)findViewById(R.id.about_project);
        aboutProject.setText("DisruptedSystems (http://disruptedsystems.org/)");

        TextView aboutDeveloper = (TextView)findViewById(R.id.about_developer);
        aboutDeveloper.setText("Marlinski (http://marlinski.org/)");
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
