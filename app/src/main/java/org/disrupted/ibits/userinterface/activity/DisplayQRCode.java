
package org.disrupted.ibits.userinterface.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.disrupted.ibits.R;

/**
 * @author
 */
public class DisplayQRCode extends AppCompatActivity {

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qrcode);

        Intent intent = getIntent();
        bitmap = (Bitmap) intent.getParcelableExtra("EXTRA_QRCODE");
        String name   = (String) intent.getStringExtra("EXTRA_GROUP_NAME");
        String buffer = (String) intent.getStringExtra("EXTRA_BUFFER");

        setTitle(name);
        getSupportActionBar().hide();

        ImageView qrView = (ImageView)findViewById(R.id.qrcode);
        TextView  bufView = (TextView)findViewById(R.id.buffer);

        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
        qrView.setImageDrawable(drawable);
        bufView.setText(buffer);
    }

    @Override
    protected void onDestroy() {
        bitmap.recycle();
        bitmap = null;
        super.onDestroy();
    }
}
