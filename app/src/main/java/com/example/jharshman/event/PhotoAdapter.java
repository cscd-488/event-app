package com.example.jharshman.event;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Created by dmacy on 5/6/2016.
 */
public class PhotoAdapter implements ListAdapter {
    private static final String[] PHOTO_TYPE = new String[] { "Default Image" ,"Gallery", "Camera"};
    private static final int[] ICON_ID = new int[] {R.drawable.photo, R.drawable.picture,
            R.drawable.camera};

    private Activity mActivity;

    public PhotoAdapter(Activity activity){
        mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.share_row, null);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.shareImage);
        imageView.setImageResource(ICON_ID[position]);

        TextView textView = (TextView) convertView.findViewById(R.id.shareText);
        textView.setText(PHOTO_TYPE[position]);
        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {}

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {}

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return PHOTO_TYPE[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
