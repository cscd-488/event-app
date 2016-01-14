package com.example.jharshman.event;

import java.util.ArrayList;

/**
 * Created by jharshman on 1/13/16.
 */
public class Event {

    /**
     * Event Attributes
     * */
    private Boolean GPS = false;
    private String mTitle;
    private String mDescription;
    private String mImageSrc;
    private ArrayList<CheckPoint> mCheckPoints = new ArrayList<CheckPoint>();

    public Boolean getGPS() {
        return GPS;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmImageSrc() {
        return mImageSrc;
    }

    public ArrayList<CheckPoint> getmCheckPoints() {
        return mCheckPoints;
    }

    public void setGPS(Boolean GPS) {
        this.GPS = GPS;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setmImageSrc(String mImageSrc) {
        this.mImageSrc = mImageSrc;
    }

}
