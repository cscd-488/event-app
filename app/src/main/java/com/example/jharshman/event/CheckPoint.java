package com.example.jharshman.event;

/**
 * Created by jharshman on 1/13/16.
 */
public class CheckPoint {

    /**
     * Event Attributes
     * */
    private String mTitle;
    private String mDescription;
    private String mImageSrc;
    private double[] mCoordinates; //todo: implement getter / setter for longitude and latitude

    public String getmTitle() {
        return mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmImageSrc() {
        return mImageSrc;
    }

    public double[] getmCoordinates() {
        return mCoordinates;
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

    public void setmCoordinates(double latitude, double longitude) {
        this.mCoordinates[0] = latitude;
        this.mCoordinates[1] = longitude;
    }
}
