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
        return String.format("%d %s %s %s %s %s %s %b %s", mID, mTitle, mDescription, mImageSrc, mCheckInType, mTimeCreated, mTimeUpdated, mRedeemed, Arrays.toString(mCheckPoints));
    }
}
