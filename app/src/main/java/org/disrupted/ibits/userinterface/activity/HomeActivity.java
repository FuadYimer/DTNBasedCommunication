package org.disrupted.ibits.userinterface.activity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.disrupted.ibits.R;
import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.database.ChatMessageDatabase;
import org.disrupted.ibits.database.DatabaseExecutor;
import org.disrupted.ibits.database.DatabaseFactory;
import org.disrupted.ibits.database.PushStatusDatabase;
import org.disrupted.ibits.database.events.ChatMessageInsertedEvent;
import org.disrupted.ibits.database.events.ChatMessageUpdatedEvent;
import org.disrupted.ibits.database.events.StatusDatabaseEvent;
import org.disrupted.ibits.network.linklayer.bluetooth.BluetoothUtil;
import org.disrupted.ibits.userinterface.adapter.HomePagerAdapter;
import org.disrupted.ibits.userinterface.fragments.FragmentChatMessageList;
import org.disrupted.ibits.userinterface.fragments.FragmentNavigationDrawer;
import org.disrupted.ibits.userinterface.fragments.FragmentNetworkDrawer;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import de.greenrobot.event.EventBus;



public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private CharSequence mTitle;

    private FragmentNavigationDrawer mNavigationDrawerFragment;
    private FragmentNetworkDrawer mNetworkDrawerFragment;
    public SlidingMenu slidingMenu;

    private View notifStatus;
    private View notifChat;
    private ViewPager viewPager;
    private HomePagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTitle = getTitle();

        /* sliding menu with both right and left drawer */
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        slidingMenu.setMenu(R.layout.slidingmenu_navigation_drawer);
        slidingMenu.setSecondaryMenu(R.layout.slidingmenu_network_drawer);
        slidingMenu.setSecondaryShadowDrawable(R.drawable.shadowright);
        if (savedInstanceState == null) {
            mNavigationDrawerFragment = new FragmentNavigationDrawer();
            mNetworkDrawerFragment    = new FragmentNetworkDrawer();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.navigation_drawer_frame, mNavigationDrawerFragment).commit();
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.network_drawer_frame, mNetworkDrawerFragment).commit();
        } else {
            mNavigationDrawerFragment = (FragmentNavigationDrawer) this.getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_frame);
            mNetworkDrawerFragment = (FragmentNetworkDrawer) this.getSupportFragmentManager().findFragmentById(R.id.network_drawer_frame);
        }
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);

        /*
         * the tablayout for status and chat message with a viewpager.
         * note that we cannot swipe views with the viewpager as the swipe
         * gesture is catched by slidingmenu to slide the panel
         */
        TabLayout tabLayout = (TabLayout) findViewById(R.id.home_tab_layout);
        viewPager = (ViewPager) findViewById(R.id.home_viewpager);
        pagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
        tabLayout.setupWithViewPager(viewPager);

        // little hack to set the icons instead of text
        notifStatus = renderTabView(this, R.drawable.ic_world,  (ViewGroup)tabLayout.getParent());
        notifChat   = renderTabView(this, R.drawable.ic_forum_white_24dp, (ViewGroup)tabLayout.getParent());
        tabLayout.getTabAt(0).setCustomView(notifStatus);
        tabLayout.getTabAt(1).setCustomView(notifChat);
        tabLayout.setSelectedTabIndicatorHeight(10);

        // for notification
        refreshStatusNotifications();
        refreshChatNotifications();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            slidingMenu.toggle();
            return true;
        }
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(slidingMenu.isMenuShowing()) {
                slidingMenu.toggle();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /*
     * Receive Bluetooth Enable/Disable
     * We forward it to the FragmentNetworkDrawer
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BluetoothUtil.REQUEST_ENABLE_BT:
            case BluetoothUtil.REQUEST_ENABLE_DISCOVERABLE:
                mNetworkDrawerFragment.manageBTCode(requestCode, resultCode, data);
                break;
        }
    }

    public View renderTabView(Context context, int iconResource, ViewGroup parent) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.badge_tab_layout, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        ((ImageView)view.findViewById(R.id.tab_icon)).setImageResource(iconResource);
        ((TextView)view.findViewById(R.id.tab_badge)).setVisibility(View.INVISIBLE);
        return view;
    }

    public void refreshStatusNotifications() {
        PushStatusDatabase.StatusQueryOption statusQueryOption = new PushStatusDatabase.StatusQueryOption();
        statusQueryOption.filterFlags = PushStatusDatabase.StatusQueryOption.FILTER_READ;
        statusQueryOption.read = false;
        statusQueryOption.query_result = PushStatusDatabase.StatusQueryOption.QUERY_RESULT.COUNT;
        DatabaseFactory.getPushStatusDatabase(RumbleApplication.getContext()).getStatuses(statusQueryOption, onRefreshStatuses);
    }
    DatabaseExecutor.ReadableQueryCallback onRefreshStatuses = new DatabaseExecutor.ReadableQueryCallback() {
        @Override
        public void onReadableQueryFinished(Object object) {
            final Integer count = (Integer)object;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView view = (TextView) notifStatus.findViewById(R.id.tab_badge);
                    if (count > 0) {
                        view.setText(count.toString());
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    };

    public void refreshChatNotifications() {
        ChatMessageDatabase.ChatMessageQueryOption messageQueryOptions = new ChatMessageDatabase.ChatMessageQueryOption();
        messageQueryOptions.filterFlags = ChatMessageDatabase.ChatMessageQueryOption.FILTER_READ;
        messageQueryOptions.read = false;
        messageQueryOptions.query_result = ChatMessageDatabase.ChatMessageQueryOption.QUERY_RESULT.COUNT;
        DatabaseFactory.getChatMessageDatabase(RumbleApplication.getContext()).getChatMessage(messageQueryOptions, onRefreshChatMessages);
    }
    DatabaseExecutor.ReadableQueryCallback onRefreshChatMessages = new DatabaseExecutor.ReadableQueryCallback() {
        @Override
        public void onReadableQueryFinished(Object object) {
            final Integer count = (Integer)object;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView view = (TextView)notifChat.findViewById(R.id.tab_badge);
                    if (count > 0) {
                        view.setText(count.toString());
                        view.setVisibility(View.VISIBLE);
                    } else {
                        view.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    };

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //ignore
        }
        @Override
        public void onPageScrollStateChanged(int state) {
            // ignore
        }
        @Override
        public void onPageSelected(int position) {
            FragmentChatMessageList fragment = (FragmentChatMessageList) pagerAdapter.getItem(1);
            if(position == 1) {
                fragment.pageIn();
            } else {
                fragment.pageOut();
            }
        }
    };

    public boolean isChatHasFocus() {
        return (viewPager.getCurrentItem() == 1);
    }

    /*
     * Handling Events coming from outside the activity
     */
    public void onEvent(StatusDatabaseEvent event) {
        refreshStatusNotifications();
    }
    public void onEvent(ChatMessageInsertedEvent event) {
        refreshChatNotifications();
    }
    public void onEvent(ChatMessageUpdatedEvent event) {
        refreshChatNotifications();
    }

}