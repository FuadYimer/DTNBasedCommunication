
package org.disrupted.ibits.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;


import org.disrupted.ibits.database.objects.PushStatus;

import java.io.File;
import java.io.IOException;

/**
 * From https://developer.android.com/training/basics/data-storage/files.html
 * @author
 */
public class FileUtil {

    private static final String TAG = "FileUtil";
    public static String RUMBLE_IMAGE_ALBUM_NAME = "Rumble";

    public static String cleanBase64(String uuid) {
        String ret = uuid.replace('/', '_');
        ret = ret.replace('+','-');
        ret = ret.replaceAll("[^a-zA-Z0-9_-]", "");
        return ret;
    }

    public static boolean checkStoragePermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isFileNameClean(String input) {
        String nameWithoutExtension = input.substring(0, input.lastIndexOf('.'));
        return (nameWithoutExtension.equals(nameWithoutExtension.replaceAll("[^a-zA-Z0-9_-]", "")));
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static File getWritableAlbumStorageDir() throws IOException {
        if(!isExternalStorageWritable())
            throw  new IOException("Storage is not writable");

        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                RUMBLE_IMAGE_ALBUM_NAME);

        if(!file.exists() && !file.mkdirs())
            throw  new IOException("could not create directory "+file.getAbsolutePath());

        if(file.getFreeSpace() < PushStatus.STATUS_ATTACHED_FILE_MAX_SIZE)
            throw  new IOException("not enough space available ("+file.getFreeSpace()+"/"+PushStatus.STATUS_ATTACHED_FILE_MAX_SIZE+")");

        return file;
    }

    // We create to store zip file and other document in different location
    public static File getWritableZIPStorageDir() throws IOException {
        if(!isExternalStorageWritable())
            throw  new IOException("Storage is not writable");

        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                RUMBLE_IMAGE_ALBUM_NAME);

        if(!file.exists() && !file.mkdirs())
            throw  new IOException("could not create directory "+file.getAbsolutePath());

        if(file.getFreeSpace() < PushStatus.STATUS_ATTACHED_FILE_MAX_SIZE)
            throw  new IOException("not enough space available ("+file.getFreeSpace()+"/"+PushStatus.STATUS_ATTACHED_FILE_MAX_SIZE+")");

        return file;
    }

    public static File getReadableAlbumStorageDir() throws IOException {
        if(!isExternalStorageReadable())
            throw  new IOException("Storage is not readable");

        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                RUMBLE_IMAGE_ALBUM_NAME);

        if(!file.exists() && !file.mkdirs())
            throw  new IOException("could not create directory "+file.getAbsolutePath());

        return file;
    }


    // Zip and other file storage album
    public static File getReadableZipStorageDir() throws IOException {
        if(!isExternalStorageReadable())
            throw  new IOException("Storage is not readable");

        File file = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                RUMBLE_IMAGE_ALBUM_NAME);

        if(!file.exists() && !file.mkdirs())
            throw  new IOException("could not create directory "+file.getAbsolutePath());

        return file;
    }

    @SuppressLint("NewApi")
    private static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    private static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    private static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.KITKAT)
            return getRealPathFromURI_API19(context, contentUri);
        if (currentapiVersion >= Build.VERSION_CODES.HONEYCOMB)
            return getRealPathFromURI_API11to18(context, contentUri);
        return getRealPathFromURI_BelowAPI11(context, contentUri);
    }

}
