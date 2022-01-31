package org.disrupted.ibits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author
 */
public class ContactHashTagInterestDatabase extends Database {
    private static final String TAG = "ContactInterestTagDatabase";

    public  static final String TABLE_NAME = "contact_hashtag_interest";
    public  static final String CDBID = "_udbid";
    public  static final String HDBID = "_hdbid";
    public  static final String INTEREST = "interest";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + CDBID + " INTEGER, "
                 + HDBID + " INTEGER, "
                 + INTEREST + " INTEGER, "
                 + " UNIQUE( " + CDBID + " , " + HDBID + "), "
                 + " FOREIGN KEY ( "+ CDBID + " ) REFERENCES " + ContactDatabase.TABLE_NAME   + " ( " + ContactDatabase.ID   + " ), "
                 + " FOREIGN KEY ( "+ HDBID + " ) REFERENCES " + GroupDatabase.TABLE_NAME + " ( " + HashtagDatabase.ID + " )"
            + " );";


    public ContactHashTagInterestDatabase(Context context, SQLiteOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public void deleteEntriesMatchingContactID(long contactID){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_NAME, CDBID + " = ?" , new String[] {Long.toString(contactID)});
    }

    public void deleteContactTagInterest(long contactDBID, long hashtagDBID){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_NAME, CDBID + " = ? AND "+HDBID + " = ? " ,
                new String[] {Long.toString(contactDBID), Long.toString(hashtagDBID)});
    }



    public long insertContactTagInterest(long contactID, long hashtagID, int value){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CDBID, contactID);
        contentValues.put(HDBID, hashtagID);
        contentValues.put(INTEREST, value);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        return db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
    }
}
