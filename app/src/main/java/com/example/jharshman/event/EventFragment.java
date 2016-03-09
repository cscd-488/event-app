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

import java.util.List;

import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EventFragment extends Fragment implements AdapterView.OnItemClickListener, EventAdapter.OnEventClickListener, DataManager.UpdateListener {

    private static final String TAG = "EventFragment";

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();

    private OnFragmentInteractionListener mListener;

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

        // get the event data
        DataManager dataManager = DataManager.instance(getContext());
        mEvents = dataManager.getEvents();
        dataManager.setUpdateListener(this);

        mListView = (ListView) view.findViewById(R.id.fragment_event_list_view);

        // create adapter
        mEventAdapter = new EventAdapter(view.getContext(), R.layout.fragment_event_card, mEvents);
        // set adapter on events collection
        mListView.setAdapter(mEventAdapter);

        mEventAdapter.setOnEventClickListener(this);
        mListView.setOnItemClickListener(this);


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
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
     * Called when server receives new data and updates data set
     *
     * @param dataManager This
     */
    @Override
    public void onDataUpdated(DataManager dataManager) {

        // update the list view
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEventAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain
     * Event fragments
     */
    public interface OnFragmentInteractionListener {

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
        DataManager.instance(getContext()).flush();
    }
}
