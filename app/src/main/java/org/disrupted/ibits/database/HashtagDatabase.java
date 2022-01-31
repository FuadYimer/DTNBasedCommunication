package org.disrupted.ibits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.disrupted.ibits.database.events.HashtagInsertedEvent;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * @author
 */
public class HashtagDatabase extends  Database{

    private static final String TAG = "HashTagDatabase";


    public  static final String TABLE_NAME    = "hashtags";
    public  static final String ID            = "_id";
    public  static final String HASHTAG       = "hashtag";
    public  static final String COUNT         = "count";
    public  static final String LAST_SEEN     = "last";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + ID        + " INTEGER PRIMARY KEY, "
                 + HASHTAG   + " TEXT, "
                 + "UNIQUE( " + HASHTAG + " ) "
          + " );";


    public HashtagDatabase(Context context, SQLiteOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public boolean getHashtags(DatabaseExecutor.ReadableQueryCallback callback) {
        return DatabaseFactory.getDatabaseExecutor(context).addQuery(
                new DatabaseExecutor.ReadableQuery() {
                    @Override
                    public ArrayList<String> read() {
                        return getHashtags();
                    }
                }, callback);
    }
    private ArrayList<String>  getHashtags() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor           = database.query(TABLE_NAME, null, null, null, null, null, null);
        if(cursor == null)
            return null;
        ArrayList<String> ret = new ArrayList<String>();
        try {
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                ret.add(cursor.getString(cursor.getColumnIndexOrThrow(HASHTAG)));
            }
        }finally {
            cursor.close();
        }
        return ret;
    }

    public long getHashtagDBID(String hashtag) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            cursor = db.query(TABLE_NAME, new String[]{ID}, HASHTAG + " = ?", new String[]{hashtag.toLowerCase()}, null, null, null, null);
            if (cursor != null && cursor.moveToFirst() && !cursor.isAfterLast())
                return cursor.getLong(cursor.getColumnIndexOrThrow(ID));
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return -1;
    }

    public long insertHashtag(String hashtag){
        long rowid = getHashtagDBID(hashtag);
        if(rowid < 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(HASHTAG, hashtag.toLowerCase());
            rowid = databaseHelper.getWritableDatabase().insert(TABLE_NAME, null, contentValues);
            EventBus.getDefault().post(new HashtagInsertedEvent(hashtag));
        }

        return rowid;
    }

}
