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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class CheckPointFragment extends Fragment implements View.OnClickListener {

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
    private int mCheckPointID;

    /**
     * Checkpoint event callback listener.
     */
    OnFragmentInteractionListener mListener;

    public CheckPointFragment() {
        // required empty constructor
    }

    /**
     * Factory method for creating or getting new singleton
     * getInstance of the fragment
     *
     * @param checkPointID The checkpoint to display
     *
     * @return A new getInstance of fragment CheckPointFragment.
     */
    public static CheckPointFragment newInstance(int checkPointID) {

        // create check points fragment
        if(mInstance == null) {
             mInstance = new CheckPointFragment();
        }

        // set check point arguments on fragment
        Bundle args = new Bundle();
        args.putInt(CHECK_POINT_KEY, checkPointID);
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
            mCheckPointID =  getArguments().getInt(CHECK_POINT_KEY);
        }
        else {
            throw new NullPointerException("Check point must be set using Key CheckPointFragment.CHECK_POINT_KEY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_check_point, container, false);

        // get the data
        CheckPoint checkPoint = DataManager.instance(getContext()).getCheckpoint(mCheckPointID);

        Log.i(TAG, "Getting checkpoint from data manager: " + checkPoint.toString());

        // get views
        ImageView image = (ImageView) view.findViewById(R.id.fragment_check_point_title_image);
        TextView title = (TextView) view.findViewById(R.id.fragment_check_point_title_text);
        TextView artist = (TextView) view.findViewById(R.id.fragment_check_point_artist_text);
        TextView description = (TextView) view.findViewById(R.id.fragment_check_point_description_text);
        ImageView checkIn = (ImageView) view.findViewById(R.id.fragment_check_point_check_in_button);
        ImageView share = (ImageView) view.findViewById(R.id.fragment_check_point_card_share_button);
        ImageView map = (ImageView) view.findViewById(R.id.fragment_check_point_map_button);

        // load data into views
        Picasso.with(getContext())
                .load(checkPoint.getImageSrc())
                .into(image);
        title.setText(checkPoint.getTitle());
        artist.setText(checkPoint.getArtist());
        description.setText(checkPoint.getDescription());

        if(checkPoint.getChecked() == 1) {
            checkIn.setImageResource(R.drawable.ic_cloud_done_black_24dp);
        }
        else {
            // set click listeners
            checkIn.setOnClickListener(this);
        }
        // set click listeners
        share.setOnClickListener(this);
        map.setOnClickListener(this);


        return view;
    }

    /**
     * Click listener for buttons.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        // get the checkpoint from the data manager
        CheckPoint checkPoint = DataManager.instance(getContext()).getCheckpoint(mCheckPointID);

        switch (view.getId()) {

            case R.id.fragment_check_point_check_in_button:

                Log.i(TAG, "Check point check in button clicked");

                // todo only launch validation if not already checked

                // todo if in gps range, check in


                // todo otherwise launch qr code scanner
                mListener.onCheckPointInteraction(R.id.fragment_check_point_check_in_button, checkPoint.getID());

//                // store that user checked in. Note: click should only ever check in, never un-check
//                boolean checkSaved = DataManager.instance(getContext()).updateChecked(mCheckPoint.getID(), true);
//
//                if(checkSaved) {
//
//                    // update image button
//                    ImageView checkButton = (ImageView) getActivity().findViewById(R.id.fragment_check_point_check_in_button);
//                    checkButton.setImageResource(R.drawable.ic_cloud_done_black_24dp);
//                }

                break;
            case R.id.fragment_check_point_card_share_button:
                // todo handle share button click

                Log.i(TAG, "Check Point Share button clicked");

                break;
            case R.id.fragment_check_point_map_button:

                Log.i(TAG, "Check Point Map button clicked");

                // call callback with check point button id, and current checkpoint id
                mListener.onCheckPointInteraction(R.id.fragment_check_point_map_button, checkPoint.getID());

                break;
        }
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

    /**
     * This interface must be implemented by activities that contain
     * Checkpoint fragment
     */
    public interface OnFragmentInteractionListener {

        /**
         * Interaction from Check Point. Launch the appropriate
         * fragment, and pass it the value.
         *
         * @param button_id The button which was clicked to start
         *                      the interaction callback.
         * @param checkpoint_id The id of the check point.
         */
        public void onCheckPointInteraction(int button_id, int checkpoint_id);
    }
}
