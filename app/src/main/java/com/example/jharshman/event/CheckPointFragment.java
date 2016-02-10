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
import android.widget.ListView;

import java.util.ArrayList;

public class CheckPointFragment extends Fragment {

    /**
     * Final Keys/Tags
     */
    private static final String TAG = "CheckPointFragment";
    public static final String EVENT_ID_KEY = "event_id_key";
    private static final String CHECK_POINT_KEY = "check_point_key";

    /**
     * Check points for list card view
     */
    ArrayList<CheckPoint> mCheckPoints;

    /**
     * List adapter for card view
     */
    CheckPointAdapter mListAdapter;

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

    public CheckPointFragment() {
        // required empty constructor
    }

    /**
     * Factory method for creating new instance of the
     * fragment
     *
     * @param id ID of the Event the checkpoints of whom we wish to display :P
     *
     * @return A new instance of fragment CheckPointFragment.
     */
    public static CheckPointFragment newInstance(int id) {

        // create check points fragment
        CheckPointFragment fragment = new CheckPointFragment();

        // set check point arguments on fragment
        Bundle args = new Bundle();
        args.putInt(EVENT_ID_KEY, id);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int eventID = -1;
        if (getArguments() != null) {
            // get Event ID
            eventID = getArguments().getInt(EVENT_ID_KEY);
        }

        Log.i(TAG, "Event ID passed in " + eventID);

        // todo pull check points from web service based on event id
        mCheckPoints = new ArrayList<>();
        for(int i = 0; i < 20; i ++) {
            CheckPoint checkPoint = new CheckPoint(42, "title " + i, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", "http://eridem.net/wp-content/uploads/2014/06/Android-Wallpaper-By-Scoobsti-1024x576.png", 49.0, 60.0);
            mCheckPoints.add(checkPoint);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_point, container, false);

        // set up list and list adapter
        mListAdapter = new CheckPointAdapter(getContext(), R.layout.fragment_check_point_card, mCheckPoints);
        mListView = (ListView) view.findViewById(R.id.fragment_check_point_card_list);
        mListView.setAdapter(mListAdapter);

        return view;
    }

    public void onButtonPressed(int id) {
        if (mListener != null) {
            mListener.onCheckPointInteraction(id);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
     * This interface must be implemented by activities which contain this fragment.
     */
    public interface OnFragmentInteractionListener {

        /**
         * Check Point was clicked
         *
         * @param id The id of the item clicked
         */
        void onCheckPointInteraction(int id);
    }
}
