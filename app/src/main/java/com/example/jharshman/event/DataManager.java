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
import java.util.Arrays;
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
    private static Context mContext;

    /**
     * Event and Check Point Data
     * It is final so that it will only be modified or updated,
     * but never destroyed and recreated with a different reference.
     * This allows it to be used easily in ArrayAdapters etc.
     */
    private final List<Event> mEvents = new ArrayList<Event>();

    /**
     * Server connection and data parser
     */
    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();

    private DataManager() {
        // singleton constructor will read data from server on first run
        mEvents.addAll(readCachedData());
        getEventData();
    }

    public static DataManager instance(Context context) {

        if(mInstance == null) {
            mContext = context;
            mInstance = new DataManager();
            mListeners = new ArrayList<>();
        }

        return mInstance;
    }

    // todo pull data from web server ( based on location and radius )
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
     * @param newEvents The new updates to write
     */
    private void updateEvents(List<Event> newEvents) {

        //todo make this faster than O(n^2)

        boolean found;
        Event newEvent;
        for(int i = 0; i < newEvents.size(); i ++) {
            newEvent = newEvents.get(i);
            found = false;
            for(int j = 0; j < mEvents.size() && ! found; j ++) {
                if(mEvents.get(j).getID() == newEvent.getID()) {
                    found = true;
                    // update that event by overwriting with new event
                    // todo make this only overwrite some stuff, and keep the check in and subscription data that is in the current events
                    mEvents.set(j, newEvent);
                }
            }
            if(! found) {
                mEvents.add(newEvent);
            }
        }

        // clue listeners in to the fact that new data awaits
        notifyUpdateListeners();
    }

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

    // todo maybe make this return a deep copy of events instead of events themselves
    public List<Event> getEvents() {

        return mEvents;
    }

    public List<CheckPoint> getCheckpoints(int eventID) {

        // todo make this faster than O(n)
        for(int i = 0; i < mEvents.size(); i ++) {
            if(mEvents.get(i).getID() == eventID) {
                return Arrays.asList(mEvents.get(i).getCheckPoints());
            }
        }
        throw new NoSuchElementException("No elements with event id " + eventID + " found");
    }

    /**
     * Get updated data set from the server
     */
    public void update() {
        getEventData();
    }

    /**
     * Write event data to local storage
     * This should be done in onPause(), or whenever something
     * important changes
     */
    public void flush() {
        writeCachedData(mEvents);
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
