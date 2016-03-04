package com.example.jharshman.event;

/**
 * @file CheckPoint.java
 * @author Josh Harshman
 * @author Aaron Young
 * @author Bruce Emehiser
 * @date 2016 01 13
 * @date 2016 02 23
 * @date 2016 03 02
 *
 * Check points for events
 */


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckPoint implements Comparable<CheckPoint>, Serializable {

    /**
     * Event Attributes
     */
    @SerializedName("id")
    private int mID;
    @SerializedName("event_id")
    private int mEventID;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image_src")
    private String mImageSrc;
    @SerializedName("coordinates")
    private String mCoordinates;
    private double mLat;
    private double mLon;
    // todo add QR functionality
    private String mQR;
    @SerializedName("created_at")
    private String mTimeCreated;
    @SerializedName("updated_at")
    private String mTimeUpdated;

    private boolean mCollected;
    private boolean mWasDisplayed;

    public CheckPoint(int id, int eventID, String title, String description, String image, double lat, double lon, String timeCreated, String timeUpdated, boolean collected) {
        mID = id;
        mEventID = eventID;
        mTitle = title;
        mDescription = description;
        mImageSrc = image;
        mLat = lat;
        mLon = lon;
        mQR = "";
        mTimeCreated = timeCreated;
        mTimeUpdated = timeUpdated;
        mCollected = collected;
    }

    public CheckPoint(int id, int eventID, String title, String description, String image, String qr, String timeCreated, String timeUpdated, boolean collected) {
        mID = id;
        mEventID = eventID;
        mTitle = title;
        mDescription = description;
        mImageSrc = image;
        mLat = -1;
        mLon = -1;
        mQR = qr;
        mTimeCreated = timeCreated;
        mTimeUpdated = timeUpdated;
        mCollected = collected;
    }

    public void setDisplayed() {
        mWasDisplayed = true;
    }

    public int getID() {
        return mID;
    }

    public int getEventID() {
        return mEventID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setCollected(boolean collected) {
        mCollected = collected;
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

    public String getQR() {
        return mQR;
    }

    public String getTimeCreated() {
        return mTimeCreated;
    }

    public String getTimeUpdated() {
        return mTimeUpdated;
    }

    public boolean getCollected() {
        return mCollected;
    }

    public double[] getCoordinates() {
        return new double[] {getLat(),getLon()};
    }

    public boolean wasDisplayed() {
        return mWasDisplayed;
    }

    @Override
    public int compareTo(@NonNull CheckPoint that) {

        // compare based on ID
        return mID - that.getID();
    }

    @Override
    public String toString() {
        return String.format("%d %d %s %s %s %f %f %s %s %s %b %b", mID, mEventID, mTitle, mDescription, mImageSrc, mLat, mLon, mQR, mTimeCreated, mTimeUpdated, mCollected, mWasDisplayed);
    }
}
