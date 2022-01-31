package org.disrupted.ibits.userinterface.activity;

import android.os.Bundle;

import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import org.disrupted.ibits.R;
import org.disrupted.ibits.userinterface.fragments.FragmentStatusList;

/**
 * @author
 */
public class HashtagDetailActivity extends AppCompatActivity {

    private static final String TAG = "HashtagDetailActivity";

    private String hashtag;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getIntent().getExtras();
        hashtag = args.getString("Hashtag");

        setContentView(R.layout.activity_hashtag_detail);
        setTitle(hashtag);

        /* setting up the toolbar */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /* setting up the view pager and the tablayout */
        FragmentStatusList fragmentStatusList = new FragmentStatusList();
        fragmentStatusList.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentStatusList).commit();

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

