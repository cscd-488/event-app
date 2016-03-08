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
    private int mCheckPointID;
    @SerializedName("event_id")
    private int mEventID;
    @SerializedName("title")
    private String mTitle;
    // todo add Artist from database
    private String mArtist;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image_src")
    private String mImageSrc;
    @SerializedName("coordinates")
    private String mCoordinates;
    // todo switch from coordinates to lat and lon
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

    /**
     * Build new instance of a CheckPoint
     */
    public class Builder {

        public CheckPoint build() {
            //todo finish builder
            throw new NullPointerException("Method build() not yet implimented");
        }

        public void setID(int id) {
            mCheckPointID = id;
        }

        public void setEventID(int eventID) {
            mEventID = eventID;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        public void setDescription(String description) {
            mDescription = description;
        }

        public void setImageSrc(String imageSrc) {
            mImageSrc = imageSrc;
        }

        public void setCoordinates(String coordinates) {
            mCoordinates = coordinates;
        }

        public void setLat(double lat) {
            mLat = lat;
        }

        public void setLon(double lon) {
            mLon = lon;
        }

        public void setQR(String qr) {
            mQR = qr;
        }

        public void setTimeCreated(String timeCreated) {
            mTimeCreated = timeCreated;
        }

        public void setTimeUpdated(String timeUpdated) {
            mTimeUpdated = timeUpdated;
        }

        public void setCollected(boolean collected) {
            mCollected = collected;
        }

        public void setWasDisplayed(boolean displayed) {
            mWasDisplayed = displayed;
        }
    }

    public CheckPoint(int id, int eventID, String title, String description, String image, double lat, double lon, String timeCreated, String timeUpdated, boolean collected) {
        mCheckPointID = id;
        mEventID = eventID;
        mTitle = title;
        mArtist = ""; // todo
        mDescription = description;
        mImageSrc = image;
        mLat = lat;
        mLon = lon;
        mQR = "";
        mTimeCreated = timeCreated;
        mTimeUpdated = timeUpdated;
        mCollected = collected;
    }

    public void setDisplayed() {
        mWasDisplayed = true;
    }

    public int getCheckPointID() {
        return mCheckPointID;
    }

    public int getEventID() {
        return mEventID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getArtist() {
        return mArtist;
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
        return mCheckPointID - that.getCheckPointID();
    }

    @Override
    public String toString() {
        return String.format("%d %d %s %s %s %f %f %s %s %s %b %b", mCheckPointID, mEventID, mTitle, mDescription, mImageSrc, mLat, mLon, mQR, mTimeCreated, mTimeUpdated, mCollected, mWasDisplayed);
    }
}
