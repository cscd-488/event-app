/**
 * @file DataManager.java
 * @author Bruce Emehiser
 * @date 2016 03 07
 *
 * Data manager which manages Event and Check Point data
 * and the storage and retrieval of them.
 */

package com.example.jharshman.event;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataManager implements Callback {

    private static final String TAG = "DataManager";

    /**
     * Listeners who want to be notified of changes
     */
    private static ArrayList<UpdateListener> mListeners;

    /**
     * Singleton instance of data manager
     */
    private static DataManager mInstance;

    private Context mContext;

    private DataHelper mDataHelper;

    /**
     * Event and Check Point Data
     * It is final so that it will only be modified or updated,
     * but never destroyed and recreated with a different reference.
     * This allows it to be used easily in ArrayAdapters etc.
     *
     * The list must also be synchronized, so if the list is being updated or read
     * it won't cause an exception if something trys to access it from another thread
     */
//    private final List<Event> mEvents = Collections.synchronizedList(new ArrayList<Event>());
//    private final ArrayList<Event> mEvents = new ArrayList<>();

    private boolean mDataSetChanged;

    /**
     * Server connection and data parser
     */
    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();

    private DataManager(Context context) {
        // singleton constructor will read data from server on first run
        mContext = context;

        // todo integrate data helper into data manager
        Log.i(TAG, "Creating new instance of data helper");
        mDataHelper = DataHelper.newInstance(context);

        mListeners = new ArrayList<>();

//        mEvents.addAll(readCachedData());
        mDataSetChanged = false;
        getEventData();
    }

    public static DataManager instance(Context context) {
        if(mInstance == null) {
            mInstance = new DataManager(context);
        }

        return mInstance;
    }

    // todo pull data from web server based on location and radius
    // todo put data to web server

    /**
     * Get event data from the server
     */
    private void getEventData() {

        Activity activity = (Activity) mContext;

        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        String token = sharedPreferences.getString(activity.getString(R.string.jwt_server_token), "");

        Request request = new Request.Builder()
                .url(activity.getString(R.string.magpie_server_event_data))
                .addHeader(activity.getString(R.string.jwt_server_token), token)
                .build();

        mClient.newCall(request).enqueue(this);
    }

    /**
     * Called when the request could not be executed due to cancellation, a connectivity problem or
     * timeout. Because networks can fail during an exchange, it is possible that the remote server
     * accepted the request before the failure.
     *
     * @param call call
     * @param e exception
     */
    @Override
    public void onFailure(Call call, IOException e) {

        try {
            Toast.makeText(mContext, "Server Connection Failed", Toast.LENGTH_LONG).show();
        } catch (Exception f) {
            // something probably happened to the activity, and we can ignore it
        }
    }

    /**
     * Called when the HTTP response was successfully returned by the remote server. The callback may
     * proceed to read the response body with {@link Response#body}. The response is still live until
     * its response body is closed with {@code response.body().close()}. The recipient of the callback
     * may even consume the response body on another thread.
     * <p/>
     * <p>Note that transport-layer success (receiving a HTTP response code, headers and body) does
     * not necessarily indicate application-layer success: {@code response} may still indicate an
     * unhappy HTTP response code like 404 or 500.
     *
     * @param call call
     * @param response response
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {

        Log.i(TAG, "onResponse()");

        if (! response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        JsonParser parser = new JsonParser();
        JsonArray jArray = parser.parse(response.body().string()).getAsJsonArray();
        ArrayList<Event> events = new ArrayList<>();

        // this parses out the json array. the Events and Checkpoints must have @SerializedName("id") notation
        for(JsonElement obj : jArray )
        {
            Event event = mGson.fromJson(obj , Event.class);
            events.add(event);
            Log.i(TAG, "Event: " + event.toString());
        }

        // update the current list of events
        updateEvents(events);
    }

    /**
     * Updates the current list of events with
     * the new data
     *
     * @param events The new updates to write
     */
    private void updateEvents(List<Event> events) {

        // insert all events into the database todo make this only update the database on collision
        for(Event event : events) {
            mDataHelper.insertEvent(event);

            // insert all checkpoints from event
            for(CheckPoint checkPoint : event.getCheckPoints()) {
                mDataHelper.insertCheckPoint(checkPoint);
            }
        }

        // clue listeners in to the fact that new data awaits
        notifyUpdateListeners();
    }

//    /**
//     * Updates the current list of events with
//     * the new data
//     *
//     * @param newEvents The new updates to write
//     */
//    private void updateEvents(List<Event> newEvents) {
//
//        // todo make this just update the events instead of overwriting them. Make sure that the subscription check in data are saved
//
//        mEvents.clear();
//        mEvents.addAll(newEvents);
//
//        // clue listeners in to the fact that new data awaits
//        notifyUpdateListeners();
//    }

    /**
     * Read cached event data if it exists
     *
     * @return Data found/not found
     */
    @SuppressWarnings("unchecked")
    private List<Event> readCachedData() {

        Activity activity = (Activity) mContext;

        // read all the event data from the cache file
        File cachedEventData = new File(activity.getCacheDir(), activity.getString(R.string.event_data_cache_file));

        // if file does not exist, then return empty list of events
        if(! cachedEventData.exists()) {
            return new ArrayList<>();
        }

        ArrayList<Event> events = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(cachedEventData));
            events = (ArrayList<Event>) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            // ignore
        }

        if(events == null) {
            return new ArrayList<>();
        }

        return events;
    }

    private void writeCachedData(List<Event> events) {

        Activity activity = (Activity) mContext;

        try {
            // save all the data out to storage for use by CheckPointFragment
            File cachedEventData = new File(activity.getCacheDir(),activity.getString(R.string.event_data_cache_file));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(cachedEventData));
            objectOutputStream.writeObject(events);

        } catch (IOException e) {
            Log.i(TAG, "Error writing cached data");
        }
    }

    /**
     * Get all the events from the data helper.
     *
     * @return List of all events.
     */
    public List<Event> getEvents() {

        // get all events from data helper
        List<Event> events = mDataHelper.getEvents();

        return events;
    }

    /**
     * Get the events that the user is subscribed to.
     * This returns a shallow copy of the event list.
     *
     * @return The list of subscribed events
     */
    public List<Event> getSubscribedEvents() {
        // todo make this return a deep copy or somehow make mEvents immutable (this will happen when sqlite database gets implemented)

        ArrayList<Event> subscribedEvents = new ArrayList<>();

//        for (Event event : mEvents) {
//            if (event.getSubscribed()) {
//                subscribedEvents.add(event);
//            }
//        }

        return subscribedEvents;
    }

    /**
     * Get all the checkpoints for the given event.
     *
     * @param eventID The event to get checkpoints of.
     * @return List of checkpoints for given event.
     */
    public List<CheckPoint> getCheckPoints(int eventID) {

        return mDataHelper.getCheckpoints(eventID);
    }

    public List<CheckPoint> getCheckpoints(int eventID) {
        // todo make this make immutable by other programs (also solved by using a sqlite database)

//        for(int i = 0; i < mEvents.size(); i ++) {
//            if(mEvents.get(i).getID() == eventID) {
//                return Arrays.asList(mEvents.get(i).getCheckPoints());
//            }
//        }
//        throw new NoSuchElementException("No elements with event id " + eventID + " found");

        List<CheckPoint> checkPoints;

        try {
            checkPoints = mDataHelper.getCheckpoints(eventID);
        } catch (NoSuchElementException e) {
            Log.e(TAG, e.toString());
            checkPoints = new ArrayList<>();
        }

        return checkPoints;
    }

    /**
     * Get updated data set from the server
     */
    public void update() {
        getEventData();
    }

