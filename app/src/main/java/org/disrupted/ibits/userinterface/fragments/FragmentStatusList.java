
package org.disrupted.ibits.userinterface.fragments;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.disrupted.ibits.database.events.ContactTagInterestUpdatedEvent;
import org.disrupted.ibits.database.events.StatusInsertedEvent;
import org.disrupted.ibits.userinterface.activity.HomeActivity;
import org.disrupted.ibits.R;
import org.disrupted.ibits.database.PushStatusDatabase;
import org.disrupted.ibits.database.events.GroupDeletedEvent;
import org.disrupted.ibits.database.events.StatusDeletedEvent;
import org.disrupted.ibits.database.events.StatusUpdatedEvent;
import org.disrupted.ibits.database.events.StatusWipedEvent;
import org.disrupted.ibits.database.objects.PushStatus;
import org.disrupted.ibits.userinterface.activity.PopupComposeStatus;
import org.disrupted.ibits.database.DatabaseExecutor;
import org.disrupted.ibits.database.DatabaseFactory;
import org.disrupted.ibits.userinterface.adapter.FilterListAdapter;
import org.disrupted.ibits.userinterface.adapter.StatusRecyclerAdapter;

import java.util.ArrayList;
import java.util.HashSet;

import de.greenrobot.event.EventBus;


