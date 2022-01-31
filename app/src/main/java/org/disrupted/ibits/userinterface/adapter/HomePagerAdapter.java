package org.disrupted.ibits.userinterface.adapter;

import android.content.Context;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.disrupted.ibits.R;
import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.userinterface.fragments.FragmentChatMessageList;
import org.disrupted.ibits.userinterface.fragments.FragmentStatusList;

/**
 * @author
 */
public class HomePagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private FragmentStatusList      statusFragment;
    private FragmentChatMessageList chatMessageFragment;

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
        statusFragment  = new FragmentStatusList();
        chatMessageFragment = new FragmentChatMessageList();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return (position == 0) ? statusFragment : chatMessageFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Context context = RumbleApplication.getContext();
        if(position == 0)
            return context.getResources().getString(R.string.group_detail_tab_message);
        else
            return context.getResources().getString(R.string.group_detail_tab_members);
    }

}