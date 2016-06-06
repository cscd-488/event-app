/**
 * @file Event.java
 * @author Aaron Young
 * @date 2016 03 01
 * @date 2016 04 01
 *
 * Map fragment class to display
 * locations
 */
package com.example.jharshman.event;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @file LocationMapFragment.java
 * @author Aaron Young
 * @date 3/2/2016
 * @date 6/5/2016
 *
 * A fragment class created to hold a map display as well as
 * communicate back various map functionality such as distance
 * and travel time
 */
public class LocationMapFragment extends Fragment implements OnMapReadyCallback {

    private static class DownloadTask extends AsyncTask<String, Void, String> {

        private TimedDistanceCallbackListener mListener;

        public DownloadTask(TimedDistanceCallbackListener listener){
            super();
            this.mListener = listener;
        }

        @Override
        protected String doInBackground(String... params) {
            String data = "";

            try {
                data = downloadUrl(params[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            ParserTask parserTask = new ParserTask(this.mListener);
            parserTask.execute(results);
        }
    }

    private static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        private TimedDistanceCallbackListener mListener;

        public ParserTask(TimedDistanceCallbackListener listener){
            super();
            this.mListener = listener;
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... params) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jsonObject = new JSONObject(params[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> results) {
            List<LatLng> points = null;
            PolylineOptions polylineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "No Path Available";

            try {
                if (results.size() < 1) {
                    mListener.onMapTimedDistance(duration);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            for (int i = 0; i < results.size(); i++) {
                points = new ArrayList<>();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = results.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) {
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(2f);
                polylineOptions.color(Color.RED);
            }
            mListener.onMapTimedDistance(duration);
        }
    }

    private class MarkerWrapper {
        public CheckPoint mLocation;
        public Marker mMarker;

        public MarkerWrapper(CheckPoint location, Marker marker) {
            this.mLocation = location;
            this.mMarker = marker;
        }
    }

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    // the fragment initialization parameters
    private static final String ARG_CHECKPOINT_ID = "arg_checkpoint_id";
    private static final float METERS_TO_FEET = 3.28084f;
    private static final float METERS_TO_KILO = 1000f;
    private static final float FEET_TO_MILES = 5280f;
    private static final int DEFAULT_ZOOM = 17;
    private static final int LENGTH = 16;

    private static Location mLocation = null;

    private MapView mMapView;
    private GoogleMap mMap;
    private int mDisplayIndex = 0;
    private MarkerWrapper[] mMarkers = new MarkerWrapper[0];
    private OnFragmentInteractionListener mListener;
    private CheckPoint[] mCoordinates = new CheckPoint[0];
    private boolean mZoomed = false;

    /**
     * The event id to display checkpoints for.
     */
    private int mEventID;

    private LocationMapFragment.TimedDistanceCallbackListener callbackListener = new LocationMapFragment.TimedDistanceCallbackListener() {
        @Override
        public void onMapTimedDistance(String time) {

            TextView timeView;

            if(getView() != null) {

                timeView = (TextView) getView().findViewById(R.id.timeToTargetTextView);
                timeView.setText(time);
            }
        }
    };

    private GoogleMap.OnMarkerClickListener myMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            for (int i = 0; i < mMarkers.length; i++) {
                if (mMarkers[i].mMarker.equals(marker)) {
                    mDisplayIndex = i;

                    fillText(mMarkers[i]);
                    return false;
                }
            }
            return true;
        }
    };

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            mLocation = location;

            if (getView() == null)
                return;
            if (!mZoomed)
                centerToLocation(mLocation.getLatitude(), mLocation.getLongitude());
            if (mCoordinates == null || mCoordinates.length == 0)
                return;

            CheckPoint hovered = mCoordinates[mDisplayIndex];

            distanceFromUser(hovered.getEventID(), getContext(), measuredCallbackListener, MeasuredDistanceCallbackListener.Measurement.MILES);
            timeToLocation(hovered.getEventID(), getContext(), callbackListener);
        }
    };

    private MeasuredDistanceCallbackListener measuredCallbackListener = new MeasuredDistanceCallbackListener() {
        @Override
        public void onMapMeasuredDistance(String distance) {

            TextView distanceView;

            if(getView() != null) {

                distanceView = (TextView) getView().findViewById(R.id.distanceTextView);
                distanceView.setText(distance);
            }
        }
    };

