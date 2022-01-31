package org.disrupted.ibits.userinterface.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.disrupted.ibits.R;
import org.disrupted.ibits.userinterface.fragments.FragmentContactInfo;

import java.util.ArrayList;

/**
 * @author
 */
public class ContactInfoRecyclerAdapter extends RecyclerView.Adapter<ContactInfoRecyclerAdapter.ContactInfoItemHolder> {

    public static final String TAG = "ContactInfoRecyclerAdapter";

    public class ContactInfoItemHolder extends RecyclerView.ViewHolder {

        public ContactInfoItemHolder(View itemView) {
            super(itemView);
        }

        public void bindInfoItem(FragmentContactInfo.ContactInfoItem infoItem) {
            TextView titleView  = (TextView) itemView.findViewById(R.id.contact_info_title);
            TextView dataView   = (TextView) itemView.findViewById(R.id.contact_info_data);
            titleView.setText(infoItem.title);
            dataView.setText(infoItem.data);
        }
    }


    private ArrayList<FragmentContactInfo.ContactInfoItem> infoList;

    public ContactInfoRecyclerAdapter() {
        this.infoList = null;
    }

    @Override
    public ContactInfoRecyclerAdapter.ContactInfoItemHolder onCreateViewHolder(ViewGroup parent, int i) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_info_list, parent, false);
        return new ContactInfoItemHolder(layout);
    }

    @Override
    public void onBindViewHolder(ContactInfoItemHolder contactInfoItemHolder, int i) {
        FragmentContactInfo.ContactInfoItem infoItem = infoList.get(i);
        contactInfoItemHolder.bindInfoItem(infoItem);
    }


    @Override
    public int getItemCount() {
        if(infoList == null)
            return 0;
        else
            return infoList.size();
    }

    public void swap(ArrayList<FragmentContactInfo.ContactInfoItem> infoList) {
        if(this.infoList != null)
            this.infoList.clear();
        this.infoList = infoList;
        notifyDataSetChanged();
    }
}
