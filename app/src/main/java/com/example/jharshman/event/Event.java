/**
 * @file Event.java
 * @author Bruce Emehiser
 * @date 2016 02 23
 *
 * This is a wrapper class for event data
 * used in the custom Event Array Adapter
 */

package com.example.jharshman.event;

import java.io.Serializable;

public class Event implements Serializable {

    int mID;
    String mTitle;
    String mDescription;
    int mPosition;
}
