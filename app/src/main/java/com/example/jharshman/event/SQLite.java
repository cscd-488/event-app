/**
 * @file SQLite.java
 * @author Bruce Emehiser
 * @date 2016 03 03
 *
 * This contains functionality to store
 */

package com.example.jharshman.event;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SQLite database. This uses the Singleton pattern, as the
 * database can only handle a single request at a time regardless
 * of how many instances of it there are.
 */
public class SQLite {

    private static final String TAG = "SQLite";

    private static final String mDatabaseName = "events";
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private SQLiteOpenHelper mSQLiteHelper;

    private SQLite mInstance;

    public SQLite getInstance(Context context) {

        if(mInstance == null) {
            mInstance = new SQLite(context);
        }
        return mInstance;
    }

    public SQLite(Context context) {
        mContext = context;

        try {
            mDatabase = context.openOrCreateDatabase(mDatabaseName, Context.MODE_PRIVATE, null);

            // create table
            mDatabase.execSQL("CREATE TABLE IF NOT EXISTS events (title VARCHAR, description VARCHAR);");

            // add some data
            mDatabase.execSQL("INSERT INTO  (title, description) VALUES ('festival', 'This is the coolest festival!');");

            // create cursor for query
            Cursor cursor = mDatabase.rawQuery("SELECT * FROM events", null);

            int column1 = cursor.getColumnIndex("title");
            int column2 = cursor.getColumnIndex("description");

            // Check if our result was valid.
            cursor.moveToFirst();
            // Loop through all Results
            do {
                String title = cursor.getString(column1);
                String description = cursor.getString(column2);

                Log.i(TAG, title);
            }while(cursor.moveToNext());

            cursor.close();
        }
        catch(Exception e) {
            Log.e("Error", "Error", e);
        } finally {
            if (mDatabase != null) {
                mDatabase.close();
            }
        }
    }
}
