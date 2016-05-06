package com.example.jharshman.event;

/**
 * @file CheckPoint.java
 * @author Josh Harshman
 * @author Aaron Young
 * @author Bruce Emehiser
 * @date 2016 01 13
 * @date 2016 02 23
 * @date 2016 03 02
 * @date 2016 05 05
 *
 * Check points for events
 */

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CheckPoint implements Comparable<CheckPoint>, Serializable {

    private static final String TAG = "CheckPoint";

    /**
     * Event Attributes
     */
    @SerializedName("id")
    private int mCheckPointID;
    @SerializedName("event_id")
    private int mEventID;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("artist")
    private String mArtist;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image_src")
    private String mImageSrc;
    // todo lat and lon from String to double
    @SerializedName("lat")
    private String lat;
    @SerializedName("lon")
    private String lon;
    private double mLat;
    private double mLon;
    @SerializedName("qr")
    private String mQR;
    @SerializedName("created_at")
    private String mTimeCreated;
    @SerializedName("updated_at")
    private String mTimeUpdated;
    @SerializedName("status")
    private int mChecked;
    private boolean mWasDisplayed;

    private CheckPoint(int id, int eventID, String title, String artist, String description, String imageSrc, double lat, double lon, String qr, String timeCreated, String timeUpdated, int checked) {

        mCheckPointID = id;
        mEventID = eventID;
        mTitle = title;
        mArtist = artist;
        mDescription = description;
        mImageSrc = imageSrc;
        mLat = lat;
        mLon = lon;
        mQR = qr;
        mTimeCreated = timeCreated;
        mTimeUpdated = timeUpdated;
        mChecked = checked;
    }

    /**
     * Builder class for CheckPoint class.
     */
    public static class Builder {

        private int mID;
        private int mEventID;
        private String mTitle;
        private String mArtist;
        private String mDescription;
        private String mImageSrc;
        private double mLat;
        private double mLon;
        private String mQR;
        private String mTimeCreated;
        private String mTimeUpdated;
        private int mChecked;

        public CheckPoint.Builder setID(int id) {
            mID = id;
            return this;
        }

        public CheckPoint.Builder setEventID(int event_id) {
            mEventID = event_id;
            return this;
        }

        public CheckPoint.Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public CheckPoint.Builder setArtist(String artist) {
            mArtist = artist;
            return this;
        }

        public CheckPoint.Builder setDescription(String description) {
            mDescription = description;
            return this;
        }

        public CheckPoint.Builder setImageSrc(String imageSrc) {
            mImageSrc = imageSrc;
            return this;
        }

        public CheckPoint.Builder setLat(double lat) {
            mLat = lat;
            return this;
        }

        public CheckPoint.Builder setLon(double lon) {
            mLon = lon;
            return this;
        }

        public CheckPoint.Builder setQR(String qr) {
            mQR = qr;
            return this;
        }

        public CheckPoint.Builder setTimeCreated(String timeCreated) {
            mTimeCreated = timeCreated;
            return this;
        }

        public CheckPoint.Builder setTimeUpdated(String timeUpdated) {
            mTimeUpdated = timeUpdated;
            return this;
        }

        public CheckPoint.Builder setChecked(int checked) {
            mChecked = checked;
            return this;
        }

        public CheckPoint build() {

            // fix any null parameters
            if(mTitle == null) {
                mTitle = "";
            }
            if(mArtist == null) {
                mArtist = "";
            }
            if(mDescription == null) {
                mDescription = "";
            }
            if(mImageSrc == null) {
                mImageSrc = "";
            }
            if(mQR == null) {
                mQR = "";
            }
            if(mTimeCreated == null) {
                mTimeCreated = "";
            }
            if(mTimeUpdated == null) {
                mTimeUpdated = "";
            }

            // instantiate new event
            return new CheckPoint(mID,
                    mEventID,
                    mTitle,
                    mArtist,
                    mDescription,
                    mImageSrc,
                    mLat,
                    mLon,
                    mQR,
                    mTimeCreated,
                    mTimeUpdated,
                    mChecked);
        }
    }

    public CheckPoint(int mEventID) {

        mCheckPointID = 1;
        mEventID = 1;
        mTitle = "default title";
        mArtist = "default artist";
        mDescription = "default description";
        mImageSrc = "http://jessicauelmen.com/wp-content/uploads/2016/02/android-logo-featured.jpeg";
        mLat = 0.0;
        mLon = 0.0;
        mQR = "default qr";
        mTimeCreated = "2016-01-01 01:01:01";
        mTimeUpdated = "2016-01-01 01:01:01";
        mChecked = 0;
    }

    public void setDisplayed() {
        mWasDisplayed = true;
    }

    public int getID() {
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

    public void setChecked(int checked) {
        mChecked = checked;
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

    public int getChecked() {
        return mChecked;
    }

    public double[] getCoordinates() {

        return new double[] {getLat(), getLon()};
    }

    public boolean wasDisplayed() {
        return mWasDisplayed;
    }

    @Override
    public int compareTo(@NonNull CheckPoint that) {

        // compare based on ID
        return mCheckPointID - that.getID();
    }

    @Override
    public String toString() {
        return String.format("%d %d %s %s %s %s %f %f %s %s %s %b %b", mCheckPointID, mEventID, mTitle, mArtist, mDescription, mImageSrc, mLat, mLon, mQR, mTimeCreated, mTimeUpdated, mChecked, mWasDisplayed);
    }
}
