package org.disrupted.ibits.database.statistics;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author
 */
public class StatLinkLayerDatabase extends StatisticDatabase {
    private static final String TAG = "StatConnectionDatabase";

    public  static final String TABLE_NAME      = "link_layer";
    public  static final String ID              = "_id";
    public  static final String LINKLAYER_ID    = "link_layer_id";
    public  static final String TIME_STARTED    = "link_layer_start";
    public  static final String TIME_STOPPED    = "link_layer_ended";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
            " (" + ID     + " INTEGER PRIMARY KEY, "
            + LINKLAYER_ID  + " TEXT, "
            + TIME_STARTED + " INTEGER, "
            + TIME_STOPPED   + " INTEGER "
            + " );";

    public StatLinkLayerDatabase(Context context, SQLiteOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    public long insertLinkLayerStat(String linkLayerID, long started_nano, long stopped_nano) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LINKLAYER_ID, linkLayerID);
        contentValues.put(TIME_STARTED, started_nano);
        contentValues.put(TIME_STOPPED, stopped_nano);
        return databaseHelper.getWritableDatabase().insert(TABLE_NAME, null, contentValues);
    }

    public void clean() {
        databaseHelper.getWritableDatabase().delete(TABLE_NAME, null, null);
    }

}
