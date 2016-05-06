/**
 * @file CheckPointFragment.java
 * @author Bruce Emehiser
 * @date 2016 02 23
 *
 * Check Point Fragment used to display checkpoints
 * in a list and handle clicks on them.
 */

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

public class CheckPointListFragment extends Fragment implements AdapterView.OnItemClickListener {

    /**
     * Final Keys/Tags
     */
    private static final String TAG = "CheckPointFragment";
    private static final String EVENT_ID_KEY = "event_id_key";

    /**
     * Check points for list card view
     */
    List<CheckPoint> mCheckPoints;

    /**
     * List adapter for card view
     */
    CheckPointListAdapter mListAdapter;

    /**
     * List view used to display contents of mCheckPoints
     * via mListAdapter
     */
    ListView mListView;

    /**
     * Fragment transaction listener for returning data
     * from fragment
     */
    private OnFragmentInteractionListener mListener;

    /**
     * Singleton check point list fragment
     */
    private static CheckPointListFragment mInstance;

    public CheckPointListFragment() {
        // required empty constructor
    }

    /**
     * Factory method for creating new getInstance of the
     * fragment
     *
     * @param event_id ID of the Event the checkpoints of whom we wish to display :P
     *
     * @return A new getInstance of fragment CheckPointFragment.
     */
    public static CheckPointListFragment getInstance(int event_id) {

        // create check points fragment
        if(mInstance == null) {
            mInstance = new CheckPointListFragment();

            // set check point arguments on fragment. The bundle is used to pass the event id to the check point fragment
            Bundle args = new Bundle();
            args.putInt(EVENT_ID_KEY, event_id);
            mInstance.setArguments(args);
        }
        else {
            Bundle bundle = mInstance.getArguments();
            bundle.putInt(EVENT_ID_KEY, event_id);
        }

        return mInstance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int eventID;
        if (getArguments() != null) {
            // get Event ID
            eventID = getArguments().getInt(EVENT_ID_KEY);
        }
        else {
            Log.e(TAG, "Event id must be set");
            return;
        }

        Log.i(TAG, "Event ID passed in " + eventID);

        // get the check points from the data manager.
        if(mCheckPoints == null) {
            mCheckPoints = new ArrayList<>();
        }
        mCheckPoints.clear();
        mCheckPoints.addAll(DataManager.instance(getContext()).getCheckpoints(eventID));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_point_list, container, false);

        // set up list and list adapter
        mListAdapter = new CheckPointListAdapter(getContext(), R.layout.fragment_check_point_card, mCheckPoints);
        mListView = (ListView) view.findViewById(R.id.fragment_check_point_list);
        mListView.setAdapter(mListAdapter);
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
     * A list item has been clicked
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(mListener != null) {
            mListener.onCheckPointListInteraction(mCheckPoints.get(position).getID());
        }
    }

    /**
     * This interface must be implemented by activities which contain this fragment.
     */
    public interface OnFragmentInteractionListener {

        /**
         * Check Point was clicked.
         *
         * @param checkPointID The id of the check point which was clicked.
         */
        void onCheckPointListInteraction(int checkPointID);
    }
}