public class FragmentStatusList extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "FragmentStatusList";

    private View mView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeLayout;
    private StatusRecyclerAdapter statusRecyclerAdapter;
    private FilterListAdapter filterListAdapter;
    private ListView filters;
    private FloatingActionButton composeFAB;
    public  boolean noCoordinatorLayout;
    private boolean loadingMore;
    private boolean noMoreStatusToLoad;

    private String   filter_gid = null;
    private String   filter_uid = null;
    private String   filter_hashtag = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            this.filter_gid = args.getString("GroupID");
            this.filter_uid = args.getString("ContactID");
            this.filter_hashtag = args.getString("Hashtag");
            this.noCoordinatorLayout = args.getBoolean("noCoordinatorLayout");
        }

        /*
         * This fragment is shown in three activities: the HomeActivity, the GroupDetail activity
         * and the ContactDetail activity. For HomeActivity and GroupDetail, I need the floating
         * action button to compose message and I need it to disappear when I scroll down so I need
         * this fragment to embeds it in a CoordinatorLayout to enable this effect.
         *
         * However for ContactDetail activity, I need a CoordinatorLayout for the whole activity
         * in order to hide the collapsingtoolbar whenever I scroll down. Unfortunately it conflicts
         * with the coordinatorlayout I use for this very fragmentStatusList. Because I don't need
         * the compose button to display the status to a specific contact, I created two different
         * layout to avoid conflicts and use the argument noCoordinatorLayout to decide which one.
         */
        if(noCoordinatorLayout) {
            mView = inflater.inflate(R.layout.fragment_status_list_no_coordinatorlayout, container, false);
        } else {
            mView = inflater.inflate(R.layout.fragment_status_list, container, false);
        }

        // the filters
        filters = (ListView) (mView.findViewById(R.id.filter_list));
        filterListAdapter = new FilterListAdapter(getActivity(), this);
        filters.setAdapter(filterListAdapter);
        filters.setClickable(false);
        filters.setVisibility(View.GONE);

        // refreshing the list of status by pulling down, disabled for ContactDetail
        swipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_container);
        if(noCoordinatorLayout)
            swipeLayout.setEnabled(false);
        else
            swipeLayout.setOnRefreshListener(this);


        /*
        final float density = getResources().getDisplayMetrics().density;
        final int swipeDistance = Math.round(64 * density);
        swipeLayout.setProgressViewOffset(true, 10, 10+swipeDistance);
        */

        // the compose button, disabled for ContactDetail
        composeFAB = (FloatingActionButton) mView.findViewById(R.id.compose_fab);
        if(noCoordinatorLayout)
            composeFAB.setVisibility(View.GONE);
        else
            composeFAB.setOnClickListener(onFabClicked);

        // the list of status
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.status_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        statusRecyclerAdapter = new StatusRecyclerAdapter(getActivity(), this);
        mRecyclerView.setAdapter(statusRecyclerAdapter);
        mRecyclerView.addOnScrollListener(loadMore);

        // now get the latest status
        loadingMore = false;
        noMoreStatusToLoad = false;
        refreshStatuses();

        EventBus.getDefault().register(this);

        return mView;
    }

    @Override
    public void onDestroy() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        statusRecyclerAdapter.clean();
        super.onDestroy();
    }

    public View.OnClickListener onFabClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent compose = new Intent(getActivity(), PopupComposeStatus.class );
            if(filter_gid != null)
                compose.putExtra("GroupID",filter_gid);
            if(filter_hashtag != null)
                compose.putExtra("Hashtag",filter_hashtag);
            startActivity(compose);
        }
    };

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        refreshStatuses();
    }

    private void refreshStatuses() {
        refreshStatuses(-1,-1);
    }
    private void refreshStatuses(long before_toa, long after_toa) {
        if(loadingMore)
            return;
        loadingMore = true;

        PushStatusDatabase.StatusQueryOption options = new PushStatusDatabase.StatusQueryOption();
        options.answerLimit = 10;
        options.query_result = PushStatusDatabase.StatusQueryOption.QUERY_RESULT.LIST_OF_MESSAGE;
        options.order_by = PushStatusDatabase.StatusQueryOption.ORDER_BY.TIME_OF_ARRIVAL;

        if(before_toa > 0) {
            options.filterFlags |= PushStatusDatabase.StatusQueryOption.FILTER_BEFORE_TOA;
            options.before_toa = before_toa;
        }
        if(after_toa > 0) {
            options.filterFlags |= PushStatusDatabase.StatusQueryOption.FILTER_AFTER_TOA;
            options.after_toa = after_toa;
        }
        if(filter_gid != null) {
            options.filterFlags |= PushStatusDatabase.StatusQueryOption.FILTER_GROUP;
            options.groupIDFilters = new HashSet<String>();
            options.groupIDFilters.add(filter_gid);
        }
        if(filter_uid != null) {
            options.filterFlags |= PushStatusDatabase.StatusQueryOption.FILTER_AUTHOR;
            options.uid = filter_uid;
        }
        if ((filterListAdapter.getCount() != 0) || (filter_hashtag != null)) {
            options.filterFlags |= PushStatusDatabase.StatusQueryOption.FILTER_TAG;
            options.hashtagFilters = filterListAdapter.getFilterList();
            if(filter_hashtag != null)
                options.hashtagFilters.add(filter_hashtag);
        }
        if(before_toa <= 0) {
            DatabaseFactory.getPushStatusDatabase(getActivity())
                    .getStatuses(options, onStatusesRefreshed);
        } else {
            DatabaseFactory.getPushStatusDatabase(getActivity())
                    .getStatuses(options, onStatusesLoaded);
        }
    }
    DatabaseExecutor.ReadableQueryCallback onStatusesRefreshed = new DatabaseExecutor.ReadableQueryCallback() {
        @Override
        public void onReadableQueryFinished(final Object result) {
            final ArrayList<PushStatus> answer = (ArrayList<PushStatus>)result;
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusRecyclerAdapter.swap(answer);
                    statusRecyclerAdapter.notifyDataSetChanged();
                    swipeLayout.setRefreshing(false);
                    loadingMore = false;
                    noMoreStatusToLoad = false;

                    if (getActivity() != null) {
                        if(getActivity() instanceof HomeActivity)
                            ((HomeActivity) getActivity()).refreshChatNotifications();
                    }
                }
            });
        }
    };
    DatabaseExecutor.ReadableQueryCallback onStatusesLoaded = new DatabaseExecutor.ReadableQueryCallback() {
        @Override
        public void onReadableQueryFinished(final Object result) {
            final ArrayList<PushStatus> answer = (ArrayList<PushStatus>)result;
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int before = statusRecyclerAdapter.getItemCount();
                    int loaded = statusRecyclerAdapter.addStatusesAtBottom(answer);
                    if(loaded > 0) {
                        statusRecyclerAdapter.notifyItemRangeInserted(
                                before,
                                statusRecyclerAdapter.getItemCount() - 1
                        );
                    } else {
                        noMoreStatusToLoad = true;
                    }
                    swipeLayout.setRefreshing(false);
                    loadingMore = false;
                }
            });
        }
    };

    /*
     * Endless scrolling. Whenever we reach the last item, we load for more
     */
    RecyclerView.OnScrollListener loadMore = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            LinearLayoutManager mLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
            if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                if((!loadingMore) && (!noMoreStatusToLoad)) {
                    PushStatus status = statusRecyclerAdapter.getLastItem();
                    if(status == null)
                        return;
                    refreshStatuses(status.getTimeOfArrival(),-1);
                }
            }

            /*
             * since design version > 22, I can't use misc.ScrollAwareFABBehavior because
             * hide() makes the view to GONE and thus doesn't trigger the onNestedScroll
             * So instead we use the Recycler Scroll to trigger the hide/show.
             */
            if (dy > 0)
                composeFAB.hide();
            else if (dy < 0)
                composeFAB.show();
        }
    };

    /*
     * Hashtag List
     */
    public void onFilterClickCallrumbleback(String filter) {
        filterListAdapter.deleteFilter(filter);
        if(filterListAdapter.getCount() == 0)
            filters.setVisibility(View.GONE);
        refreshStatuses();
    }
    public void addFilter(String filter) {
        if((filter_hashtag != null) && (filter_hashtag.toLowerCase().equals(filter.toLowerCase())))
            return;
        if(filterListAdapter.getCount() == 0)
            filters.setVisibility(View.VISIBLE);
        if(filterListAdapter.addFilter(filter)) {
            refreshStatuses();
        }
    }

    /*
     * Status Events
     */
    public void onEvent(GroupDeletedEvent event) {
        refreshStatuses();
    }
    public void onEvent(StatusWipedEvent event) {
        refreshStatuses();
    }
    public void onEvent(StatusInsertedEvent event) {
        final PushStatus message = event.status;
        if(message.getAuthor().isLocal()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusRecyclerAdapter.addStatusOnTop(message);
                    statusRecyclerAdapter.notifyItemInserted(0);
                    mRecyclerView.smoothScrollToPosition(0);
                }
            });
        }
    }
    public void onEvent(StatusDeletedEvent event) {
        final String uuid = event.uuid;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
            int pos = statusRecyclerAdapter.deleteStatus(uuid);
            if(pos >= 0)
                statusRecyclerAdapter.notifyItemRemoved(pos);
            }
        });
    }
    public void onEvent(StatusUpdatedEvent event) {
        final PushStatus message = event.status;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if(statusRecyclerAdapter.updateStatus(message))
                //    statusRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }
    public void onEvent(ContactTagInterestUpdatedEvent event) {
        if(event.contact.isLocal()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    filterListAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
