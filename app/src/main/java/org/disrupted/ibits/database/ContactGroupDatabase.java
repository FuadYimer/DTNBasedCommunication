package org.disrupted.ibits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author
 */
public class ContactGroupDatabase extends Database {

    private static final String TAG = "ContactGroupDatabase";

    public  static final String TABLE_NAME = "group_subscriptions";
    public  static final String UDBID = "_udbid";
    public  static final String GDBID = "_gdbid";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
       " (" + UDBID + " INTEGER, "
            + GDBID + " INTEGER, "
            + " UNIQUE( " + UDBID + " , " + GDBID + "), "
            + " FOREIGN KEY ( "+ UDBID + " ) REFERENCES " + ContactDatabase.TABLE_NAME   + " ( " + ContactDatabase.ID   + " ), "
            + " FOREIGN KEY ( "+ GDBID + " ) REFERENCES " + GroupDatabase.TABLE_NAME + " ( " + GroupDatabase.ID + " )"
       + " );";


    public ContactGroupDatabase(Context context, SQLiteOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public void deleteEntriesMatchingContactID(long contactID){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_NAME, UDBID + " = ?" , new String[] {Long.toString(contactID)});
    }

    public void deleteEntriesMatchingGroupID(long groupID){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_NAME, GDBID + " = ?" , new String[] {Long.toString(groupID)});
    }

    public long insertContactGroup(long contactID, long groupID){
        ContentValues contentValues = new ContentValues();
        contentValues.put(UDBID, contactID);
        contentValues.put(GDBID, groupID);
        try {
            return databaseHelper.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_FAIL);
        } catch(SQLiteConstraintException ce) {
            return -1;
        }
    }
}
