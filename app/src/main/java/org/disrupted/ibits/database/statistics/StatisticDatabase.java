package org.disrupted.ibits.database.statistics;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.disrupted.ibits.database.Database;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author
 */
public abstract class StatisticDatabase extends Database {

    public StatisticDatabase(Context context, SQLiteOpenHelper databaseHelper) {
        super(context, databaseHelper);
    }

    public abstract String getTableName();

    public JSONArray getJSON() {
        SQLiteDatabase database = databaseHelper.getReadableDatabase();
        Cursor cursor           = database.query(getTableName(), null, null, null, null, null, null);
        if(cursor == null)
            return null;
        JSONArray resultSet = new JSONArray();
        try {
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                int totalColumn = cursor.getColumnCount();
                JSONObject rowObject = new JSONObject();
                for( int i=0 ;  i< totalColumn ; i++ )
                {
                    if( cursor.getColumnName(i) != null )
                    {
                        try
                        {
                            if( cursor.getString(i) != null )
                                rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                            else
                                rowObject.put( cursor.getColumnName(i) ,  "" );
                        } catch( Exception e ) {}
                    }
                }
                resultSet.put(rowObject);
                cursor.moveToNext();
            }
            cursor.close();
        } finally {
            cursor.close();
        }
        return resultSet;
    }

}
