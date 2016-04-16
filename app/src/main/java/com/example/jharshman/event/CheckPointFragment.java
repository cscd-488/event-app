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
import android.widget.ImageView;
import android.widget.TextView;

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
                .load(mCheckPoint.getImageSrc())
                .into(image);
        title.setText(mCheckPoint.getTitle());
        artist.setText(mCheckPoint.getArtist());
        description.setText(mCheckPoint.getDescription());

        if(mCheckPoint.getChecked()) {
            checkIn.setImageResource(R.drawable.ic_cloud_done_black_24dp);
        }

        // set click listeners todo finish setting click listeners
        checkIn.setOnClickListener(this);


        return view;
    }

    /**
     * Click listener for buttons.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fragment_check_point_check_in_button:

                // store that user checked in. Note: click should only ever check in, never un-check
                boolean checkSaved = DataManager.instance(getContext()).updateChecked(mCheckPoint.getID(), true);

                if(checkSaved) {

                    // update image button
                    ImageView checkButton = (ImageView) getActivity().findViewById(R.id.fragment_check_point_check_in_button);
                    checkButton.setImageResource(R.drawable.ic_cloud_done_black_24dp);
                }

                break;
            case R.id.fragment_check_point_card_share_button:
                // todo handle share button click
                break;
            case R.id.fragment_check_point_map_button:
                // todo handle map button click
                break;
        }

    }
}
