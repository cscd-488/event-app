/**
 * @file Event.java
 * @author Bruce Emehiser
 * @date 2016 02 23
 * @date 2016 03 02
 *
 * This is a wrapper class for event data
 * used in the custom Event Array Adapter
 */

package com.example.jharshman.event;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Event implements Serializable {

    @SerializedName("id")
    private int mID;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("short_title")
    private String mShortTitle;
    @SerializedName("author")
    private String mAuthor;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image_src")
    private String mImageSrc;
    @SerializedName("lat")
    private double mLat;
    @SerializedName("lon")
    private double mLon;
    @SerializedName("qr")
    private String mQR;
    @SerializedName("created_at")
    private String mTimeCreated;
    @SerializedName("updated_at")
    private String mTimeUpdated;

    private boolean mRedeemed;
    private boolean mSubscribed;

    @SerializedName("checkpoints")
    private CheckPoint[] mCheckPoints;

    private Event(int id,
                  String title,
                  String shortTitle,
                  String author,
                  String description,
                  String imageSrc,
                  double lat,
                  double lon,
                  String qr,
                  String timeCreated,
                  String timeUpdated,
                  boolean subscribed,
                  boolean redeemed,
                  CheckPoint[] checkPoints) {

        // check parameters
        if(title == null
                || shortTitle == null
                || author == null
                || description == null
                || imageSrc == null
                || qr == null
                || timeCreated == null
                || timeUpdated == null
                || checkPoints == null) {
            throw new NullPointerException("Event parameters cannot be null.");
        }

        mID = id;
        mTitle = title;
        mShortTitle = shortTitle;
        mAuthor = author;
        mDescription = description;
        mImageSrc = imageSrc;
        mLat = lat;
        mLon = lon;
        mQR = qr;
        mTimeCreated = timeCreated;
        mTimeUpdated = timeUpdated;

        mSubscribed = subscribed;
        mRedeemed = redeemed;

        // todo remove checkpoints from events, and only get them with a database call
        mCheckPoints = checkPoints;
    }

// public Event() {
//
//        int defaultID = 1;
//
//        mID = defaultID;
//        mTitle = "default title";
//        mShortTitle = generateShortTitle("DT");
//        mAuthor = "default author";
//        mDescription = "default description";
//        mImageSrc = "http://jessicauelmen.com/wp-content/uploads/2016/02/android-logo-featured.jpeg";
//        mLat = 0.0;
//        mLon = 0.0;
//        mQR = "default qr";
//        mTimeCreated = "2016-01-01 01:01:01";
//        mTimeUpdated = "2016-01-01 01:01:01";
//
//        mRedeemed = false;
//
//        mSubscribed = false;
//
//        mCheckPoints = new CheckPoint[] {new CheckPoint(defaultID)};
//    }

    /**
     * Builder class for Event class.
     */
    public static class Builder {

        private int mID;
        private String mTitle;
        private String mShortTitle;
        private String mAuthor;
        private String mDescription;
        private String mImageSrc;
        private double mLat;
        private double mLon;
        private String mQR;
        private String mTimeCreated;
        private String mTimeUpdated;
        private boolean mSubscribed;
        private boolean mRedeemed;
        private CheckPoint[] mCheckPoints;

        public Event.Builder setID(int id) {
            mID = id;
            return this;
        }

        public Event.Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Event.Builder setShortTitle(String shortTitle) {
            mShortTitle = shortTitle;
            return this;
        }

        public Event.Builder setAuthor(String author) {
            mAuthor = author;
            return this;
        }

        public Event.Builder setDescription(String description) {
            mDescription = description;
            return this;
        }

        public Event.Builder setImageSrc(String imageSrc) {
            mImageSrc = imageSrc;
            return this;
        }

        public Event.Builder setLat(double lat) {
            mLat = lat;
            return this;
        }

        public Event.Builder setLon(double lon) {
            mLon = lon;
            return this;
        }

        public Event.Builder setQR(String qr) {
            mQR = qr;
            return this;
        }

        public Event.Builder setTimeCreated(String timeCreated) {
            mTimeCreated = timeCreated;
            return this;
        }

        public Event.Builder setTimeUpdated(String timeUpdated) {
            mTimeUpdated = timeUpdated;
            return this;
        }

        public Event.Builder setSubscribed(boolean subscribed) {
            mSubscribed = subscribed;
            return this;
        }

        public Event.Builder setRedeemed(boolean redeemed) {
            mRedeemed = redeemed;
            return this;
        }

        public Event.Builder setCheckPoints(List<CheckPoint> checkPoints) {
            mCheckPoints = checkPoints.toArray(new CheckPoint[checkPoints.size()]);
            return this;
        }

        public Event.Builder setCheckPoints(CheckPoint[] checkPoints) {
            mCheckPoints = checkPoints;
            return this;
        }

        public Event build() {

            // fix any null parameters
            if(mTitle == null) {
                mTitle = "";
            }
            if(mShortTitle == null) {
                mShortTitle = "";
            }
            if(mAuthor == null) {
                mAuthor = "";
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
            if(mCheckPoints == null) {
                mCheckPoints = new CheckPoint[0];
            }

            // instantiate new event
            return new Event(mID,
                    mTitle,
                    mShortTitle,
                    mAuthor,
                    mDescription,
                    mImageSrc,
                    mLat,
                    mLon,
                    mQR,
                    mTimeCreated,
                    mTimeUpdated,
                    mSubscribed,
                    mRedeemed,
                    mCheckPoints);
        }
    }

    public void setRedeemed(boolean redeemed) {
        mRedeemed = redeemed;
    }

    public void setSubscribed(boolean subscribed) {
        mSubscribed = subscribed;
    }

    public int getID() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getShortTitle() {

        if(mShortTitle == null) {
            mShortTitle = generateShortTitle(mTitle);
        }

        return mShortTitle;
    }

    public String getAuthor() {
        return mAuthor;
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

    public boolean isRedeemed() {
        return mRedeemed;
    }

    public boolean getSubscribed() {
        return mSubscribed;
    }

    public CheckPoint[] getCheckPoints() {

        // todo remove checkpoints from event class, and only get them with a database call
        if(mCheckPoints == null) {
            mCheckPoints = new CheckPoint[0];
        }

        return mCheckPoints;
    }

    @Override
    public String toString() {
        return String.format("%d %s %s %s %s %s %s %s %b %s", mID, mTitle, mShortTitle, mAuthor, mDescription, mImageSrc, mTimeCreated, mTimeUpdated, mRedeemed, Arrays.toString(mCheckPoints));
    }

    /**
     * Generate a short title by adding the first character of
     * each space delimited word to the title string
     * @param title The title string
     * @return The short version of the title string
     */
    private static String generateShortTitle(String title) {

        // check incoming parameters
        if(title == null) {
            return "";
        }

        String shortTitle = "";
        for(String s : title.split(" ")) {
            shortTitle += Character.toUpperCase(s.charAt(0));
        }

        return shortTitle;
    }
}