    private static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try {

            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception downloading", e.toString());
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private static String getDirectionsUrl(LatLng start, LatLng dest) {
        String strOrigin = "origin=" + start.latitude + "," + start.longitude;
        String strDest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=walking";
        String params = strOrigin + "&" + strDest + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;

        return url;
    }

    public LocationMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param checkpointID The id of the checkpoint to load.
     * @return A new instance of fragment LocationMapFragment.
     */
    public static LocationMapFragment newInstance(int checkpointID) {
        LocationMapFragment fragment = new LocationMapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CHECKPOINT_ID, checkpointID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            // get the id of the event
            mEventID = getArguments().getInt(ARG_CHECKPOINT_ID);

            // get the checkpoints for the event
            addLocations(mEventID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_location_map, container, false);

        setHeader();
        this.buildMap(v, savedInstanceState);

        return v;
    }

    private void setHeader() {
        TextView header = (TextView) getActivity().findViewById(R.id.headerTitle);
        header.setText("  Map");
    }

    private void buildMap(View v, Bundle savedInstanceState) {
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(this);
    }

    private static float conversion(float inMeters, MeasuredDistanceCallbackListener.Measurement measurement){
        switch(measurement){
            case FEET:
                inMeters *= METERS_TO_FEET;
                break;
            case MILES:
                inMeters *= METERS_TO_FEET;
                inMeters /= FEET_TO_MILES;
                break;
            case KILOMETERS:
                inMeters /= METERS_TO_KILO;
                break;
        }

        return inMeters;
    }

    /**
     * Returns true if location services are enabled
     * @SuppressWarnings({"MissingPermission"}) may be required if using permission calls
     *
     * @param context
     * @return True if location services enabled
     */
    public static boolean isLocationEnabled(Context context){
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    /**
     *
     * Returns back a boolean within a callback that determines if a user
     * is within a set distance from the provided checkpoint. An error message
     * is included and will be blank if nothing went wrong.
     *
     * @param checkPoint
     * @param context
     * @param distance
     * @param inRange
     * @param measurement
     */
    @SuppressWarnings({"MissingPermission"})
    public static void isUserInRange(int checkPoint, Context context, float distance, UserInRange inRange, MeasuredDistanceCallbackListener.Measurement measurement){
        float[] results = new float[1];
        double[] coords = DataManager.instance(context).getCheckpoint(checkPoint).getCoordinates();
        Location location = mLocation;

        if(location == null){
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (isLocationEnabled(context)) {
                inRange.userInRange(false, "Location Services Disabled");
                return;
            }
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location == null) {
                inRange.userInRange(false, "Location Services Disabled");
                return;
            }
        }

        Location.distanceBetween(location.getLatitude(), location.getLongitude(), coords[0], coords[1], results);

        results[0] = conversion(results[0], measurement);

        inRange.userInRange(results[0] <= distance, "");
    }

    /**
     *
     * Calls the passed callback function when the distance finishes calculating.
     * Passes the distance back in units provided on call.
     *
     * @param checkPoint
     * @param context
     * @param callBack
     * @param returnFormat
     */
    @SuppressWarnings({"MissingPermission"})
    public static void distanceFromUser(int checkPoint, Context context, MeasuredDistanceCallbackListener callBack, MeasuredDistanceCallbackListener.Measurement returnFormat){
        String format = "m";
        double[] coords = DataManager.instance(context).getCheckpoint(checkPoint).getCoordinates();
        float[] results = new float[1];
        Location location = mLocation;

        if(location == null){
            LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (isLocationEnabled(context)) {
                callBack.onMapMeasuredDistance("Location Services Disabled");
                return;
            }
            location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location == null) {
                callBack.onMapMeasuredDistance("No Last Known Location");
                return;
            }
        }

        Location.distanceBetween(location.getLatitude(), location.getLongitude(), coords[0], coords[1], results);

        results[0] = conversion(results[0], returnFormat);

        callBack.onMapMeasuredDistance(String.format("%.2f", results[0]) + " " + format);
    }

