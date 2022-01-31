package org.disrupted.ibits.userinterface.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.disrupted.ibits.R;
import org.disrupted.ibits.userinterface.fragments.FragmentHashtagList;

/**
 * @author
 */
public class HashtagListActivity extends AppCompatActivity {

    private static final String TAG = "HashtagsActivity";

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        setTitle(R.string.navigation_drawer_hashtag);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        Fragment fragmentHashtagList = new FragmentHashtagList();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragmentHashtagList)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id==android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
    }
}
