package org.disrupted.ibits.userinterface.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import org.disrupted.ibits.util.Log;


import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import org.disrupted.ibits.R;
import org.disrupted.ibits.app.RumbleApplication;
import org.disrupted.ibits.database.DatabaseExecutor;
import org.disrupted.ibits.database.DatabaseFactory;
import org.disrupted.ibits.database.objects.Contact;
import org.disrupted.ibits.database.objects.Group;
import org.disrupted.ibits.database.objects.PushStatus;
import org.disrupted.ibits.userinterface.events.UserComposeStatus;
import org.disrupted.ibits.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * @author
 */
public class PopupComposeStatus extends Activity {

    private static final String TAG = "PopupCompose";
    public static final int REQUEST_PERMISSION_CAMERA = 32;
    public static final int REQUEST_IMAGE_CAPTURE     = 42;
    public static final int REQUEST_PICK_IMAGE        = 52;
    public static final int REQUEST_ATTACH_FILE        = 62;

    private LinearLayout dismiss;
    private EditText    compose;
    private ImageView   compose_background;
    private ImageButton takePicture;
    private ImageButton choosePicture;
    private ImageButton send;
    private Bitmap imageBitmap;
    private ImageView groupLock;
    private TextView file_path;
    private ImageButton attach_file;

    private Spinner spinner;
    private GroupSpinnerAdapter spinnerArrayAdapter;
    private String  filter_gid = null;
    private String  filter_hashtag = null;

