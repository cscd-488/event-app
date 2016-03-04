/**
 * @file EventAdapter.java
 * @author Bruce Emehiser
 * @date 2016 02 16
 * @date 2016 03 03
 *
 * Array Adapter for Collections
 */

package com.example.jharshman.event;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class EventAdapter extends ArrayAdapter<Event> implements View.OnClickListener {

    private static final String TAG = "EventAdapter";

    /**
     * Listener for click events
     */
    OnEventClickListener mListener;

    /**
     * List of events
     */
    List<Event> mEvents;

    public EventAdapter(Context context, int resource, List<Event> events) {
        super(context, resource, events);

        mEvents = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        EventViewHolder holder;
        if(convertView == null) {
            // create a new view

            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_event_card, parent, false);

            holder = new EventViewHolder();

            holder.mTitle = (TextView) convertView.findViewById(R.id.fragment_event_card_title);
            holder.mDescription = (TextView) convertView.findViewById(R.id.fragment_event_card_short_description);
            holder.mAddDeleteButton = (FloatingActionButton) convertView.findViewById(R.id.fragment_collections_add_delete_button);
            // todo get progress bar

            // set holder on view for later use
            convertView.setTag(holder);
        }
        else {
            // set the view based on the resources in the holder
            holder = (EventViewHolder) convertView.getTag();
        }

        holder.mTitle.setText(mEvents.get(position).getTitle());
        holder.mDescription.setText(mEvents.get(position).getDescription());

        // set event id as tag on add/delete button so we can get the ID when notifying of a click
        holder.mAddDeleteButton.setTag(mEvents.get(position).getID());
        holder.mAddDeleteButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        holder.mAddDeleteButton.setOnClickListener(this);

        // todo set progress bar based on position

        return convertView;
    }

    /**
     * Set listener to notify when View is clicked
     *
     * @param listener Listener to notify of view click
     */
    public void setOnEventClickListener(OnEventClickListener listener) {
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
                int id = (int) view.getTag();
                mListener.onEventClick(view, id);

            } catch (NullPointerException e) {
                Log.e(TAG, e.getClass() + " Unable to notify listener");
            }
        }
    }

    /**
     * Interface that will be called to notify the class
     * that contains the EventAdapter that an event has
     * been clicked on
     */
    interface OnEventClickListener {

        /**
         * Notify listener that event has been clicked
         *
         * @param view The view which has been clicked
         * @param eventId The id of the event for the view that has been clicked
         */
        void onEventClick(View view, int eventId);
    }
}
