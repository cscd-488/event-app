package com.example.jharshman.event;

/**
 * Created by jharshman on 1/13/16.
 *
 * Modified by Aaron Young on 2/4/16
 */
public class CheckPoint implements Comparable<CheckPoint>{

    /**
     * Event Attributes
     * */
    private String mTitle;
    private String mDescription;
    private String mImageSrc;
    private double[] mCoordinates; //todo: implement getter / setter for longitude and latitude
    private boolean mDisplayed;

    public CheckPoint(String title, String description, String image, double[] coordinates) {
        this.mTitle = title;
        this.mDescription = description;
        this.mImageSrc = image;
        this.mCoordinates = coordinates;
        this.mDisplayed = false;
    }

    public void setmDisplayed(){this.mDisplayed = true;}
    public boolean wasDisplayed(){return this.mDisplayed;}

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

    @Override
    public int compareTo(CheckPoint another) {
        if(this.getmCoordinates()[0] == another.getmCoordinates()[0] && this.getmCoordinates()[1] == another.getmCoordinates()[1])
            return 0;
        return -1;
    }
}
