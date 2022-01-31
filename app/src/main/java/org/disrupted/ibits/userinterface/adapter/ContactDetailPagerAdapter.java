package org.disrupted.ibits.userinterface.adapter;

import android.content.Context;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import org.disrupted.ibits.R;
import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.userinterface.fragments.FragmentContactInfo;
import org.disrupted.ibits.userinterface.fragments.FragmentStatusList;

/**
 * @author
 */
public class ContactDetailPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    private FragmentStatusList  statusFragment;
    private FragmentContactInfo infoFragment;

    public ContactDetailPagerAdapter(FragmentManager fm, Bundle args) {
        super(fm);
        infoFragment = new FragmentContactInfo();
        infoFragment.setArguments(args);

        statusFragment  = new FragmentStatusList();
        args.putBoolean("noCoordinatorLayout",true);
        statusFragment.setArguments(args);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return (position == 0) ? statusFragment : infoFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Context context = RumbleApplication.getContext();
        if(position == 0)
            return context.getResources().getString(R.string.contact_detail_tab_message);
        else
            return context.getResources().getString(R.string.contact_detail_tab_info);
    }

}
