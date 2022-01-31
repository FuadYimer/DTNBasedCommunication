package org.disrupted.ibits.userinterface.adapter;

import android.app.Activity;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.disrupted.ibits.R;
import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.userinterface.activity.HashtagDetailActivity;
import org.disrupted.ibits.userinterface.events.UserSetHashTagInterest;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * @author
 */
public class HashtagRecyclerAdapter extends RecyclerView.Adapter<HashtagRecyclerAdapter.HashtagHolder> {

    public static final String TAG = "HashtagRecyclerAdapter";

    public class HashtagHolder extends RecyclerView.ViewHolder {

        TextView hashtag;
        TextView subscription;

        public HashtagHolder(View itemView) {
            super(itemView);
            hashtag       = (TextView) itemView.findViewById(R.id.hashtag);
            subscription  = (TextView) itemView.findViewById(R.id.subscription_button);
        }

        public void bindHashtag(final String hashtag) {
            this.hashtag.setText(hashtag);
            Integer interest = Contact.getLocalContact().getHashtagInterests().get(hashtag);
            final boolean isInterested = ((interest != null) && (interest > 0));
            if(isInterested) {
                subscription.setText(R.string.filter_subscribed);
                subscription.setTextColor(activity.getResources().getColor(R.color.white));
                subscription.setBackgroundColor(activity.getResources().getColor(R.color.green));
            } else {
                subscription.setText(R.string.filter_not_subscribed);
                subscription.setTextColor(activity.getResources().getColor(R.color.black));
                subscription.setBackgroundColor(activity.getResources().getColor(R.color.white));
            }

            subscription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isInterested)
                        EventBus.getDefault().post(new UserSetHashTagInterest(hashtag, Contact.MIN_INTEREST_TAG_VALUE));
                    else
                        EventBus.getDefault().post(new UserSetHashTagInterest(hashtag, Contact.MAX_INTEREST_TAG_VALUE));
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent hashtagStatusActivity = new Intent(activity, HashtagDetailActivity.class );
                    hashtagStatusActivity.putExtra("Hashtag",hashtag);
                    activity.startActivity(hashtagStatusActivity);
                    activity.overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
                }
            });
        }
    }

    private Activity activity;
    private ArrayList<String> hashtagList;

    public HashtagRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.hashtagList = null;
    }

    @Override
    public HashtagHolder onCreateViewHolder(ViewGroup parent, int i) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hashtag_list, parent, false);
        return new HashtagHolder(layout);
    }

    @Override
    public void onBindViewHolder(HashtagHolder hashtagHolder, int i) {
        String hashtag = hashtagList.get(i);
        hashtagHolder.bindHashtag(hashtag);
    }

    @Override
    public int getItemCount() {
        if(hashtagList == null)
            return 0;
        else
            return hashtagList.size();
    }

    public void swap(ArrayList<String> hashtagList) {
        if(this.hashtagList != null)
            this.hashtagList.clear();
        this.hashtagList = hashtagList;
        notifyDataSetChanged();
    }
}
