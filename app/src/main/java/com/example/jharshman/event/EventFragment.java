package com.example.jharshman.event;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnEventInteraction} interface
 * to handle interaction events.
 */
public class EventFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "EventFragment";

    private OnEventInteraction mListener;

    private List<Event> mEvents;

    private ListView mListView;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event, container, false);

//        NestedScrollView nestedScrollView = (NestedScrollView) view.findViewById(R.id.fragment_collections_card_view);
        mListView = (ListView) view.findViewById(R.id.fragment_event_list_view);

        // todo pull data from web service to put into list view
        mEvents = new ArrayList<>();
        // add blah data
        for(int i = 0; i < 10; i ++) {
            Event event = new Event(i, "EWU " + i, "Eastern Treasure Hunt", "", "", "", "", false, new CheckPoint[]{});
            mEvents.add(event);
        }
        // create adapter
        EventAdapter eventAdapter = new EventAdapter(view.getContext(), R.layout.fragment_event_card, mEvents);
        // set adapter on collection
        mListView.setAdapter(eventAdapter);

        mListView.setOnItemClickListener(this);

        return view;
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
}
