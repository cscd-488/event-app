/**
 * @file EventAdapter.java
 * @author Bruce Emehiser
 * @date 2016 02 16
 *
 * Array Adapter for Collections
 */

package com.example.jharshman.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

class EventAdapter extends ArrayAdapter<Event> {

    private static final String TAG = "EventAdapter";

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
        // todo set progress bar based on position

        return convertView;
    }

    /**
     * Notify user of which item was clicked
     *
     * todo make users be able to click add/delete floating action button
     */
    public interface OnItemClickListener {

        /**
         * Notify listener that an event was clicked
         * and pass it the clicked event
         *
         * @param view The view which was clicked
         * @param clicked The event which was clicked
         */
        void onItemClicked(View view, Event clicked);
    }
}
