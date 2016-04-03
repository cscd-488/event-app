/**
 * @file EventDataHelper.java
 * @author Bruce Emehiser
 * @date 2016 03 15
 *
 * This contains functionality to store data locally
 * in a SQLite database
 */

package com.example.jharshman.event;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class EventDataHelper extends SQLiteOpenHelper {

    private static final String TAG = "EventDataHelper";

    // database version
    private static final int DATABASE_VERSION = 1;

    // database name
    private static final String DATABASE_NAME = "magpie_data";

    // table names
    private static final String USER_TABLE = "users";
    private static final String SUBSCRIPTIONS_TABLE = "subscriptions";
    private static final String CHECKED_TABLE = "checked";
    private static final String REDEEMED_TABLE = "redeemed";
    private static final String EVENT_TABLE = "events";
    private static final String CHECK_POINT_TABLE = "check_points";

    // column names
    private static final String USER_COLUMN_ID = "user_id";
    private static final String USER_COLUMN_FIRST_NAME = "first_name";
    private static final String USER_COLUMN_LAST_NAME = "last_name";
    private static final String USER_COLUMN_TOKEN = "token";

    private static final String SUBSCRIPTIONS_COLUMN_ID = "subscription_id";
    private static final String SUBSCRIPTION_COLUMN_USER_ID = "user_id";
    private static final String SUBSCRIPTION_COLUMN_EVENT_ID = "event_id";

    private static final String CHECKED_COLUMN_ID = "checked_id";
    private static final String CHECKED_COLUMN_USER_ID = "user_id";
    private static final String CHECKED_COLUMN_CHECKPOINT_ID = "checkpoint_id";

    private static final String REDEEMED_COLUMN_ID = "redeemed_id";
    private static final String REDEEMED_COLUMN_USER_ID = "user_id";
    private static final String REDEEMED_COLUMN_EVENT_ID = "event_id";

    private static final String EVENT_COLUMN_ID = "event_id";
    private static final String EVENT_COLUMN_TITLE = "title";
    private static final String EVENT_COLUMN_SHORT_TITLE = "short_title";
    private static final String EVENT_COLUMN_AUTHOR = "author";
    private static final String EVENT_COLUMN_DESCRIPTION = "description";
    private static final String EVENT_COLUMN_IMAGE_SRC = "image_src";
    private static final String EVENT_COLUMN_LAT = "lat";
    private static final String EVENT_COLUMN_LON = "lon";
    private static final String EVENT_COLUMN_QR = "qr";
    private static final String EVENT_COLUMN_TIME_CREATED = "time_created";
    private static final String EVENT_COLUMN_TIME_UPDATED = "time_updated";

    private static final String CHECK_POINT_COLUMN_ID = "checkpoint_id";
    private static final String CHECK_POINT_COLUMN_TITLE = "title";
    private static final String CHECK_POINT_COLUMN_ARTIST = "artist";
    private static final String CHECK_POINT_COLUMN_DESCRIPTION = "description";
    private static final String CHECK_POINT_COLUMN_IMAGE_SRC = "image_src";
    private static final String CHECK_POINT_COLUMN_LAT = "lat";
    private static final String CHECK_POINT_COLUMN_LON = "lon";
    private static final String CHECK_POINT_COLUMN_QR = "qr";
    private static final String CHECK_POINT_COLUMN_TIME_CREATED = "time_created";
    private static final String CHECK_POINT_COLUMN_TIME_UPDATED = "time_updated";

    public static EventDataHelper newInstance(Context context, String databaseName) {

        int version = 1;

        //todo figure out how to actually get a cursor factory
        SQLiteDatabase.CursorFactory cursorFactory = new SQLiteDatabase.CursorFactory() {
            @Override
            public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
                return null;
            }
        };

        EventDataHelper databasehelper = new EventDataHelper(context, databaseName, cursorFactory, version);

        return databasehelper;
    }

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    private EventDataHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param database The database.
     */
    @Override
    public void onCreate(SQLiteDatabase database) {

        // create tables todo finish adding tables to table creation


        database.execSQL(
                "CREATE TABLE " + USER_TABLE + "("
                        + USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + USER_COLUMN_FIRST_NAME + " TEXT NOT NULL"
                        + USER_COLUMN_LAST_NAME + " TEXT NOT NULL"
                        + USER_COLUMN_TOKEN + " TEXT NOT NULL"
                        + ")"
        );

        database.execSQL(
                "CREATE TABLE " + SUBSCRIPTIONS_TABLE + "("
                        + SUBSCRIPTIONS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + SUBSCRIPTION_COLUMN_USER_ID + " INTEGER NOT NULL"
                        + SUBSCRIPTION_COLUMN_EVENT_ID + " INTEGER NOT NULL"
                        + ")"
        );

        database.execSQL(
                "CREATE TABLE " + CHECKED_TABLE + "("
                        + CHECKED_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CHECKED_COLUMN_USER_ID + " INTEGER NOT NULL"
                        + CHECKED_COLUMN_CHECKPOINT_ID + " INTEGER NOT NULL"
                        + ")"
        );

        database.execSQL(
                "CREATE TABLE " + EVENT_TABLE + "("
                        + EVENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + EVENT_COLUMN_TITLE + " TEXT NOT NULL,"
                        + EVENT_COLUMN_DESCRIPTION + " TEXT NOT NULL"
                        + EVENT_COLUMN_IMAGE_SRC + " TEXT NOT NULL"
                        // todo finish event table
                        + ")"
        );

        database.execSQL(
                "CREATE TABLE " + CHECK_POINT_TABLE + "("
                        + CHECK_POINT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CHECK_POINT_COLUMN_TITLE + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_DESCRIPTION + " TEXT NOT NULL"
                        + CHECK_POINT_COLUMN_IMAGE_SRC + " TEXT NOT NULL"
                        + CHECK_POINT_COLUMN_LAT + " TEXT NOT NULL"
                        + CHECK_POINT_COLUMN_LON + " TEXT NOT NULL"
                        + CHECK_POINT_COLUMN_QR + " TEXT NOT NULL"
                        // TODO finish check point table
                        + ")"
        );

    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The EventDataHelper ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param database         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        // drop the old table
        database.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + CHECK_POINT_TABLE);

        // create a new tables
        onCreate(database);
    }

    /**
     * Get a list of all Events from the database
     *
     * @return The list of Events
     */
    private List<Event> getEvents() {

        ArrayList<Event> events = new ArrayList<>();

        // try statement in case of database errors
        try {

            // get the database from SqliteOpenHelper.this
            SQLiteDatabase database = this.getReadableDatabase();

            // send query database for all events
            Cursor cursor = database.query(EVENT_TABLE, new String[] {
                            EVENT_COLUMN_ID,
                            EVENT_COLUMN_TITLE,
                            EVENT_COLUMN_DESCRIPTION,
                            EVENT_COLUMN_IMAGE_SRC
                    },
                    null, null, null, null, null, null);

            // get the data from the cursor
            if(cursor != null) {

                // add items to the list todo complete list of elements
                int event_id;
                String title;
                String description;
                String imageSrc;
                Event event;
                for(cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()) {

                    // get elements from cursor
                    event_id = cursor.getInt(0);
                    title = cursor.getString(1);
                    description = cursor.getString(2);
                    imageSrc = cursor.getString(3);

                    // create new event and add it to the list
                    event = new Event(0, title, description, imageSrc, null, null, null, false, null);
                    events.add(event);
                }

                // close the cursor
                cursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "error getting events from database");
            e.printStackTrace();
        }

        // return events (which may be an empty list)
        return events;
    }

    /**
     * todo create methods to insert and update existing Events in the table
     * http://www.perfectapk.com/sqliteopenhelper-example.html
     */
}
