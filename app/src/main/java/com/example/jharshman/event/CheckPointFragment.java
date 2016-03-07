/**
 * @file CheckPointFragment.java
 * @author Bruce Emehiser
 * @date 2016 02 23
 *
 * Check Point Fragment used to display checkpoints
 * in a list and handle clicks on them.
 */

package com.example.jharshman.event;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CheckPointFragment extends Fragment {

    /**
     * Final Keys/Tags
     */
    private static final String TAG = "CheckPointFragment";
    public static final String CHECK_POINT_KEY = "check_point_key";

    /**
     * Singleton getInstance of fragment
     */
    private static CheckPointFragment mInstance;

    /**
     * Check point we are representing and handling in the fragment
     */
    private CheckPoint mCheckPoint;

    public CheckPointFragment() {
        // required empty constructor
    }

    /**
     * Factory method for creating or getting new singleton
     * getInstance of the fragment
     *
     * @param checkPoint The checkpoint to display
     *
     * @return A new getInstance of fragment CheckPointFragment.
     */
    public static CheckPointFragment newInstance(CheckPoint checkPoint) {

        // create check points fragment
        if(mInstance == null) {
             mInstance = new CheckPointFragment();
        }

        // set check point arguments on fragment
        Bundle args = new Bundle();
        args.putSerializable(CHECK_POINT_KEY, checkPoint);
        mInstance.setArguments(args);

        return mInstance;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int eventID = -1;
        if (getArguments() != null) {
            // get Event ID
            mCheckPoint = (CheckPoint) getArguments().getSerializable(CHECK_POINT_KEY);
        }
        else {
            throw new NullPointerException("Check point must be set using Key CheckPointFragment.CHECK_POINT_KEY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_point, container, false);

        // todo load up the views with data

        return view;
    }
}
