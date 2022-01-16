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

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.disrupted.rumble.R;
import org.disrupted.rumble.database.DatabaseFactory;
import org.disrupted.rumble.userinterface.activity.settings.AboutActivity;
import org.disrupted.rumble.userinterface.activity.settings.DebugActivity;
import org.disrupted.rumble.userinterface.activity.settings.LicenceActivity;
import org.disrupted.rumble.userinterface.activity.settings.MiscellaneousActivity;
import org.disrupted.rumble.userinterface.activity.settings.StatisticActivity;
import org.disrupted.rumble.userinterface.activity.settings.StorageActivity;
import org.disrupted.rumble.userinterface.events.UserWipeChatMessages;
import org.disrupted.rumble.userinterface.events.UserWipeData;
import org.disrupted.rumble.userinterface.events.UserWipeFiles;
import org.disrupted.rumble.userinterface.events.UserWipeStatuses;
import org.disrupted.rumble.userinterface.views.CombinedHistogram;
import org.disrupted.rumble.userinterface.views.SimpleHistogram;
import org.disrupted.rumble.util.FileUtil;

import java.io.File;
import java.io.IOException;

import de.greenrobot.event.EventBus;

/**
 * @author Lucien Loiseau
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
