/**
 * @file CheckPointListAdapter.java
 * @author Bruce Emehiser
 * @date 2016 02 23
 * @date 2016 05 12
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
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Card Adapter which holds list of Card cards
 */
public class CheckPointListAdapter extends ArrayAdapter<CheckPoint> {

    private static final String TAG = "CheckPointListAdapter";

    /**
     * List of card objects currently being displayed in list view
     */
    List<CheckPoint> mCheckPoints;

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param checkPoints  The objects to represent in the ListView.
     */
    public CheckPointListAdapter(Context context, int resource, List<CheckPoint> checkPoints) {
        super(context, resource, checkPoints);

        mCheckPoints = checkPoints;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final CheckPointListViewHolder holder;
        if(convertView == null) {
            // inflate layout for card view
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_check_point_card, parent, false);

            // create new view holder for current check point
            holder = new CheckPointListViewHolder();

            // set views in view holder
            holder.mTitle = (TextView) convertView.findViewById(R.id.check_point_card_title);
            holder.mAuthor = (TextView) convertView.findViewById(R.id.check_point_card_author);
            holder.mImage = (ImageView) convertView.findViewById(R.id.check_point_card_image);
            holder.mTime = (TextView) convertView.findViewById(R.id.check_point_card_time_text);
            holder.mDistance = (TextView) convertView.findViewById(R.id.check_point_card_distance_text);

            // set holder on convert view
            convertView.setTag(R.id.check_point_card, holder);
        }
        else {
            holder = (CheckPointListViewHolder) convertView.getTag(R.id.check_point_card);
        }
        // get checkpoint and set view data
        CheckPoint checkPoint = mCheckPoints.get(position);

        holder.mTitle.setText(checkPoint.getTitle());
        holder.mAuthor.setText(checkPoint.getArtist());
        Picasso.with(getContext())
                .load(checkPoint.getImageSrc())
                .into(holder.mImage);
        holder.mTime.setText(R.string.ui_calculating);
        holder.mDistance.setText(R.string.ui_calculating);
        try {
            LocationMapFragment.timeToLocation(checkPoint.getID(), getContext(), new LocationMapFragment.TimedDistanceCallbackListener() {
                @Override
                public void onMapTimedDistance(String time) {
                    Log.i(TAG, "Time to location callback " + time);
                    holder.mTime.setText(time);
                }
            });
            LocationMapFragment.distanceFromUser(checkPoint.getID(), getContext(), new LocationMapFragment.MeasuredDistanceCallbackListener() {
                @Override
                public void onMapMeasuredDistance(String distance) {
                    Log.i(TAG, "Measurement Distance from user callback " + distance);
                    holder.mDistance.setText(distance);
                }
            }, LocationMapFragment.MeasuredDistanceCallbackListener.Measurement.MILES);

        } catch (NullPointerException e) {
            Log.e(TAG, "Error getting time or distance from LocationMapFragment static methods.");
        }

        return convertView;
    }
}