package com.example.jharshman.event;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jharshman on 1/13/16.
 */
public class Event {

    private class AsyncNetworkOperation extends AsyncTask<URL, Integer, String> {
        @Override
        protected String doInBackground(URL... urls) {
            String json = null;
            BufferedReader bufferedReader = null;
            try {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(urls[0].openStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine())!=null) {
                        stringBuilder.append(line+"\n");
                    }
                    json = stringBuilder.toString();
                } finally {
                    if(bufferedReader != null)
                        bufferedReader.close();
                }
            } catch(IOException io) {
                io.printStackTrace();
            }
            return json;
        }
    }

    /**
     * Event Attributes
     * */
    private Boolean GPS = false;
    private String mTitle;
    private String mDescription;
    private String mImageSrc;
    private ArrayList<CheckPoint> mCheckPoints = new ArrayList<CheckPoint>();

    public Event(URL url) throws InterruptedException, ExecutionException {

        String json = new AsyncNetworkOperation().execute(url).get();
        Log.d("DEBUG", ""+json);
        try {
            JSONObject jsonObject = new JSONObject(json);

            // get event data
            JSONObject subObject = jsonObject.getJSONObject("event");
            setGPS((subObject.getString("GPS") == "on"));
            setmTitle(subObject.getString("title"));
            setmDescription(subObject.getString("description"));
            setmImageSrc(subObject.getString("image-src"));

            // pull location data

        } catch(JSONException jsonex) {
            jsonex.printStackTrace();
        }
    }

    public Event(String title, String description, String imageSrc) {
        this.mTitle = title;
        this.mDescription = description;
        this.mImageSrc = imageSrc;
    }

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
