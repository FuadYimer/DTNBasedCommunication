
package org.disrupted.ibits.userinterface.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.disrupted.ibits.R;
import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.database.DatabaseFactory;
import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.database.objects.Interface;
import org.disrupted.ibits.userinterface.adapter.ContactInfoRecyclerAdapter;
import org.disrupted.ibits.util.TimeUtil;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author
 */
public class FragmentContactInfo extends Fragment {


    public static final String TAG = "FragmentContactInfo";

    private View   mView;
    private String contact_uid = null;
    private RecyclerView mRecyclerView;
    private ContactInfoRecyclerAdapter mRecyclerAdapter;

    public static class ContactInfoItem {
        public String title;
        public String data;
        public ContactInfoItem(String title, String data) {
            this.title = title;
            this.data = data;
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        if(args != null) {
            this.contact_uid = args.getString("ContactID");
        }

        // inflate the view and bind the adapter
        mView = inflater.inflate(R.layout.fragment_contact_info, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.contact_info_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerAdapter = new ContactInfoRecyclerAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);

        // get the contact from DB
        Contact contact = DatabaseFactory.getContactDatabase(RumbleApplication.getContext())
                .getContact(contact_uid);
        String groupMembership = "";
        for(String gid : contact.getJoinedGroupIDs()) {
            groupMembership += DatabaseFactory.getGroupDatabase(getActivity()).getGroup(gid).getName();
            groupMembership += ", ";
        }
        groupMembership = groupMembership.substring(0,groupMembership.length()-2);

        // create the list of information to be displayed
        Resources resources = getActivity().getResources();
        ArrayList<ContactInfoItem> infoList = new ArrayList<ContactInfoItem>();
        infoList.add(new ContactInfoItem(
                resources.getString(R.string.contact_detail_name),
                contact.getName()
        ));
        infoList.add(new ContactInfoItem(
                resources.getString(R.string.contact_detail_uid),
                contact.getUid()
        ));
        infoList.add(new ContactInfoItem(
                resources.getString(R.string.contact_detail_last_met),
                TimeUtil.timeElapsed(contact.lastMet())
        ));
        infoList.add(new ContactInfoItem(
                resources.getString(R.string.contact_detail_group_membership),
                groupMembership
        ));
        infoList.add(new ContactInfoItem(
                resources.getString(R.string.contact_detail_nb_status_rcvd),
                contact.nbStatusReceived()+""
        ));
        infoList.add(new ContactInfoItem(
                resources.getString(R.string.contact_detail_nb_status_sent),
                contact.nbStatusSent()+""
        ));
        Set<Interface> interfaces = contact.getInterfaces();
        for(Interface iface : interfaces) {
            infoList.add(new ContactInfoItem(
                    resources.getString(R.string.contact_detail_interface),
                    iface.getMacAddress()
            ));
        }
        mRecyclerAdapter.swap(infoList);

        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
