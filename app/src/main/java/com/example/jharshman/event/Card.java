
/**
 * @file Card.java
 * @author Bruce Emehiser
 * @date 2016 02 07
 *
 * Used to encapsulate all information concerning
 * event waypoints.
 */

package com.example.jharshman.event;

/**
 * Card
 */
public class Card {

    /**
     * Build waypoint object
     * This allows simple setup of a waypoint, and locks out
     * future editing via sets
     */
    public class Builder {

        public Builder() {
            // empty public constructor
        }

        public void setTitleText(String titleText) {
            mTitleText = titleText;
        }

        public void setTitleImage(String titleImage) {
            mTitleImage = titleImage;
        }

        public void setDescription(String description) {
            mDescription = description;
        }

        public void setBodyText(String bodyText) {
            mBodyText = bodyText;
        }

        public void setShare(String shareUrl) {
            mShare = shareUrl;
        }

        public void setCheckIn(String checkIn) {
            mCheckIn = checkIn;
        }

        public void setCollected(boolean collected) {
            mCollected = collected;
        }

        public Card build() {

            return new Card(mTitleText, mTitleImage, mDescription, mBodyText, mShare, mCheckIn, mCollected);
        }

    }

    private String mTitleText; // short title
    private String mTitleImage; // uri/url todo figure out title loading/storage
    private String mDescription; // long title
    private String mBodyText; // long description
    private String mShare; // url todo figure out share
    private String mCheckIn; // url todo figure out the check in string id
    private boolean mCollected; // check in has been collected

    /**
     * Constructor
     * Initializes values to "" if null
     *
     * @param titleText Title
     * @param titleImage Image
     * @param description Description
     * @param bodyText Body
     * @param share Share
     * @param checkIn Check in
     * @param collected Collected
     */
    public Card(String titleText, String titleImage, String description, String bodyText, String share, String checkIn, boolean collected) {

        mTitleText = titleText;
        mTitleImage = titleImage;
        mDescription = description;
        mBodyText = bodyText;
        mShare = share;
        mCheckIn = checkIn;
        mCollected = collected;

        if(mTitleText == null) {
            mTitleText = "";
        }
        if(mTitleImage == null) {
            mTitleImage = "";
        }
        if(mDescription == null) {
            mDescription = "";
        }
        if(mBodyText == null) {
            mBodyText = "";
        }
        if(mShare == null) {
            mShare = "";
        }
        if(mCheckIn == null) {
            mCheckIn = "";
        }
    }

    public String getTitleText() {
        return mTitleText;
    }

    public String getTitleImage() {
        return mTitleImage;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getBodyText() {
        return mBodyText;
    }

    public String getShare() {
        return mShare;
    }

    public String getCheckIn() {
        return mCheckIn;
    }

    public boolean isCollected() {
        return mCollected;
    }

    public void setCollected(boolean collected) {
        mCollected = collected;
    }
}

