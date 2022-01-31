package org.disrupted.ibits.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author
 */
public abstract class Database {

    private static final String TAG = "Database";

    protected static final String ID_WHERE            = "_id = ?";

    protected SQLiteOpenHelper databaseHelper;
    protected final Context context;

    public Database(Context context, SQLiteOpenHelper databaseHelper) {
        this.context        = context;
        this.databaseHelper = databaseHelper;
    }

    abstract public String getTableName();

    public void reset(SQLiteOpenHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public int getCount() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor = database.query(getTableName(), null, null, null, null, null, null);
        if(cursor == null)
            return -1;
        try {
            return cursor.getCount();
        } finally {
            cursor.close();
        }
    }
}
