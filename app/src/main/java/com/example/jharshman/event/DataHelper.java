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
import android.content.res.Resources;
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
    private static final String SUBSCRIPTIONS_TABLE = "subscriptions";
    private static final String EVENT_TABLE = "events";
    private static final String CHECK_POINT_TABLE = "check_points";

    // column names
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
    private static final String EVENT_COLUMN_SUBSCRIBED = "subscribed";
    private static final String EVENT_COLUMN_REDEEMED = "redeemed";

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
    private static final String CHECK_POINT_COLUMN_CHECKED = "checked";

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

        // create tables
        database.execSQL(
                "CREATE TABLE " + EVENT_TABLE + "("
                        + EVENT_COLUMN_ID + " INTEGER PRIMARY KEY,"
                        + EVENT_COLUMN_TITLE + " TEXT NOT NULL,"
                        + EVENT_COLUMN_SHORT_TITLE + " TEXT NOT NULL,"
                        + EVENT_COLUMN_AUTHOR + " TEXT NOT NULL,"
                        + EVENT_COLUMN_DESCRIPTION + " TEXT NOT NULL,"
                        + EVENT_COLUMN_IMAGE_SRC + " TEXT NOT NULL,"
                        + EVENT_COLUMN_LAT + " DOUBLE,"
                        + EVENT_COLUMN_LON + " DOUBLE,"
                        + EVENT_COLUMN_QR + " TEXT NOT NULL,"
                        + EVENT_COLUMN_TIME_CREATED + " TEXT NOT NULL,"
                        + EVENT_COLUMN_TIME_UPDATED + " TEXT NOT NULL,"
                        + EVENT_COLUMN_SUBSCRIBED + " INTEGER NOT NULL,"
                        + EVENT_COLUMN_REDEEMED + " INTEGER NOT NULL"
                        + ")"
        );

        database.execSQL(
                "CREATE TABLE " + CHECK_POINT_TABLE + "("
                        + CHECK_POINT_COLUMN_ID + " INTEGER PRIMARY KEY,"
                        + CHECK_POINT_COLUMNT_EVENT_ID + " INTEGER NOT NULL,"
                        + CHECK_POINT_COLUMN_TITLE + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_ARTIST + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_DESCRIPTION + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_IMAGE_SRC + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_LAT + " DOUBLE,"
                        + CHECK_POINT_COLUMN_LON + " DOUBLE,"
                        + CHECK_POINT_COLUMN_QR + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_TIME_CREATED + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_TIME_UPDATED + " TEXT NOT NULL,"
                        + CHECK_POINT_COLUMN_CHECKED + " INTEGER NOT NULL,"
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
        database.execSQL("DROP TABLE IF EXISTS " + SUBSCRIPTIONS_TABLE);
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
        contentValues.put(EVENT_COLUMN_SUBSCRIBED, event.getSubscribed() ? 1 : 0); // Note: SQLite doesn't contain BOOL, so I'm using INTEGER
        contentValues.put(EVENT_COLUMN_REDEEMED, event.isRedeemed() ? 1 : 0);

        // insert row into the database Note: no exception should be thrown todo make events update with whichever data is newest
        long inserted = database.insertWithOnConflict(EVENT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        Log.i(TAG, "Insert Event successful: " + inserted);

        // inserted succeeded if (inserted != -1)
        return inserted != -1;
    }

    /**
     * Add an checkpoint to the local database if the checkpoint does not exist.
     *
     * @param checkPoint The checkpoint to add.
     * @return True if event was added, otherwise false.
     */
    public boolean insertCheckPoint(CheckPoint checkPoint) {

        SQLiteDatabase database = getWritableDatabase();

        // set up the row
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHECK_POINT_COLUMN_ID, checkPoint.getID());
        contentValues.put(CHECK_POINT_COLUMNT_EVENT_ID, checkPoint.getEventID());
        contentValues.put(CHECK_POINT_COLUMN_TITLE, checkPoint.getTitle());
        contentValues.put(CHECK_POINT_COLUMN_ARTIST, checkPoint.getArtist());
        contentValues.put(CHECK_POINT_COLUMN_DESCRIPTION, checkPoint.getDescription());
        contentValues.put(CHECK_POINT_COLUMN_IMAGE_SRC, checkPoint.getImageSrc());
        contentValues.put(CHECK_POINT_COLUMN_LAT, checkPoint.getLat());
        contentValues.put(CHECK_POINT_COLUMN_LON, checkPoint.getLon());
        contentValues.put(CHECK_POINT_COLUMN_QR, checkPoint.getQR());
        contentValues.put(CHECK_POINT_COLUMN_TIME_CREATED, checkPoint.getTimeCreated());
        contentValues.put(CHECK_POINT_COLUMN_TIME_UPDATED, checkPoint.getTimeUpdated());
        contentValues.put(CHECK_POINT_COLUMN_CHECKED, checkPoint.getChecked());

        // insert row into the database Note: no exception should be thrown todo make checkpoints update with whichever data is newest
        long inserted = database.insertWithOnConflict(CHECK_POINT_TABLE, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);

        Log.i(TAG, "Insert CheckPoint successful: " + inserted);

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

            // get the database from SQLiteOpenHelper.this
            SQLiteDatabase database = this.getReadableDatabase();

            // send query database for all events

            Cursor cursor = database.query(EVENT_TABLE, null, null, null, null, null, null);

            Log.i(TAG, "Cursor Created");
            if (cursor == null) {
                Log.i(TAG, "Cursor is null!");
            }

            // get the data from the cursor
            if(cursor != null) {

                Log.i(TAG, "Cursor wasn't null!");

                // add items to the list
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
                boolean subscribed;
                List<CheckPoint> checkPoints;
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
                    subscribed = getSubscribed(event_id);
                    checkPoints = this.getCheckpoints(event_id);

                    // create new event and add it to the list
                    event = new Event.Builder()
                            .setID(event_id)
                            .setTitle(title)
                            .setShortTitle(shortTitle)
                            .setAuthor(author)
                            .setDescription(description)
                            .setImageSrc(imageSrc)
                            .setLat(lat)
                            .setLon(lon)
                            .setQR(qr)
                            .setTimeCreated(timeCreated)
                            .setTimeUpdated(timeUpdated)
                            .setCheckPoints(checkPoints)
                            .setSubscribed(subscribed)
                            .build();

                    Log.i(TAG, String.format("%d %s %s %s %s %b", event_id, title, author, description, imageSrc, subscribed));

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
     * Get subscription status based on event_id.
     *
     * @param event_id The id of the event to find the subscription status of.
     * @return The boolean subscription status.
     */
    public boolean getSubscribed(int event_id) {

        // todo change this to work with the EVENT TABLE

        Log.i(TAG, "Getting events...");

        boolean subscribed = false;

        // try statement in case of database errors
        try {

            // get the database from SQLiteOpenHelper.this
            SQLiteDatabase database = this.getReadableDatabase();

            // send query database for subscription for event, NOTE: LIMIT 1
            Cursor cursor = database.query(true, EVENT_TABLE, new String[] {EVENT_COLUMN_SUBSCRIBED}, null, null, null, null, null, "1");

            Log.i(TAG, "Cursor Created");

            // get the data from the cursor
            if(cursor != null && cursor.moveToFirst() && ! cursor.isAfterLast()) {

                Log.i(TAG, "Getting subscription status from cursor");

                // get elements from cursor
                subscribed = cursor.getInt(0) == 1;

                Log.i(TAG, "Subscribed to event " + event_id + " " + subscribed);

                // close the cursor
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "error getting event subscription status from database");
            e.printStackTrace();
        }

        // return subscription status. If event was not null, this will be false.
        return subscribed;
    }

    /**
     * Update subscription status of an event.
     *
     * @param eventID The event to set subscription value.
     * @param subscribed The boolean subscription status.
     * @return The number of rows affected by update, or -1 if no rows affected.
     */
    public long updateSubscribed(int eventID, boolean subscribed) {

        SQLiteDatabase database = getWritableDatabase();

        // set up the row
        ContentValues contentValues = new ContentValues();
        contentValues.put(EVENT_COLUMN_SUBSCRIBED, subscribed ? 1 : 0);

        // insert row into the database Note: no exception should be thrown
        long updated = database.update(EVENT_TABLE, contentValues, EVENT_COLUMN_ID + "=?", new String[]{String.valueOf(eventID)});

        Log.i(TAG, "Event Subscription updated successful: " + updated);

        return updated;
    }

    /**
     * Update subscription status of an event.
     *
     * @param checkpointID The event to set subscription value.
     * @param checked The boolean subscription status.
     * @return The number of rows affected by update, or -1 if no rows affected.
     */
    public long updateChecked(int checkpointID, boolean checked) {

        SQLiteDatabase database = getWritableDatabase();

        // set up the row
        ContentValues contentValues = new ContentValues();
        contentValues.put(CHECK_POINT_COLUMN_CHECKED, checked ? 1 : 0);

        // insert row into the database Note: no exception should be thrown
        long updated = database.update(CHECK_POINT_TABLE, contentValues, CHECK_POINT_COLUMN_ID + "=?", new String[]{String.valueOf(checkpointID)});

        Log.i(TAG, "Check Point Checked updated successful: " + updated);

        return updated;
    }

    /**
     * Get all the checkpoints for the given eventID.
     *
     * @param eventID The event ID correlating with the checkpoints we want.
     * @return The list of checkpoint ids.
     */
    public List<CheckPoint> getCheckpoints(int eventID) {

        Log.i(TAG, "Getting checkpoints...");

        ArrayList<CheckPoint> checkPoints = new ArrayList<>();

        // try statement in case of database errors
        try {

            // get the database from SQLiteOpenHelper.this
            SQLiteDatabase database = this.getReadableDatabase();

            // send query database for all checkpoints
            Cursor cursor = database.query(CHECK_POINT_TABLE, null, CHECK_POINT_COLUMNT_EVENT_ID + "=?", new String[] {String.valueOf(eventID)}, null, null, null);
//            Cursor cursor = database.query(CHECK_POINT_TABLE, null, null, null, null, null, null);

            Log.i(TAG, "Cursor Created");

            // get the data from the cursor
            if(cursor != null) {

                Log.i(TAG, "Cursor wasn't null!");

                // add items to the list
                int checkpoint_id;
                int event_id;
                String title;
                String artist;
                String description;
                String imageSrc;
                double lat;
                double lon;
                String qr;
                String timeCreated;
                String timeUpdated;
                int checked;
                CheckPoint checkpoint;

                for(cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()) {

                    Log.i(TAG, "Getting event from cursor");

                    int pos = 0;

                    // get elements from cursor
                    checkpoint_id = cursor.getInt(pos ++);
                    event_id = cursor.getInt(pos ++);
                    title = cursor.getString(pos ++);
                    artist = cursor.getString(pos ++);
                    description = cursor.getString(pos ++);
                    imageSrc = cursor.getString(pos ++);
                    lat = cursor.getDouble(pos ++);
                    lon = cursor.getDouble(pos ++);
                    qr = cursor.getString(pos ++);
                    timeCreated = cursor.getString(pos ++);
                    timeUpdated = cursor.getString(pos ++);
                    checked = cursor.getInt(pos);

                    // create new checkpoint and add it to the list
                    checkpoint = new CheckPoint.Builder()
                            .setID(checkpoint_id)
                            .setEventID(event_id)
                            .setTitle(title)
                            .setArtist(artist)
                            .setDescription(description)
                            .setImageSrc(imageSrc)
                            .setLat(lat)
                            .setLon(lon)
                            .setQR(qr)
                            .setTimeCreated(timeCreated)
                            .setTimeUpdated(timeUpdated)
                            .setChecked(checked)
                            .build();

                    Log.i(TAG, String.format("%d %s %s %s %s", checkpoint_id, title, artist, description, imageSrc));

                    checkPoints.add(checkpoint);
                }

                // close the cursor
                cursor.close();
            }
            else {
                Log.i(TAG, "Cursor is null!");
            }

        } catch (Exception e) {
            Log.e(TAG, "error getting checkpoints from database");
            e.printStackTrace();
        }

        if(checkPoints.size() == 0) {
            Log.i(TAG, "No checkpoints were found for event_id: " + eventID);
        }

        // return events (which may be an empty list)
        return checkPoints;
    }

    /**
     * Get a single checkpoint by checkpoint id.
     *
     * @param checkpointID The id of the checkpoint to get.
     * @return The new checkpoint if it exists.
     * @throws android.content.res.Resources.NotFoundException If the checkpoint was not found.
     */
    public CheckPoint getCheckpoint(int checkpointID) {


        Log.i(TAG, "Getting checkpoints...");

        CheckPoint checkPoint = null;

        // try statement in case of database errors
        try {

            // get the database from SQLiteOpenHelper.this
            SQLiteDatabase database = this.getReadableDatabase();

            // send query database for all checkpoints
            Cursor cursor = database.query(CHECK_POINT_TABLE, null, CHECK_POINT_COLUMN_ID + "=?", new String[] {String.valueOf(checkpointID)}, null, null, "1");

            // get the data from the cursor
            if(cursor != null) {

                Log.i(TAG, "Cursor wasn't null!");

                // add items to the list
                int checkpoint_id;
                int event_id;
                String title;
                String artist;
                String description;
                String imageSrc;
                double lat;
                double lon;
                String qr;
                String timeCreated;
                String timeUpdated;
                int checked;

                for(cursor.moveToFirst(); ! cursor.isAfterLast(); cursor.moveToNext()) {

                    Log.i(TAG, "Getting checkpoint from cursor");

                    int pos = 0;

                    // get elements from cursor
                    checkpoint_id = cursor.getInt(pos ++);
                    event_id = cursor.getInt(pos ++);
                    title = cursor.getString(pos ++);
                    artist = cursor.getString(pos ++);
                    description = cursor.getString(pos ++);
                    imageSrc = cursor.getString(pos ++);
                    lat = cursor.getDouble(pos ++);
                    lon = cursor.getDouble(pos ++);
                    qr = cursor.getString(pos ++);
                    timeCreated = cursor.getString(pos ++);
                    timeUpdated = cursor.getString(pos ++);
                    checked = cursor.getInt(pos);

                    // create new checkpoint and add it to the list
                    checkPoint = new CheckPoint.Builder()
                            .setID(checkpoint_id)
                            .setEventID(event_id)
                            .setTitle(title)
                            .setArtist(artist)
                            .setDescription(description)
                            .setImageSrc(imageSrc)
                            .setLat(lat)
                            .setLon(lon)
                            .setQR(qr)
                            .setTimeCreated(timeCreated)
                            .setTimeUpdated(timeUpdated)
                            .setChecked(checked)
                            .build();

                    Log.i(TAG, String.format("%d %s %s %s %s", checkpoint_id, title, artist, description, imageSrc));
                }

                // close the cursor
                cursor.close();
            }
            else {
                Log.i(TAG, "No checkpoints were found for Event with " + checkpointID);
                throw new Resources.NotFoundException("No checkpoint was found with id: " + checkpointID);
            }

        } catch (Exception e) {
            Log.e(TAG, "error getting checkpoint from database");
            e.printStackTrace();
            throw new Resources.NotFoundException("No checkpoint was found with id: " + checkpointID);
        }

        // return events (which may be an empty list)
        return checkPoint;
    }

    /**
     * todo create methods to insert and update existing Events in the table
     * http://www.perfectapk.com/sqliteopenhelper-example.html
     */
}
