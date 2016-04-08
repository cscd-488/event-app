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

    public void setID(int mID) {
        this.mID = mID;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setImageSrc(String mImageSrc) {
        this.mImageSrc = mImageSrc;
    }

    public Event() {

        int defaultID = 1;

        mID = defaultID;
        mTitle = "default title";
        mShortTitle = generateShortTitle("DT");
        mAuthor = "default author";
        mDescription = "default description";
        mImageSrc = "http://jessicauelmen.com/wp-content/uploads/2016/02/android-logo-featured.jpeg";
        mLat = 0.0;
        mLon = 0.0;
        mQR = "default qr";
        mTimeCreated = "2016-01-01 01:01:01";
        mTimeUpdated = "2016-01-01 01:01:01";

        mRedeemed = false;

        mSubscribed = false;

        mCheckPoints = new CheckPoint[] {new CheckPoint(defaultID)};
    }

    /**
     * Builder class for Event class.
     */
    public class Builder {

        public Event.Builder setID(int id) {
            mID = id;

            return this;
        }

        public Event.Builder setTitle(String title) {
            mTitle = title;

            return this;
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
