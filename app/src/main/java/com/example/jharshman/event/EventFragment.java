package com.example.jharshman.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEventInteraction} interface
 * to handle interaction events.
 */
public class EventFragment extends Fragment implements AdapterView.OnItemClickListener, Callback, EventAdapter.OnEventClickListener {

    private static final String TAG = "EventFragment";

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();

    private OnEventInteraction mListener;

    private List<Event> mEvents;

    private ListView mListView;
    private EventAdapter mEventAdapter;
    private SharedPreferences mSharedPreferences;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_event_list_view);

        mEvents = new ArrayList<>();

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String token = mSharedPreferences.getString(getString(R.string.jwt_server_token), null);

        Log.i(TAG, "Token:" + token);


        // todo do this more elegantly
        try {
            // read cached data if there is any
            mEvents = readCachedData();

        } catch (NullPointerException e) {
            Log.i(TAG, "There was no cached data");
            try {

                // read data from server
                getEventData();

            } catch (Exception f) {
                Log.e(TAG, "Could not get event data from server");
            }
        }


        // create adapter
        mEventAdapter = new EventAdapter(view.getContext(), R.layout.fragment_event_card, mEvents);
        // set adapter on collection
        mListView.setAdapter(mEventAdapter);

        mEventAdapter.setOnEventClickListener(this);
        mListView.setOnItemClickListener(this);

        return view;
    }

    /**
     * Get event data from the server
     *
     * @throws Exception exception
     */
    public void getEventData() throws Exception {

        Log.i(TAG, "getEventData()");

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String token = mSharedPreferences.getString(getString(R.string.jwt_server_token), "");

        Request request = new Request.Builder()
                .url(getString(R.string.magpie_server_event_data))
                .addHeader(getString(R.string.jwt_server_token), token)
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
            Log.e(TAG, "onFailure()");
        } catch (Exception f) {
            // fragment no longer loaded
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

        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray jArray = parser.parse(response.body().string()).getAsJsonArray();

        // if the events array list is not null, we can't overwrite it because it is being used by the array adapter
        if(mEvents == null) {
            mEvents = new ArrayList<Event>();
        }
        else {
            mEvents.clear();
        }

        // this parses out the json array. the Events and Checkpoints must have @SerializedName("id") notation
        for(JsonElement obj : jArray )
        {
            Event event = gson.fromJson( obj , Event.class);
            mEvents.add(event);
        }

        // save the data for later use
//        writeCachedData(mEvents);

        // updating the list adapter must be done from the UI Thread
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEventAdapter.notifyDataSetChanged();
            }
        });

        // testing log
        for(Event e : mEvents) {
            Log.i(TAG, e.toString());
        }
    }

    /**
     * Read cached event data if it exists
     *
     * @return Data found/not found
     */
    private List<Event> readCachedData() {

        // read all the event data from the cache file
        File cachedEventData = new File(getActivity().getCacheDir(), getString(R.string.event_data_cache_file));

        // if file does not exist, then return false
        if(! cachedEventData.exists()) {
            throw new NullPointerException("There is no cached data");
        }

        ArrayList<Event> events = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(cachedEventData));

            events = (ArrayList<Event>) objectInputStream.readObject();

        } catch (IOException e) {
            Log.e(TAG, "error reading file for loading event data, IOException");
            throw new NullPointerException("There is no cached data");
        } catch (ClassNotFoundException f) {
            Log.e(TAG, "error reading file for loading event data, ClassNotFound");
            throw new NullPointerException("There is no cached data");
        }
        if(events == null) {
            throw new NullPointerException("There is no cached data");
        }
        return events;
    }

    private void writeCachedData(List<Event> events) {

        try {
            // save all the data out to storage for use by CheckPointFragment
            File cachedEventData = new File(getActivity().getCacheDir(), getString(R.string.event_data_cache_file));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(cachedEventData));
            objectOutputStream.writeObject(events);

        } catch (IOException e) {
            Log.i(TAG, "Error writing cached data");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventInteraction) {
            mListener = (OnEventInteraction) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnEventInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Log.i(TAG, "Item Clicked " + position);

        if(mListener != null) {
            mListener.onEventInteraction(mEvents.get(position).getID());
        }
    }

    /**
     * Notify listener that event has been clicked
     *
     * @param view The view which has been clicked
     * @param event The Event
     */
    @Override
    public void onEventClick(View view, Event event) {

        Log.i(TAG, String.format("onEventClick(%d, %s)", view.getId(), event.getTitle()));

        if(view.getId() == R.id.fragment_collections_add_delete_button) {

            // update subscription state
            event.setSubscribed(! event.getSubscribed());

            mEventAdapter.notifyDataSetChanged();
        }
    }

    /**
     * This interface must be implemented by activities that contain
     * Event fragments
     */
    public interface OnEventInteraction {

        /**
         * Interaction from Event. Load proper
         * set of CheckPoints based on id.
         * @param id The Event Event Key
         */
        void onEventInteraction(int id);
    }

    @Override
    public void onPause() {
        super.onPause();

        // save the current events list which contains subscriptions etc
        writeCachedData(mEvents);
    }
}
