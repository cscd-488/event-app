/**
 * @file EventFragment.java
 * @author Bruce Emehiser
 * @date 2016 03 10
 *
 * Event fragment which handles the display
 * and user interactions for event data
 */


package com.example.jharshman.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class EventFragment extends Fragment implements AdapterView.OnItemClickListener, EventAdapter.OnEventClickListener, View.OnClickListener, DataManager.UpdateListener {

    private static final String TAG = "EventFragment";

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();

    private OnFragmentInteractionListener mListener;

    private List<Event> mEvents;

    private ListView mListView;
    private EventAdapter mEventAdapter;
    private SharedPreferences mSharedPreferences;
    private FloatingActionButton mFab;
    private RelativeLayout mHeader;

    private boolean mInEditMode;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        // get the event data
        DataManager dataManager = DataManager.instance(getContext());
        dataManager.setUpdateListener(this);
        if(mEvents == null) {
            mEvents = new ArrayList<>();
        }
        else {
            mEvents.clear();
        }
        mEvents.addAll(dataManager.getSubscribedEvents());

        // set up list view
        mListView = (ListView) view.findViewById(R.id.fragment_event_list_view);
        mEventAdapter = new EventAdapter(view.getContext(), R.layout.fragment_event_card, mEvents);
        mListView.setAdapter(mEventAdapter);

        mEventAdapter.setOnEventClickListener(this);
        mListView.setOnItemClickListener(this);

        // set up floating action button
        mFab = (FloatingActionButton) view.findViewById(R.id.fragment_event_fab);
        mFab.setOnClickListener(this);

        // set up header
        ImageView headerDoneButton = (ImageView) view.findViewById(R.id.fragment_event_header_done_image_button);
        headerDoneButton.setOnClickListener(this);

        mHeader = (RelativeLayout) view.findViewById(R.id.fragment_event_header);
        mHeader.setVisibility(View.GONE);

        mInEditMode = false;

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

        if(view.getId() == R.id.event_card_fab) {

            if(mInEditMode) {
                // update subscription state
                event.setSubscribed(!event.getSubscribed());

                // update the view
                mEventAdapter.notifyDataSetChanged();
            }
            else {
                // notify listener of event click
                if(mListener != null) {
                    mListener.onEventInteraction(event.getID());
                }
            }

            // todo set data set changed flag so that the server will get updated when the data is saved
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
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        // hide or show event list editing views
        if(view.getId() == R.id.fragment_event_fab) {
            mInEditMode = true;

            mFab.hide();
            mHeader.setVisibility(View.VISIBLE);
            mEventAdapter.setShowEditButtons(true);
            mEvents.clear();
            mEvents.addAll(DataManager.instance(getContext()).getEvents());
        }
        else if(view.getId() == R.id.fragment_event_header_done_image_button) {
            mInEditMode = false;

            mFab.show();
            mHeader.setVisibility(View.GONE);
            mEventAdapter.setShowEditButtons(false);
            mEvents.clear();
            mEvents.addAll(DataManager.instance(getContext()).getSubscribedEvents());
        }
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
