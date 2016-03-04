/**
 * @file EventViewHolder.java
 * @author Bruce Emehiser
 * @date 2016 02 16
 * @date 2016 03 03
 *
 * View Holder
 */

package com.example.jharshman.event;

import android.support.design.widget.FloatingActionButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;

public class EventViewHolder implements Serializable {

    TextView mTitle;
    TextView mDescription;
    FloatingActionButton mAddDeleteButton;
    ProgressBar mProgressBar;
    int mProgress;
}
