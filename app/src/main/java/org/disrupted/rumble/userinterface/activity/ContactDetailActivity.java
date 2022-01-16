/*
 * Copyright (C) 2014 Lucien Loiseau
 * This file is part of Rumble.
 * Rumble is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Rumble is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Rumble.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.disrupted.rumble.userinterface.activity;

import android.os.Bundle;

import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import org.disrupted.rumble.R;
import org.disrupted.rumble.userinterface.adapter.ContactDetailPagerAdapter;

/**
 * @author Lucien Loiseau
 */
public class ContactDetailActivity extends AppCompatActivity {


    private static final String TAG = "ContactDetailActivity";

    private String contactName;
    private String contactUID;

    private static final TextDrawable.IBuilder builder = TextDrawable.builder().rect();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contact_detail);

        Bundle args = getIntent().getExtras();
        String contactName = args.getString("ContactName");
        String contactUID  = args.getString("ContactID");

        /* set the toolbar */
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(contactName);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(contactName);

        /* set the background header */
        ImageView header = (ImageView) findViewById(R.id.header_background);
        ColorGenerator generator = ColorGenerator.DEFAULT;
        header.setBackgroundDrawable(
                builder.build(contactName.substring(0, 1),
                generator.getColor(contactUID)));

        /* setting up the view pager and the tablayout */
        TabLayout tabLayout = (TabLayout) findViewById(R.id.contact_tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.contact_viewpager);
        ContactDetailPagerAdapter pagerAdapter = new ContactDetailPagerAdapter(getSupportFragmentManager(), args);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorHeight(10);
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