//    /**
//     * Write event data to local storage
//     * This should be done in onPause(), or whenever something
//     * important changes
//     */
//    public void flush() {
//        // save the events to disk
//        writeCachedData(mEvents);
//
//        if(mDataSetChanged) {
//            // todo save the updates to the server
//            // this will include subscriptions and check in data
//        }
//    }

    /**
     * This sets a flag which is used to decide whether or not
     * to update the server with any changes to the data.
     */
    public void setDataChanged() {
        mDataSetChanged = true;
    }

    public interface UpdateListener {

        /**
         * Called when server receives new data and updates data set
         *
         * @param dataManager This
         */
        void onDataUpdated(DataManager dataManager);
    }

    /**
     * Remove listener from list if listener exists
     *
     * @param listener The listener to remove
     */
    public void setUpdateListener(UpdateListener listener) {
        mListeners.add(listener);
    }

    /**
     * Add listener to be notified when data set changes
     *
     * @param listener Listener to add to listener list
     */
    public void removeUpdateListener(UpdateListener listener) {
        mListeners.remove(listener);
    }

    /**
     * Notify all listeners that the data set has changed
     */
    private void notifyUpdateListeners() {

        for(UpdateListener listener : mListeners) {
            try {
                listener.onDataUpdated(this);
            } catch (NullPointerException e) {
                mListeners.remove(listener);
            }
        }
    }
}
