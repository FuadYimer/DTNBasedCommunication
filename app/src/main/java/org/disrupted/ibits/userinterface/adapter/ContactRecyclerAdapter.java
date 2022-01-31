package org.disrupted.ibits.userinterface.adapter;

import android.app.Activity;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.disrupted.ibits.R;
import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.userinterface.activity.ContactDetailActivity;
import org.disrupted.ibits.util.TimeUtil;

import java.util.ArrayList;

/**
 * @author
 */
public class ContactRecyclerAdapter extends RecyclerView.Adapter<ContactRecyclerAdapter.ContactHolder> {

    public static final String TAG = "ContactRecyclerAdapter";

    public class ContactHolder extends RecyclerView.ViewHolder {

        ImageView contact_avatar;
        TextView  contact_name;
        TextView  contact_last_met;

        public ContactHolder(View itemView) {
            super(itemView);
            contact_avatar   = (ImageView) itemView.findViewById(R.id.contact_avatar);
            contact_name     = (TextView) itemView.findViewById(R.id.contact_name);
            contact_last_met = (TextView) itemView.findViewById(R.id.contact_last_met);
        }

        public void bindContact(Contact contact) {
            ColorGenerator generator = ColorGenerator.DEFAULT;
            contact_avatar.setImageDrawable(
                    builder.build(contact.getName().substring(0, 1),
                            generator.getColor(contact.getUid())));
            contact_name.setText(contact.getName());
            if (contact.isLocal()) {
                contact_last_met.setText(R.string.contact_is_local);
            } else {
                if(contact.lastMet() == 0)
                    contact_last_met.setText(R.string.contact_has_never_been_met);
                else
                    contact_last_met.setText(activity.getResources()
                            .getString(R.string.contact_last_met)
                            + " " + TimeUtil.timeElapsed(contact.lastMet()));
            }

            /*
             * Manage click events
             */
            final String    uid  = contact.getUid();
            final String    name = contact.getName();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent contactDetailActivity = new Intent(activity, ContactDetailActivity.class );
                    contactDetailActivity.putExtra("ContactID", uid);
                    contactDetailActivity.putExtra("ContactName",name);
                    activity.startActivity(contactDetailActivity);
                    activity.overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
                }
            });

        }
    }

    private Activity activity;
    private ArrayList<Contact> contactList;
    private static final TextDrawable.IBuilder builder = TextDrawable.builder().rect();


    public ContactRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.contactList = null;
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int i) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact_list, parent, false);
        return new ContactHolder(layout);
    }

    @Override
    public void onBindViewHolder(ContactHolder contactHolder, int i) {
        Contact contact = contactList.get(i);
        contactHolder.bindContact(contact);
    }

    @Override
    public int getItemCount() {
        if(contactList == null)
            return 0;
        else
            return contactList.size();
    }

    public void swap(ArrayList<Contact> contactList) {
        if(this.contactList != null)
            this.contactList.clear();
        this.contactList = contactList;
        notifyDataSetChanged();
    }
}
