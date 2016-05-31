/**
 * @file EventFragment.java
 * @author Bruce Emehiser
 * @date 2016 03 10
 *
 * Event fragment which handles the display
 * and user interactions for event data
 */


package com.example.jharshman.event;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EventFragment extends Fragment implements EventAdapter.OnEventClickListener, View.OnClickListener, DataManager.UpdateListener {

    private static final String TAG = "EventFragment";

    private OnFragmentInteractionListener mListener;

    private List<Event> mEvents;

    private ListView mListView;
    private EventAdapter mEventAdapter;
    private FloatingActionButton mFab;
    private LinearLayout mHeader;

    private boolean mInEditMode;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        setHeader();

        // get the event data
        DataManager dataManager = DataManager.instance(getContext());
        dataManager.setUpdateListener(this);
        if (mEvents == null) {
            mEvents = new ArrayList<>();
        } else {
            mEvents.clear();
        }
        mEvents.addAll(dataManager.getSubscribedEvents());

        // set up list view
        mListView = (ListView) view.findViewById(R.id.fragment_event_list_view);
        mEventAdapter = new EventAdapter(view.getContext(), R.layout.fragment_event_card, mEvents);
        mListView.setAdapter(mEventAdapter);

        // set click listeners
        mEventAdapter.setOnEventClickListener(this);

        // set up floating action button
        mFab = (FloatingActionButton) view.findViewById(R.id.fragment_event_fab);
        mFab.setOnClickListener(this);

        // set up header
        Button headerDoneButton = (Button) view.findViewById(R.id.fragment_event_header_done_image_button);
        headerDoneButton.setOnClickListener(this);

        mHeader = (LinearLayout) view.findViewById(R.id.fragment_event_header);
        mHeader.setVisibility(View.GONE);

        mInEditMode = false;

        return view;
    }

    private void setHeader() {
        TextView header = (TextView) getActivity().findViewById(R.id.headerTitle);
        header.setText("  My Events");
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
     * Notify listener that event has been clicked
     *
     * @param view The view which has been clicked
     * @param event The Event
     */
    @Override
    public void onEventClick(View view, Event event) {

        Log.i(TAG, String.format("onEventClick(%d, %s)", view.getId(), event.getTitle()));

        if (view.getId() == R.id.event_card_fab) {

            if (mInEditMode) {
                // update subscription state
                event.setSubscribed(event.getSubscribed() == 0 ? 1 : 0);
                // update the database to match
                DataManager.instance(getContext()).updateSubscribed(event.getID(), event.getSubscribed());

                // update the view
                mEventAdapter.notifyDataSetChanged();
            } else {
                // notify listener of event click
                if (mListener != null) {
                    mListener.onEventInteraction(event.getID());
                }
            }
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

        // "add" floating action button, show header
        if (view.getId() == R.id.fragment_event_fab) {
            mInEditMode = true;

            mFab.hide();
            mHeader.setVisibility(View.VISIBLE);
            mEventAdapter.setShowEditButtons(true);
            mEvents.clear();
            mEvents.addAll(DataManager.instance(getContext()).getSubscribedEvents());

            int radius = 3;

            LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(location != null) {

                    // get the events based on lat, lon, and radius
                    DataManager.instance(getContext()).getLocalEvents(radius, location.getLatitude(), location.getLongitude(), new DataManager.GetLocalEventsCallback() {
                        @Override
                        public void success(List<Event> events) {

                            Log.i(TAG, "Get Local Events Server Connection Successful");

                            for (Event event : events) {
                                if(! mEvents.contains(event)) {
                                    mEvents.add(event);
                                }
                            }

                            // update list view
                            onDataUpdated(DataManager.instance(getContext()));
                        }

                        @Override
                        public void failure(String message) {

                            // pop toast the long way, because of ui threading stuff
                            ((Activity) getContext()).runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getContext(), "Server Connection Failed", Toast.LENGTH_LONG).show();
                                }
                            });

                            Log.e(TAG, "Get Local Events Server Connection Failed");
                        }
                    });
                }
            }

//            mEvents.addAll(DataManager.instance(getContext()).getLocalEvents(radius, lat, lon));
        }
        // done button, hide header
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
//        DataManager.instance(getContext()).flush();
    }
}
