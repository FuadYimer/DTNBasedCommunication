package org.disrupted.ibits.userinterface.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.squareup.picasso.Picasso;

import org.disrupted.ibits.R;
import org.disrupted.ibits.database.objects.Group;
import org.disrupted.ibits.userinterface.activity.DisplayQRCode;
import org.disrupted.ibits.userinterface.activity.GroupDetailActivity;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author
 */
public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.GroupHolder>  {

    public static final String TAG = "GroupListAdapter";

    public class GroupHolder extends RecyclerView.ViewHolder {

        LinearLayout title;
        ImageView group_lock;
        TextView  group_name;
        TextView  group_unread;
        TextView  group_desc;
        ImageView group_invite;

        public GroupHolder(View itemView) {
            super(itemView);

            title        = (LinearLayout) itemView.findViewById(R.id.group_title);
            group_lock   = (ImageView)    itemView.findViewById(R.id.group_lock_image);
            group_name   = (TextView)     itemView.findViewById(R.id.group_name);
            group_unread = (TextView)     itemView.findViewById(R.id.group_unread_msg);
            group_desc   = (TextView)     itemView.findViewById(R.id.group_desc);
            group_invite = (ImageView)   itemView.findViewById(R.id.group_invite);
        }

        public void bindGroup(Group group, int unread) {

            //group_name.setTextColor(ColorGenerator.DEFAULT.getColor(groupList.get(i).getName()));
            if(group.isPrivate())
                Picasso.get()
                        .load(R.drawable.ic_lock_grey600_24dp)
                        .into(group_lock);
            else
                Picasso.get()
                        .load(R.drawable.ic_lock_open_grey600_24dp)
                        .into(group_lock);

            group_name.setText(group.getName());
            group_unread.setText(""+unread);
            if(unread == 0) {
                group_unread.setVisibility(View.INVISIBLE);
            } else {
                group_unread.setVisibility(View.VISIBLE);
            }

            if(group.getDesc().equals(""))
                group_desc.setVisibility(View.GONE);
            else
                group_desc.setText(activity.getString(R.string.description)+ " " + group.getDesc());


            /*
             * Manage click events
             */
            final String    gid           = group.getGid();
            final String    name          = group.getName();
            final String    groupBase64ID = group.getGroupBase64ID();

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent groupStatusActivity = new Intent(activity, GroupDetailActivity.class );
                    groupStatusActivity.putExtra("GroupID",gid);
                    groupStatusActivity.putExtra("GroupName",name);
                    activity.startActivity(groupStatusActivity);
                    activity.overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
                }
            });

            group_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int size = 200;
                    Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
                    hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                    QRCodeWriter qrCodeWriter = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = qrCodeWriter.encode(groupBase64ID, BarcodeFormat.QR_CODE, size, size, hintMap);
                        Bitmap image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);

                        if(image != null) {
                            for (int i = 0; i < size; i++) {
                                for (int j = 0; j < size; j++) {
                                    image.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                                }
                            }
                            Intent intent = new Intent(activity, DisplayQRCode.class);
                            intent.putExtra("EXTRA_GROUP_NAME", name);
                            intent.putExtra("EXTRA_BUFFER", groupBase64ID);
                            intent.putExtra("EXTRA_QRCODE", image);
                            activity.startActivity(intent);
                        }
                    }catch(WriterException ignore) {
                    }
                }
            });


        }
    }

    private Activity activity;
    private ArrayList<Group> groupList;
    private ArrayList<Integer> unreadList;

    public GroupRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.groupList = null;
    }

    @Override
    public GroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_list, parent, false);
        return new GroupHolder(layout);
    }

    @Override
    public void onBindViewHolder(GroupHolder groupHolder, int position) {
        Group contact  = groupList.get(position);
        Integer unread = unreadList.get(position);
        groupHolder.bindGroup(contact, unread);
    }

    @Override
    public int getItemCount() {
        if(groupList == null)
            return 0;
        else
            return groupList.size();
    }

    public void swap(ArrayList<Group> groupList) {
        if(this.groupList != null)
            this.groupList.clear();
        this.groupList  = groupList;
        this.unreadList = new ArrayList<>(groupList.size());
        for(int i = 0; i < groupList.size(); i++) {
            unreadList.add(0);
        }
        notifyDataSetChanged();
    }

    public void updateUnread(String gid, int unread) {
        for(int i = 0; i < groupList.size(); i++) {
            if(groupList.get(i).getGid().equals(gid)) {
                unreadList.set(i, unread);
                notifyItemChanged(i);
            }
        }
    }

}