    /**
     * Calls the callback function with walking time passed as a string
     *
     * @param checkPoint
     * @param context
     * @param callback
     */
    @SuppressWarnings({"MissingPermission"})
    public static void timeToLocation(int checkPoint, Context context, TimedDistanceCallbackListener callback) {
        double[] location = new double[2];

        if (callback != null) {
            if (mLocation != null) {
                location[0] = mLocation.getLatitude();
                location[1] = mLocation.getLongitude();
            } else {
                LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (isLocationEnabled(context)) {
                    callback.onMapTimedDistance("Location Services Disabled");
                    return;
                }
                Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastLocation != null){
                    location[0] = lastLocation.getLatitude();
                    location[1] = lastLocation.getLongitude();
                }else {
                    callback.onMapTimedDistance("No Last Known Location");
                    return;
                }
            }
            double[] coords = DataManager.instance(context).getCheckpoint(checkPoint).getCoordinates();

            String url = getDirectionsUrl(new LatLng(location[0], location[1]),
                    new LatLng(coords[0], coords[1]));

            DownloadTask downloadTask = new DownloadTask(callback);
            downloadTask.execute(url);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.mMapView.onLowMemory();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * Takes in an event ID and pulls the needed data from the data manager
     *
     * @param eventID
     */
    public void addLocations(int eventID) {
        List<CheckPoint> mCoordinates = DataManager.instance(getContext()).getCheckpoints(eventID);

        if(mCoordinates == null)
            throw new NullPointerException("CoordinateCollection list cannot be null");

        this.mCoordinates = mCoordinates.toArray(this.mCoordinates);
    }

    /**
     * Called internally when the map display is ready
     * Unknown effects when called externally 
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        try {
            int off = Settings.Secure.getInt(getContext().getContentResolver(), Settings.Secure.LOCATION_MODE);
            if (off == 0) {
                Toast.makeText(getContext(), "Please enable GPS", Toast.LENGTH_SHORT).show();
                Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(onGPS);
            }
        }catch(Settings.SettingNotFoundException e) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        this.setupCheckPoints();
        this.populateFunctionality();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }
        this.mMap.setMyLocationEnabled(true);
        this.mMap.setOnMarkerClickListener(this.myMarkerClickListener);
        this.mMap.setOnMyLocationChangeListener(this.myLocationChangeListener);
    }

    private void populateFunctionality(){
        MarkerWrapper mw = null;
        if(this.mMarkers.length != 0)
            mw = this.mMarkers[this.mDisplayIndex];

        ImageButton prevButton = (ImageButton) getActivity().findViewById(R.id.prevButton);
        ImageButton nextButton = (ImageButton) getActivity().findViewById(R.id.nextButton);
        ImageButton centerButton = (ImageButton) getActivity().findViewById(R.id.centerButton);
        ImageButton detailsButton = (ImageButton) getActivity().findViewById(R.id.displayCheckPointButton);
        this.fillText(mw);

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onMapFragmentInteraction(mCoordinates[mDisplayIndex]);
                }
            }
        });

        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMarkers.length == 0)return;

                centerToLocation(mMarkers[mDisplayIndex].mLocation.getCoordinates());
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMarkers.length == 0)return;

                mDisplayIndex--;
                if (mDisplayIndex == -1)
                    mDisplayIndex = mMarkers.length - 1;

                fillText(mMarkers[mDisplayIndex]);
                centerToLocation(mMarkers[mDisplayIndex].mLocation.getCoordinates());
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMarkers.length == 0)return;

                mDisplayIndex++;
                if (mDisplayIndex == mMarkers.length)
                    mDisplayIndex = 0;

                fillText(mMarkers[mDisplayIndex]);
                centerToLocation(mMarkers[mDisplayIndex].mLocation.getCoordinates());
            }
        });
    }

    private void fillText(MarkerWrapper mw){
        if(mw == null) return;

        TextView view = (TextView) getActivity().findViewById(R.id.titleText);

        view.setText(mw.mLocation.getTitle());
        view = (TextView)getActivity().findViewById(R.id.descriptionTextView);
        if(mw.mLocation.getDescription().length() > LENGTH)
            view.setText(mw.mLocation.getDescription().substring(0, 16));
        else
            view.setText(mw.mLocation.getDescription());
    }

    private void centerToLocation(double[] coords){
        this.centerToLocation(coords[0], coords[1]);
    }

    private void centerToLocation(double lat, double lon){
        if(this.mMap == null)
            return;

        if(mLocation != null){
            this.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), DEFAULT_ZOOM));
        }
        this.mZoomed = true;
    }

    private void setupCheckPoints(){
        if(this.mCoordinates == null)
            return;
        double[] coords;
        this.mMarkers = new MarkerWrapper[this.mCoordinates.length];
        float hue;
        for (int i = 0; i < this.mCoordinates.length; i++){
            coords = this.mCoordinates[i].getCoordinates();

            Log.d(this.mCoordinates[i].getTitle(), coords[0] + ", " + coords[1]);
            mMarkers[i] = new MarkerWrapper(this.mCoordinates[i], mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(coords[0], coords[1]))
                    .snippet(this.mCoordinates[i].getDescription())
                    .title(this.mCoordinates[i].getTitle())));
        }
    }

    public interface MeasuredDistanceCallbackListener{
        public static enum Measurement{
            FEET,
            MILES,
            METERS,
            KILOMETERS;
        }
        void onMapMeasuredDistance(String distance);
    }
    public interface UserInRange{
        void userInRange(boolean inRange, String errorMsg);
    }
    public interface TimedDistanceCallbackListener{
        void onMapTimedDistance(String time);
    }
    public interface OnFragmentInteractionListener {
        void onMapFragmentInteraction(CheckPoint checkPoint);
    }
}
