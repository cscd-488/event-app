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
import android.content.res.ColorStateList;
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

    /**
     * This tells the adapter to show the edit buttons,
     * or to show the generic buttons for external linking etc.
     */
    boolean mShowEditButtons;

    public EventAdapter(Context context, int resource, List<Event> events) {
        super(context, resource, events);

        mEvents = events;
        mShowEditButtons = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        EventViewHolder holder;
        if(convertView == null) {
            // create a new view

            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.fragment_event_card, parent, false);

            holder = new EventViewHolder();

            holder.mTitle = (TextView) convertView.findViewById(R.id.event_card_title);
            holder.mDescription = (TextView) convertView.findViewById(R.id.event_card_short_description);
            holder.mAddDeleteButton = (FloatingActionButton) convertView.findViewById(R.id.event_card_fab);
            // todo get progress bar

            // set holder on view for later use
            convertView.setTag(R.id.fragment_event_list_view, holder);
        }
        else {
            // set the view based on the resources in the holder
            holder = (EventViewHolder) convertView.getTag(R.id.fragment_event_list_view);
        }

        // get Event and set Views with its values
        Event event = mEvents.get(position);

        holder.mTitle.setText(event.getTitle());
        holder.mDescription.setText(event.getDescription());
        // set event id as tag on add/delete button so we can get the EVENT_TAG_KEY when notifying of a click
        holder.mAddDeleteButton.setTag(R.layout.fragment_event, event);

        // set up floating action button
        setFAB(event, holder);

        holder.mAddDeleteButton.setOnClickListener(this);

        // todo set progress bar based on position

        return convertView;
    }

    /**
     * Set up the floating action button based on the current state
     * of the list view.
     *
     * @param event Event who's data we care about
     * @param holder The holder containing the floating action button
     */
    private void setFAB(Event event, EventViewHolder holder) {

        // if we want to see the add/delete button
        if(mShowEditButtons) {
            if (!event.getSubscribed()) {
                holder.mAddDeleteButton.setImageResource(android.R.drawable.ic_menu_add);
                holder.mAddDeleteButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.green)));
            } else {
                holder.mAddDeleteButton.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                holder.mAddDeleteButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.red)));
            }
        }
        // else we want to see the link button
        else {
            if(false) {
                // todo if we have completed an event, show restart button
            }
            else {
                holder.mAddDeleteButton.setImageResource(android.R.drawable.ic_menu_info_details);
                holder.mAddDeleteButton.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorAccent)));
            }
        }
    }

    /**
     * Show or hide the edit buttons
     *
     * @param show The boolean state true=show, false=hide
     */
    public void setShowEditButtons(boolean show) {
        mShowEditButtons = show;
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

        Log.i("CheckPointListAdapter", "button clicked");

        if(mListener != null) {
            try {
                Event event = (Event) view.getTag(R.layout.fragment_event);
                mListener.onEventClick(view, event);

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
         * @param event The event
         */
        void onEventClick(View view, Event event);
    }
}