    private String pictureTaken;
    private Uri pictureChosen;
    private Uri fileChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_compose_status);

        Bundle args = getIntent().getExtras();
        if(args != null) {
            this.filter_gid = args.getString("GroupID");
            this.filter_hashtag = args.getString("Hashtag");
        }

        imageBitmap = null;
        dismiss = (LinearLayout)(findViewById(R.id.popup_dismiss));
        compose = (EditText)(findViewById(R.id.popup_user_status));
        compose_background = (ImageView)(findViewById(R.id.popup_user_attached_photo));
        takePicture = (ImageButton)(findViewById(R.id.popup_take_picture));
        choosePicture = (ImageButton)(findViewById(R.id.popup_choose_image));
        send = (ImageButton)(findViewById(R.id.popup_button_send));
        spinner = (Spinner)(findViewById(R.id.group_list_spinner));
        groupLock = (ImageView)(findViewById(R.id.group_lock_image));
        file_path = findViewById(R.id.file_path);
        attach_file= findViewById(R.id.attach_file);

        groupLock.setBackgroundResource(R.drawable.ic_lock_outline_white_24dp);

        if(filter_gid == null) {
            spinnerArrayAdapter = new GroupSpinnerAdapter();
            spinner.setAdapter(spinnerArrayAdapter);
            spinner.setOnItemSelectedListener(spinnerArrayAdapter);
            getGroupList();
        } else {
            spinner.setVisibility(View.INVISIBLE);
        }

        if(filter_hashtag != null) {
            compose.setText(filter_hashtag);
        }

        dismiss.setOnClickListener(onDiscardClick);
        takePicture.setOnClickListener(onTakePictureClick);
        choosePicture.setOnClickListener(onAttachPictureClick);
        attach_file.setOnClickListener(onAttachFile);
        send.setOnClickListener(onClickSend);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void getGroupList() {
        DatabaseFactory.getGroupDatabase(this).getGroups(onGroupsLoaded);
    }
    private DatabaseExecutor.ReadableQueryCallback onGroupsLoaded = new DatabaseExecutor.ReadableQueryCallback() {
        @Override
        public void onReadableQueryFinished(final Object result) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Group> answer = (ArrayList<Group>) (result);
                    spinnerArrayAdapter.swap(answer);
                    spinnerArrayAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    @Override
    protected void onDestroy() {
        if(imageBitmap != null) {
            imageBitmap.recycle();
            imageBitmap = null;
        }
        super.onDestroy();
    }

    View.OnClickListener onDiscardClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(compose.getWindowToken(), 0);
            finish();
        }
    };

    View.OnClickListener onTakePictureClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Activity activity = PopupComposeStatus.this;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                    return;
                }
            }

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                File photoFile;
                try {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                    File storageDir = FileUtil.getWritableAlbumStorageDir();
                    String imageFileName = "JPEG_" + timeStamp + "_";
                    String suffix = ".jpg";
                    photoFile = File.createTempFile(
                            imageFileName,  /* prefix */
                            suffix,         /* suffix */
                            storageDir      /* directory */
                    );
                    pictureTaken = photoFile.getName();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (IOException error) {
                    Log.e(TAG, "[!] cannot create photo file > " + error.getMessage());
                }
            }
        }
    };


    View.OnClickListener onAttachPictureClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Activity activity = PopupComposeStatus.this;
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("image/*");
            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickIntent.setType("image/*");
            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
            if (getIntent.resolveActivity(activity.getPackageManager()) != null) {
                startActivityForResult(chooserIntent, REQUEST_PICK_IMAGE);
            }
        }
    };

    View.OnClickListener onAttachFile = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Activity activity = PopupComposeStatus.this;
            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
            getIntent.setType("*/*");
            //Intent pickIntent = new Intent(Intent.ACTION_PICK);
            Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
            pickIntent.setType("*/*");
            Intent chooserIntent = Intent.createChooser(getIntent, "Select File");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

            if (getIntent.resolveActivity(activity.getPackageManager()) != null) {
                startActivityForResult(chooserIntent, REQUEST_ATTACH_FILE);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                File attachedFile = new File(FileUtil.getReadableAlbumStorageDir(), pictureTaken);
                pictureChosen = null;
                Picasso.get()
                        .load("file://"+attachedFile.getAbsolutePath())
                        .fit()
                        .centerCrop()
                        .into(compose_background);
            } catch(IOException ignore){
            }
        }
        if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data == null)
                return;
            Uri selectedImage = data.getData();
            pictureTaken = null;
            pictureChosen = selectedImage;
            Picasso.get()
                    .load(selectedImage)
                    .fit()
                    .centerCrop()
                    .into(compose_background);

        }

        if (requestCode == REQUEST_ATTACH_FILE && resultCode == RESULT_OK) {
            if (data == null)
                return;
            Uri selectedFile = data.getData();
            pictureTaken = null;
            fileChosen = selectedFile;
            file_path.setText(data.getData().getPath());

        }
    }

    /*
     * Receive the result of dynamic permission request for storage
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "[+] permission to engage camera granted");
                } else {
                    Log.d(TAG, "[!] permission to engage camera refused");
                }
            }
        }
    }

    View.OnClickListener onClickSend = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            try {
                String message = compose.getText().toString();
                if (message.equals(""))
                    message = " ";

                Group group;
                if(filter_gid == null)
                    group = spinnerArrayAdapter.getSelected();
                else
                    group = DatabaseFactory.getGroupDatabase(PopupComposeStatus.this).getGroup(filter_gid);

                if(group == null)
                    return;

                Contact localContact = DatabaseFactory.getContactDatabase(RumbleApplication.getContext()).getLocalContact();
                long now = System.currentTimeMillis();
                PushStatus pushStatus = new PushStatus(localContact, group, message, now, localContact.getUid());
                pushStatus.setUserRead(true);
                if(pictureChosen != null )  {
                    // copy the file into ibits directory
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                    File chosenFile = File.createTempFile(
                            "JPEG" + timeStamp + "_",  /* prefix */
                            ".jpg",         /* suffix */
                            FileUtil.getWritableAlbumStorageDir()      /* directory */
                    );

                    InputStream in = PopupComposeStatus.this.getContentResolver().openInputStream(pictureChosen);
                    OutputStream out = new FileOutputStream(chosenFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    pictureChosen = null;
                    pictureTaken = chosenFile.getName();
                }

                // convert the choosen file in to zip file ----> Based on DHIS template
                if(fileChosen != null )  {
                    // copy the file into ibits directory
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                    File chosenFile = File.createTempFile(
                            "ZIP" + timeStamp + "_",  /* prefix */
                            ".zip",         /* suffix */
                            // for zip file and document storage
                            FileUtil.getWritableZIPStorageDir()      /* directory */
                    );

                    InputStream in = PopupComposeStatus.this.getContentResolver().openInputStream(fileChosen);
                    OutputStream out = new FileOutputStream(chosenFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    pictureChosen = null;
                    pictureTaken = chosenFile.getName();

                }

               EventBus.getDefault().post(new UserComposeStatus(pushStatus, pictureTaken));
            } catch (Exception e) {
                Log.e(TAG,"[!] "+e.getMessage());
            } finally {
                compose.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(compose.getWindowToken(), 0);
                finish();
            }
        }
    };

    public class GroupSpinnerAdapter extends ArrayAdapter<String> implements AdapterView.OnItemSelectedListener{

        private ArrayList<Group> groupList;
        private Group selectedItem;

        public GroupSpinnerAdapter() {
            super(PopupComposeStatus.this, android.R.layout.simple_spinner_item);
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            groupList = new ArrayList<Group>();
            selectedItem = null;
        }

        public Group getSelected() {
            return selectedItem;
        }

        @Override
        public String getItem(int position) {
            return groupList.get(position).getName();
        }

        @Override
        public int getCount() {
            return groupList.size();
        }

        public void swap(ArrayList<Group> array) {
            this.groupList = array;
            if(array != null)
                selectedItem = array.get(0);
            else
                selectedItem = null;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Group clicked = groupList.get(i);
            if(!clicked.isPrivate())
                groupLock.setBackgroundResource(R.drawable.ic_lock_open_white_24dp);
            else
                groupLock.setBackgroundResource(R.drawable.ic_lock_white_24dp);
            selectedItem = clicked;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

    }

}
