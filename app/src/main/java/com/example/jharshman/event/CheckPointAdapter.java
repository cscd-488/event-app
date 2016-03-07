/**
 * @file CheckPointAdapter.java
 * @author Bruce Emehiser
 * @date 2016 02 23
 *
 * This is a simple array adapter for holding
 * event and waypoint cards. It provides a clean
 * and simple user interface.
 */

package com.example.jharshman.event;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Card Adapter which holds list of Card cards
 */
public class CheckPointAdapter extends ArrayAdapter<CheckPoint> implements View.OnClickListener {

    private static final String TAG = "CheckPointAdapter";

    /**
     * List of card objects currently being displayed in list view
     */
    List<CheckPoint> mCheckPoints;

    /**
     * Listener for click events
     */
    OnCheckPointClickListener mListener;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param checkPoints  The objects to represent in the ListView.
     */
    public CheckPointAdapter(Context context, int resource, List<CheckPoint> checkPoints) {
        super(context, resource, checkPoints);

        mCheckPoints = checkPoints;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CheckPointViewHolder holder;
        if(convertView == null) {
            // inflate layout for card view
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_check_point_card, parent, false);

            // create new view holder for current check point
            holder = new CheckPointViewHolder();

            // set views in view holder
            holder.mTitle = (TextView) convertView.findViewById(R.id.fragment_check_point_card_title_text);
            holder.mImage = (ImageView) convertView.findViewById(R.id.fragment_check_point_card_title_image);
            holder.mDescription = (TextView) convertView.findViewById(R.id.fragment_check_point_card_description_text);
            holder.mCheckIn = (ImageButton) convertView.findViewById(R.id.fragment_check_point_check_in_button);
            holder.mSharing = (ImageButton) convertView.findViewById(R.id.fragment_check_point_card_share_button);

            // set holder on convert view
            convertView.setTag(R.id.fragment_check_point_card_view, holder);
        }
        else {
            holder = (CheckPointViewHolder) convertView.getTag(R.id.fragment_check_point_card_view);
        }
        // get checkpoint and set view data
        CheckPoint checkPoint = mCheckPoints.get(position);

        holder.mTitle.setText(checkPoint.getTitle());
        Picasso.with(getContext())
                .load(checkPoint.getImageSrc())
                .into(holder.mImage);
        holder.mDescription.setText(checkPoint.getDescription());

        // set tags and listeners for clicks
        holder.mCheckIn.setTag(R.id.fragment_check_point_card_list, checkPoint);
        holder.mSharing.setTag(R.id.fragment_check_point_card_list, checkPoint);
        // todo add camera button

        holder.mCheckIn.setOnClickListener(this);
        holder.mSharing.setOnClickListener(this);
        // todo add camera button

        return convertView;
    }

    /**
     * Set listener to notify when View is clicked
     *
     * @param listener Listener to notify of view click
     */
    public void setOnCheckPointClickListener(OnCheckPointClickListener listener) {
        mListener = listener;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param view The view that was clicked.
     */
    @Override
    public void onClick(View view) {

        Log.i("CheckPointAdapter", "button clicked");

        if(mListener != null) {
            try {
                // todo notify listener that view was clicked
                Event event = (Event) view.getTag(R.id.fragment_check_point_card_list);
                mListener.onCheckPointClick(view, event);

            } catch (NullPointerException e) {
                Log.e(TAG, e.getClass() + " Unable to notify listener");
            }
        }
    }


    /**
     * Interface that will be called to notify the class
     * that contains the EventAdapter that check point has
     * been clicked on
     */
    interface OnCheckPointClickListener {

        /**
         * Notify listener that check point has been clicked
         *
         * @param view The view which has been clicked
         * @param event The Event
         */
        void onCheckPointClick(View view, Event event);
    }
}
