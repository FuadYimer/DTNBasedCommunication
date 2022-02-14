package org.disrupted.ibits.userinterface.activity;

import android.content.Intent;
import android.os.Bundle;

import org.disrupted.ibits.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.squareup.picasso.Picasso;

import org.disrupted.ibits.R;
import org.disrupted.ibits.database.DatabaseFactory;
import org.disrupted.ibits.database.objects.PushStatus;
import org.disrupted.ibits.util.FileUtil;
import org.disrupted.ibits.util.TimeUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author
 */
public class DisplayStatusActivity extends AppCompatActivity {

    private static final String TAG = "DisplayStatusActivity";

    private static final TextDrawable.IBuilder builder = TextDrawable.builder().rect();

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_status);
        setTitle(R.string.status_viewer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle args = getIntent().getExtras();
        String statusID = args.getString("StatusID");
        renderStatus(statusID);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
        }
        return false;
    }

    private void renderStatus(String statusID) {

        PushStatus status = DatabaseFactory.getPushStatusDatabase(this).getStatus(statusID);
        if(status == null)
            return;

        ImageView avatarView    = (ImageView)findViewById(R.id.status_item_avatar);
        TextView authorView     = (TextView) findViewById(R.id.status_item_author);
        TextView textView       = (TextView) findViewById(R.id.status_item_body);
        TextView tocView        = (TextView) findViewById(R.id.status_item_created);
        TextView toaView        = (TextView) findViewById(R.id.status_item_received);
        TextView groupNameView  = (TextView) findViewById(R.id.status_item_group_name);
        ImageView attachedView  = (ImageView)findViewById(R.id.status_item_attached_image);
        ImageView moreView      = (ImageView)findViewById(R.id.status_item_more_options);
        LinearLayout box        = (LinearLayout)findViewById(R.id.status_item_box);

        final String uid = status.getAuthor().getUid();
        final String name= status.getAuthor().getName();

        // we draw the avatar
        ColorGenerator generator = ColorGenerator.DEFAULT;
        avatarView.setImageDrawable(
                builder.build(status.getAuthor().getName().substring(0, 1),
                        generator.getColor(status.getAuthor().getUid())));

        // we draw the author field
        authorView.setText(status.getAuthor().getName());
        tocView.setText(TimeUtil.timeElapsed(status.getTimeOfCreation()));
        toaView.setText(TimeUtil.timeElapsed(status.getTimeOfArrival()));
        groupNameView.setText(status.getGroup().getName());
        groupNameView.setTextColor(generator.getColor(status.getGroup().getGid()));

        // we draw the status (with clickable links)
        textView.setText(status.getPost());
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextIsSelectable(true);

        Linkify.addLinks(textView, Linkify.ALL);

        /* todo: clickable hashtags */

        /* we draw the attached file (if any) */
        if (status.hasAttachedFile()) {
            attachedView.setVisibility(View.VISIBLE);
            try {
                //final File attachedFile = new File(FileUtil.getReadableAlbumStorageDir(), status.getFileName());
                final File attachedFile;
                if (status.getFileName().endsWith(".zip")){
                    attachedFile = new File(FileUtil.getReadableZipStorageDir(), status.getFileName());
                }else{
                    attachedFile = new File(FileUtil.getReadableAlbumStorageDir(), status.getFileName());
                }


                if (!attachedFile.isFile() || !attachedFile.exists())
                    throw new IOException("file does not exists");

//                Picasso.get()
//                        .load("file://"+attachedFile.getAbsolutePath())
//                        .resize(96, 96)
//                        .centerCrop()
//                        .into(attachedView);
//
                if (!attachedFile.isFile() || !attachedFile.exists())
                    throw new IOException("file does not exists");

                if (status.getFileName().endsWith(".zip")){
                    attachedView.setBackgroundResource(R.drawable.ic_attach_file);
                }else{
                    Picasso.get()
                            .load("file://"+attachedFile.getAbsolutePath())
                            .resize(96, 96)
                            .centerCrop()
                            .into(attachedView);
                }



                final String filename =  status.getFileName();

		/* we open the attached image file in gallery */
                attachedView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "trying to open: " + filename);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("content://"+attachedFile.getAbsolutePath()), "image/*");
			startActivity(intent);

                    }
                });
            } catch (IOException ignore) {
                Picasso.get()
                        .load(R.drawable.ic_close_black_48dp)
                        .resize(96, 96)
                        .centerCrop()
                        .into(attachedView);
            }
        } else {
            attachedView.setVisibility(View.GONE);
        }
        /*
        moreView.setOnClickListener(new PopupMenuListener());
        if (!status.hasUserReadAlready() || ((System.currentTimeMillis() - status.getTimeOfArrival()) < 60000)) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                box.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.status_shape_unread));
            } else {
                box.setBackground(activity.getResources().getDrawable(R.drawable.status_shape_unread));
            }
            if (!status.hasUserReadAlready()) {
                status.setUserRead(true);
                EventBus.getDefault().post(new UserReadStatus(status));
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                box.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.status_shape_read));
            } else {
                box.setBackground(activity.getResources().getDrawable(R.drawable.status_shape_read));
            }
        }
        */
    }

}
