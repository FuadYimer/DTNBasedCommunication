
package org.disrupted.ibits.userinterface.fragments;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.disrupted.ibits.R;
import org.disrupted.ibits.database.DatabaseExecutor;
import org.disrupted.ibits.database.DatabaseFactory;
import org.disrupted.ibits.database.events.ContactTagInterestUpdatedEvent;
import org.disrupted.ibits.database.events.HashtagInsertedEvent;
import org.disrupted.ibits.userinterface.adapter.HashtagRecyclerAdapter;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * @author
 */
public class FragmentHashtagList extends Fragment {

    public static final String TAG = "FragmentContactList";

    private View mView;
    private RecyclerView mRecyclerView;
    private HashtagRecyclerAdapter hashtagRecyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_hashtag_list, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.hashtag_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        hashtagRecyclerAdapter = new HashtagRecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(hashtagRecyclerAdapter);
        EventBus.getDefault().register(this);
        getHashtagList();
        return mView;
    }

    @Override
    public void onDestroy() {
        if(EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void getHashtagList() {
        DatabaseFactory.getHashtagDatabase(getActivity())
                .getHashtags(onHashtagLoaded);
    }
    private DatabaseExecutor.ReadableQueryCallback onHashtagLoaded = new DatabaseExecutor.ReadableQueryCallback() {
        @Override
        public void onReadableQueryFinished(final Object result) {
            if(getActivity() == null)
                return;
            getActivity().runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> answer = (ArrayList<String>)(result);
                            hashtagRecyclerAdapter.swap(answer);
                        }
                    }
            );
        }
    };

    public void onEvent(HashtagInsertedEvent event) {
        getHashtagList();
    }
    public void onEvent(ContactTagInterestUpdatedEvent event) {
        if(event.contact.isLocal()) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hashtagRecyclerAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
