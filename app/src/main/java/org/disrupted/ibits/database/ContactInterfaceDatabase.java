package org.disrupted.ibits.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * ContactInterfaceDatabase keeps track of the interfaces we met that match a certain contact
 * This is to avoid resending all of the contact information (name, uid, avatar, etc.) whenever
 * we reconnect to a device.
 *
 * In order to avoid storing all the interface mac address in plain text, we store a hash of the
 * mac address.
 *
 * @author
 */
public class ContactInterfaceDatabase extends Database {


    private static final String TAG = "ContactInterfaceDatabase";

    public static final String TABLE_NAME       = "contact_interfaces";
    public static final String CONTACT_DBID     = "_cdbid";
    public static final String INTERFACE_DBID   = "_idbid";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + CONTACT_DBID     + " INTEGER, "
                 + INTERFACE_DBID  + " INTEGER, "
                 + " UNIQUE( " + INTERFACE_DBID + "), "
                 + " FOREIGN KEY ( "+ CONTACT_DBID    + " ) REFERENCES " + ContactDatabase.TABLE_NAME  + " ( " + ContactDatabase.ID  + " ), "
                 + " FOREIGN KEY ( "+ INTERFACE_DBID + " ) REFERENCES " + InterfaceDatabase.TABLE_NAME   + " ( " + InterfaceDatabase.ID   + " ) "
            + " );";

    public ContactInterfaceDatabase(Context context, SQLiteOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public void deleteEntriesMatchingContactDBID(long contactDBID){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete(TABLE_NAME, CONTACT_DBID + " = ?" , new String[] {contactDBID + ""});
    }

    /*
     * adds a (contact, interface) entry.
     *  - returns 1 if an entry has been added or updated,
     *  - returns -1 otherwise
     *
     * Interface is a hash between the macAddress and the protocolID.
     * Only one Contact can be matched to an Interface
     */
    public long insertContactInterface(long contactDBID, long interfaceDBID){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACT_DBID, contactDBID);
        contentValues.put(INTERFACE_DBID, interfaceDBID);

        Cursor cursor = null;
        try {
            SQLiteDatabase database = databaseHelper.getReadableDatabase();
            cursor = database.query(TABLE_NAME, null, INTERFACE_DBID+" = ?",
                    new String[] {Long.toString(interfaceDBID)}, null, null, null);
            if(cursor.moveToFirst() && !cursor.isAfterLast()) {
                long contactDBID2 = cursor.getLong(cursor.getColumnIndexOrThrow(CONTACT_DBID));
                if(contactDBID2 == contactDBID)
                    return -1;
            }
            databaseHelper.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            return 1;
        } finally {
            if(cursor != null)
                cursor.close();
        }
    }

}
