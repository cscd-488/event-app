package com.example.jharshman.event;

/**
 * @file CheckPoint.java
 * @author Josh Harshman
 * @author Aaron Young
 * @author Bruce Emehiser
 * @date 2016 01 13
 * @date 2016 02 23
 *
 * Check points for events
 */


import android.support.annotation.NonNull;
import java.io.Serializable;

public class CheckPoint implements Comparable<CheckPoint>, Serializable {

    /**
     * Event Attributes
     */
    private int mID;
    private String mTitle;
    private String mDescription;
    private String mImageSrc;
    private double mLat;
    private double mLon;
    private boolean mDisplayed;

    public CheckPoint(int id, String title, String description, String image, double lat, double lon) {
        mID = id;
        mTitle = title;
        mDescription = description;
        mImageSrc = image;
        mLat = lat;
        mLon = lon;
        this.mDisplayed = false;
    }


    public void setmDisplayed(){this.mDisplayed = true;}
    public boolean wasDisplayed(){return this.mDisplayed;}

    public int getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getImageSrc() {
        return mImageSrc;
    }

    public double getLat() {
        return mLat;
    }

    public double getLon() {
        return mLon;
    }

    public double[] getCoordinates() {
        return new double[] {getLat(),getLon()};
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    @Override
    public int compareTo(@NonNull CheckPoint that) {

        // compare based on ID
        return mID - that.getID();
    }
}
