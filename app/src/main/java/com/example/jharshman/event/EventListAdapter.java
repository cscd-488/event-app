package com.example.jharshman.event;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jharshman on 1/16/16.
 */
public class EventListAdapter extends ArrayAdapter<Event> {

    /**
     * EventListAdapter Attributes
     * */
    private Activity mParentActivity;
    private ArrayList<Event> mEvents;

    /**
     * EventListAdapter Constructor
     * Calls super class constructor for ArrayAdapter.
     * Sets the mParentActivity and mEvents variables
     *
     * @PARAM   Activity of parent
     * @PARAM   Array of event objects
     * */
    public EventListAdapter(Activity parentActivity, ArrayList<Event> events) {
        super(parentActivity.getApplicationContext(), R.layout.list_row, events);
        this.mParentActivity = parentActivity;
        this.mEvents = events;
    }

    /**
     * Implementation of getView(int, View, ViewGroup) from ArrayAdapter<>.
     * Inflates view for row item and sets row item attributes.
     *
     * @PARAM   Position within the array
     * @PARAM   View for row element
     * @PARAM   ViewGroup for parent
     * @RETURN  Inflated View for row element
     * */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // inflate if view is null
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)mParentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_row, null);
        }

        // find widget views for row element
        TextView title = (TextView)convertView.findViewById(R.id.title);
        TextView descr = (TextView)convertView.findViewById(R.id.description);
        ImageView pict = (ImageView)convertView.findViewById(R.id.thumbnail);

        // set widget views for row element
        title.setText(mEvents.get(position).getmTitle());
        descr.setText(mEvents.get(position).getmDescription());
        pict.setImageResource(R.drawable.spokane);

        return convertView;
    }
}
