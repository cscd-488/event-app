/**
 * @file EventViewHolder.java
 * @author Bruce Emehiser
 * @date 2016 02 16
 *
 * View Holder
 */

package com.example.jharshman.event;

import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;

public class EventViewHolder implements Serializable {

    TextView mTitle;
    TextView mDescription;
    ProgressBar mProgressBar;
    int mProgress;
}
