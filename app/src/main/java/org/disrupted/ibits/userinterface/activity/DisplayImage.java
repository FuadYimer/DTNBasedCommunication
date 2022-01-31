package org.disrupted.ibits.userinterface.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.disrupted.ibits.R;
import org.disrupted.ibits.util.FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author
 */
public class DisplayImage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        ImageView image = (ImageView)findViewById(R.id.image);
        Intent intent = getIntent();
        String name   = (String) intent.getStringExtra("IMAGE_NAME");

        setTitle(name);
        getSupportActionBar().hide();

        try {
            File attachedFile = new File(FileUtil.getReadableAlbumStorageDir(), name);
            if (!attachedFile.isFile() || !attachedFile.exists())
                throw new IOException("file does not exists");

            Picasso.get()
                    .load("file://" + attachedFile.getAbsolutePath())
                    .fit()
                    .centerInside()
                    .into(image);

        } catch(Exception ignore) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}