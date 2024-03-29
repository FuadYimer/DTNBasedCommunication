package org.disrupted.ibits.database.statistics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author
 */
public class StatMessageDatabase extends StatisticDatabase {

    private static final String TAG = "StatMessageDatabase";

    public  static final String TABLE_NAME      = "messages";
    public  static final String ID              = "_id";
    public  static final String KEY             = "key";
    public  static final String VALUE           = "value";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + ID     + " INTEGER PRIMARY KEY, "
            + KEY  + " TEXT, "
            + VALUE   + " INTEGER, "
            + "UNIQUE( " + KEY +" ) "
            + " );";

    public StatMessageDatabase(Context context, SQLiteOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public long getValue(String key, long default_value) {
        Cursor cursor = null;
        try {
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            cursor = database.query(TABLE_NAME, new String[] {KEY}, KEY+ " = ?", new String[] {key}, null, null, null);
            if(cursor == null)
                return -1;
            if(cursor.moveToFirst() && !cursor.isAfterLast()) {
                return cursor.getLong(cursor.getColumnIndexOrThrow(KEY));
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY, key);
                contentValues.put(VALUE, default_value);
                databaseHelper.getWritableDatabase().insert(TABLE_NAME, null, contentValues);
                return default_value;
            }
        } finally {
            if(cursor != null)
                cursor.close();
        }
    }

    public void updateValue(String key, long value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY, key);
        contentValues.put(VALUE, value);
        databaseHelper.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null,
                contentValues,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void clean() {
        databaseHelper.getWritableDatabase().delete(TABLE_NAME, null, null);
    }

    public class KeyNotFoundException extends Exception {
    }
}
