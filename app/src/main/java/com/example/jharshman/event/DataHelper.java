/**
 * @file DataHelper.java
 * @author Bruce Emehiser
 * @date 2016 03 15
 *
 * This contains functionality to store data locally
 * in a SQLite database
 */

package com.example.jharshman.event;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataHelper extends SQLiteOpenHelper {

    // debugging tag
    private static final String TAG = "DataHelper";

    // database version
    private static final int DATABASE_VERSION = 1;

    // database name
    private static final String DATABASE_NAME = "magpie.db";

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
    private static final String CHECK_POINT_COLUMNT_EVENT_ID = "event_id";
    private static final String CHECK_POINT_COLUMN_TITLE = "title";
    private static final String CHECK_POINT_COLUMN_ARTIST = "artist";
    private static final String CHECK_POINT_COLUMN_DESCRIPTION = "description";
    private static final String CHECK_POINT_COLUMN_IMAGE_SRC = "image_src";
    private static final String CHECK_POINT_COLUMN_LAT = "lat";
    private static final String CHECK_POINT_COLUMN_LON = "lon";
    private static final String CHECK_POINT_COLUMN_QR = "qr";
    private static final String CHECK_POINT_COLUMN_TIME_CREATED = "time_created";
    private static final String CHECK_POINT_COLUMN_TIME_UPDATED = "time_updated";

    /**
     * Get an instance of the data helper.
     * @param context The application context
     * @return Instance of Event Data Helper
     */
    public static DataHelper newInstance(Context context) {

        Log.i(TAG, "Creating new instance of SQLite data helper");

        return new DataHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
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
    private DataHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
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

        Log.i(TAG, "Creating database");

        // create tables todo finish adding tables to table creation

        database.execSQL(
                "CREATE TABLE " + USER_TABLE + "("
                        + USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + USER_COLUMN_FIRST_NAME + " TEXT NOT NULL,"
                        + USER_COLUMN_LAST_NAME + " TEXT NOT NULL,"
                        + USER_COLUMN_TOKEN + " TEXT NOT NULL"
                        + ")"
        );

        database.execSQL(
                "CREATE TABLE " + SUBSCRIPTIONS_TABLE + "("
                        + SUBSCRIPTIONS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + SUBSCRIPTION_COLUMN_USER_ID + " INTEGER NOT NULL,"
                        + SUBSCRIPTION_COLUMN_EVENT_ID + " INTEGER NOT NULL,"
                        + "FOREIGN KEY(" + SUBSCRIPTION_COLUMN_USER_ID + ") REFERENCES " + USER_TABLE + "(" + USER_COLUMN_ID + "),"
                        + "FOREIGN KEY(" + SUBSCRIPTION_COLUMN_USER_ID + ") REFERENCES " + EVENT_TABLE + "(" + EVENT_COLUMN_ID + ")"
                        + ")"
        );

        database.execSQL(
                "CREATE TABLE " + CHECKED_TABLE + "("
                        + CHECKED_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CHECKED_COLUMN_USER_ID + " INTEGER NOT NULL,"
                        + CHECKED_COLUMN_CHECKPOINT_ID + " INTEGER NOT NULL,"
                        + "FOREIGN KEY(" + CHECKED_COLUMN_USER_ID + ") REFERENCES " + USER_TABLE + "(" + USER_COLUMN_ID + "),"
                        + "FOREIGN KEY(" + CHECKED_COLUMN_CHECKPOINT_ID + ") REFERENCES " + CHECK_POINT_TABLE + "(" + CHECK_POINT_COLUMN_ID + ")"
                        + ")"
        );

        database.execSQL(
                "CREATE TABLE " + EVENT_TABLE + "("
                        + EVENT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + EVENT_COLUMN_TITLE + " TEXT NOT NULL,"
                        + EVENT_COLUMN_SHORT_TITLE + " TEXT NOT NULL,"
                        + EVENT_COLUMN_AUTHOR + " TEXT NOT NULL,"
                        + EVENT_COLUMN_DESCRIPTION + " TEXT NOT NULL,"
                        + EVENT_COLUMN_IMAGE_SRC + " TEXT NOT NULL,"
                        + EVENT_COLUMN_LAT + " DOUBLE,"
                        + EVENT_COLUMN_LON + " DOUBLE,"
                        + EVENT_COLUMN_QR + " TEXT NOT NULL,"
                        + EVENT_COLUMN_TIME_CREATED + " TEXT NOT NULL,"
                        + EVENT_COLUMN_TIME_UPDATED + " TEXT NOT NULL"
                        + ")"
        );

        database.execSQL(
                "CREATE TABLE " + CHECK_POINT_TABLE + "("
                        + CHECK_POINT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + CHECK_POINT_COLUMNT_EVENT_ID + " INTEGER,"
                        + CHECK_POINT_COLUMN_TITLE + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_ARTIST + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_DESCRIPTION + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_IMAGE_SRC + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_LAT + " DOUBLE,"
                        + CHECK_POINT_COLUMN_LON + " DOUBLE,"
                        + CHECK_POINT_COLUMN_QR + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_TIME_CREATED + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_TIME_UPDATED + " TEXT NOT NULL,"
                        + "FOREIGN KEY(" + CHECK_POINT_COLUMNT_EVENT_ID + ") REFERENCES " + EVENT_TABLE +"(" + EVENT_COLUMN_ID + ")"
                        + ")"
        );
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The DataHelper ALTER TABLE documentation can be found
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
        database.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + SUBSCRIPTIONS_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + CHECKED_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + REDEEMED_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + CHECK_POINT_TABLE);

        // create a new tables
        onCreate(database);
    }


    /**
     * Add an event to the local database if the event does not exist.
     *
     * @param event The event to add.
     * @return True if event was added, otherwise false.
     */
    public boolean insertEvent(Event event) {

        SQLiteDatabase database = getWritableDatabase();

        // set up the row
        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENT_COLUMN_ID, event.getID());
        contentValues.put(EVENT_COLUMN_TITLE, event.getTitle());
        contentValues.put(EVENT_COLUMN_SHORT_TITLE, event.getShortTitle());
        contentValues.put(EVENT_COLUMN_AUTHOR, event.getAuthor());
        contentValues.put(EVENT_COLUMN_DESCRIPTION, event.getDescription());
        contentValues.put(EVENT_COLUMN_IMAGE_SRC, event.getImageSrc());
        contentValues.put(EVENT_COLUMN_LAT, event.getLat());
        contentValues.put(EVENT_COLUMN_LON, event.getLon());
        contentValues.put(EVENT_COLUMN_QR, event.getQR());
        contentValues.put(EVENT_COLUMN_TIME_CREATED, event.getTimeCreated());
        contentValues.put(EVENT_COLUMN_TIME_UPDATED, event.getTimeUpdated());

        // insert row into the database
        long inserted = database.insert(EVENT_TABLE, null, contentValues);

        Log.i(TAG, "Insert Event successful: " + inserted);

        // inserted succeeded if (inserted != -1)
        return inserted != -1;
    }

    /**
     * Get a list of all Events from the database
     *
     * @return The list of Events
     */
    public List<Event> getEvents() {

        Log.i(TAG, "Getting events...");

        ArrayList<Event> events = new ArrayList<>();

        // try statement in case of database errors
        try {

            // get the database from SqliteOpenHelper.this
            SQLiteDatabase database = this.getReadableDatabase();

            // send query database for all events
//            Cursor cursor = database.query(EVENT_TABLE, new String[] {
//                            EVENT_COLUMN_ID,
//                            EVENT_COLUMN_TITLE,
//                            EVENT_COLUMN_SHORT_TITLE,
//                            EVENT_COLUMN_AUTHOR,
//                            EVENT_COLUMN_DESCRIPTION,
//                            EVENT_COLUMN_IMAGE_SRC,
//                            EVENT_COLUMN_LAT,
//                            EVENT_COLUMN_LON,
//                            EVENT_COLUMN_QR,
//                            EVENT_COLUMN_TIME_CREATED,
//                            EVENT_COLUMN_TIME_UPDATED
//                    },
//                    null ,null, null, null, null);

            Cursor cursor = database.query(true, EVENT_TABLE, null, null, null, null, null, null, null, null);
//            Cursor cursor = database.query(EVENT_TABLE, null, null, null, null, null, null);

            Log.i(TAG, "Cursor Created");
            if (cursor == null) {
                Log.i(TAG, "Cursor is null!");
            }

            // get the data from the cursor
            if(cursor != null) {

                Log.i(TAG, "Cursor wasn't null!");

                // add items to the list todo complete list of elements
                int event_id;
                String title;
                String shortTitle;
                String author;
                String description;
                String imageSrc;
                double lat;
                double lon;
                String qr;
                String timeCreated;
                String timeUpdated;
                Event event;

                for(cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()) {

                    Log.i(TAG, "Getting event from cursor");

                    int pos = 0;

                    // get elements from cursor
                    event_id = cursor.getInt(pos ++);
                    title = cursor.getString(pos ++);
                    shortTitle = cursor.getString(pos ++);
                    author = cursor.getString(pos ++);
                    description = cursor.getString(pos ++);
                    imageSrc = cursor.getString(pos ++);
                    lat = cursor.getDouble(pos ++);
                    lon = cursor.getDouble(pos ++);
                    qr = cursor.getString(pos ++);
                    timeCreated = cursor.getString(pos ++);
                    timeUpdated = cursor.getString(pos);

                    // create new event and add it to the list
                    // todo finish builder in event, and set up this to use it
                    event = new Event();
                    event.setID(event_id);
                    event.setTitle(title);
                    event.setAuthor(author);
                    event.setDescription(description);
                    event.setImageSrc(imageSrc);

                    Log.i(TAG, String.format("%d %s %s %s %s", event_id, title, author, description, imageSrc));

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
