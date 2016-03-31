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
    // todo add Short Title from database
    private String mShortTitle;
    // todo add Author from Database
    private String mAuthor;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image_src")
    private String mImageSrc;
    @SerializedName("check_in_type")
    private String mCheckInType;
    @SerializedName("created_at")
    private String mTimeCreated;
    @SerializedName("updated_at")
    private String mTimeUpdated;

    private boolean mRedeemed;
    private boolean mSubscribed;

    @SerializedName("locations")
    private CheckPoint[] mCheckPoints;

    public Event(int id, String title, String description, String imageSrc, String checkInType,
                 String timeCreated, String timeUpdated, boolean redeemed, CheckPoint[] checkPoints) {

        mID = id;
        mTitle = title;
        mShortTitle = generateShortTitle(title);
        mDescription = description;
        mImageSrc = imageSrc;
        mCheckInType = checkInType;
        mTimeCreated = timeCreated;
        mTimeUpdated = timeUpdated;
        mRedeemed = redeemed;
        mCheckPoints = checkPoints;
        mSubscribed = false;
    }

    public void setTimeUpdated(String timeUpdated) {
        mTimeUpdated = timeUpdated;
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

    public String getDescription() {
        return mDescription;
    }

    public String getImageSrc() {
        return mImageSrc;
    }

    public String getCheckInType() {
        return mCheckInType;
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
        return String.format("%d %s %s %s %s %s %s %s %b %s", mID, mTitle, mShortTitle, mDescription, mImageSrc, mCheckInType, mTimeCreated, mTimeUpdated, mRedeemed, Arrays.toString(mCheckPoints));
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
