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
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationMapFragment extends Fragment implements OnMapReadyCallback{

    private static class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String data = "";

            try{
                data = downloadUrl(params[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String results){
            super.onPostExecute(results);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(results);
        }
    }

    private static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... params) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jsonObject = new JSONObject(params[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jsonObject);
            }catch(Exception e){
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
            String duration = "";

            try {
                if (results.size() < 1) {
                    return;
                }
            }catch(Exception e){
                e.printStackTrace();
                return;
            }

            for(int i = 0; i < results.size(); i++){
                points = new ArrayList<>();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = results.get(i);

                for(int j = 0; j < path.size(); j++){
                    HashMap<String, String> point = path.get(j);

                    if(j==0){
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){
                        duration = (String)point.get("duration");
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
            timeToLocation = duration;
        }
    }

    private class MarkerWrapper{
        public CheckPoint location;
        public Marker marker;

        public MarkerWrapper(CheckPoint location, Marker marker){
            this.location = location;
            this.marker = marker;
        }
    }

    // the fragment initialization parameters
    private static final String ARG_CHECKPOINT_ID = "arg_checkpoint_id";

    private static final float METERS_TO_FEET = 3.28084f;
    private static final float METERS_TO_KILO = 1000f;
    private static final float FEET_TO_MILES = 5280f;
    private static final int DEFAULT_ZOOM = 17;

    private static Location mLocation;
    private static String timeToLocation = "No Data";

    private MapView mapView;
    private int displayIndex = 0;
    private GoogleMap map;
    private MarkerWrapper[] markers = new MarkerWrapper[0];
    private OnFragmentInteractionListener mListener;
    private CheckPoint[] coordinates = new CheckPoint[0];
    private boolean zoomed = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            float distance;
            mLocation = location;
            if(!zoomed)
                centerToLocation(mLocation.getLatitude(), mLocation.getLongitude());
            if(coordinates == null || coordinates.length == 0)
                return;
            CheckPoint hovered = coordinates[displayIndex];

            TextView distanceView = (TextView) getView().findViewById(R.id.distanceTextView);
            TextView timeView = (TextView) getView().findViewById(R.id.timeToTargetTextView);

            distance = distanceFromUserMiles(hovered);

            timeToLocation(hovered);

            try {

                timeView.setText(timeToLocation);
                if (distance >= 0) {
                    distanceView.setText(String.format("%.2f", distance) + "miles");
                } else {
                    distanceView.setText("No user location");
                }
            }catch(NullPointerException e){
                e.printStackTrace();
            }

        }
    };

    private static String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try{

            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";

            while((line = br.readLine())!= null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception downloading", e.toString());
        }finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private static String getDirectionsUrl(LatLng start, LatLng dest){
        String strOrigin = "origin="+start.latitude+","+start.longitude;
        String strDest = "destination="+dest.latitude+","+dest.longitude;
        String sensor = "sensor=false";
        String mode = "mode=walking";
        String params = strOrigin+"&"+strDest+"&"+sensor+"&"+mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+params;

        return url;
    }

    private GoogleMap.OnMarkerClickListener myMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            for(int i = 0; i < markers.length; i++){
                if(markers[i].marker.equals(marker)){
                    displayIndex = i;

                    fillText(markers[i]);
                    return false;
                }
            }
            return true;
        }
    };

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
            mParam1 = getArguments().getString(ARG_CHECKPOINT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_location_map, container, false);

       this.buildMap(v, savedInstanceState);

        return v;
    }

    private void buildMap(View v, Bundle savedInstanceState){
        mapView = (MapView) v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public static float distanceFromUserKilometers(CheckPoint checkPoint) {
        return distanceFromUserMeter(checkPoint) / METERS_TO_KILO;
    }

    public static float distanceFromUserMiles(CheckPoint checkPoint) {
        return distanceFromUserFeet(checkPoint) / FEET_TO_MILES;
    }

    /**
     * Returns the walking time from the users last known location to the passed checkpoint
     *
     * @param  checkPoint  the coordinate to compare from
     * @return      the walking time in days/hrs/min/sec from the last known location. "No Data" if no known location
     */
    public static String timeToLocation(CheckPoint checkPoint){
        double[] coords = checkPoint.getCoordinates();

        String url = getDirectionsUrl(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()),
                new LatLng(coords[0], coords[1]));

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

        return timeToLocation;
    }

    /**
     * Returns the distance in meters from the last known user location
     *
     * @param  checkPoint  the coordinate to compare from
     * @return      the distance in meters from the last known location. -1 if no known location
     */
    public static float distanceFromUserMeter(CheckPoint checkPoint){
        if(mLocation == null)
            return -1f;

        float[] results = new float[1];
        Location.distanceBetween(mLocation.getLatitude(), mLocation.getLongitude(), checkPoint.getCoordinates()[0], checkPoint.getCoordinates()[1], results);
        return results[0];
    }

    /**
     * Returns the distance in feet from the last known user location
     *
     * @param  checkPoint  the coordinate to compare from
     * @return      the distance in feet from the last known location. -1 if no known location
     */
    public static float distanceFromUserFeet(CheckPoint checkPoint){
        if(mLocation == null)
            return -1f;

        float[] results = new float[1];
        Location.distanceBetween(mLocation.getLatitude(), mLocation.getLongitude(), checkPoint.getCoordinates()[0], checkPoint.getCoordinates()[1], results);
        results[0] *= METERS_TO_FEET;
        return results[0];
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        this.mapView.onLowMemory();
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

    public void addLocations(int eventID) {
        List<CheckPoint> coordinates = DataManager.instance(getContext()).getCheckpoints(eventID);

        if(coordinates == null)
            throw new NullPointerException("CoordinateCollection list cannot be null");

        this.coordinates = coordinates.toArray(this.coordinates);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.map.setMyLocationEnabled(true);

        this.setupCheckPoints();
        this.map.setOnMarkerClickListener(this.myMarkerClickListener);
        this.map.setOnMyLocationChangeListener(this.myLocationChangeListener);
        this.populateFunctionality();
    }

    private void populateFunctionality(){
        MarkerWrapper mw = null;
        if(this.markers.length != 0)
            mw = this.markers[this.displayIndex];

        ImageButton prevButton = (ImageButton) getActivity().findViewById(R.id.prevButton);
        ImageButton nextButton = (ImageButton) getActivity().findViewById(R.id.nextButton);
        ImageButton centerButton = (ImageButton) getActivity().findViewById(R.id.centerButton);
        ImageButton detailsButton = (ImageButton) getActivity().findViewById(R.id.displayCheckPointButton);
        this.fillText(mw);

        detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Not yet implemented", Toast.LENGTH_SHORT).show();
            }
        });

        centerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(markers.length == 0)return;

                centerToLocation(markers[displayIndex].location.getCoordinates());
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(markers.length == 0)return;

                displayIndex--;
                if (displayIndex == -1)
                    displayIndex = markers.length - 1;

                fillText(markers[displayIndex]);
                centerToLocation(markers[displayIndex].location.getCoordinates());
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(markers.length == 0)return;

                displayIndex++;
                if (displayIndex == markers.length)
                    displayIndex = 0;

                fillText(markers[displayIndex]);
                centerToLocation(markers[displayIndex].location.getCoordinates());
            }
        });
    }

    private void fillText(MarkerWrapper mw){
        if(mw == null) return;

        TextView view = (TextView) getActivity().findViewById(R.id.titleText);

        view.setText(mw.location.getTitle());
        view = (TextView)getActivity().findViewById(R.id.descriptionTextView);
        view.setText("Not implemented");
    }

    private void centerToLocation(double[] coords){
        this.centerToLocation(coords[0], coords[1]);
    }

    private void centerToLocation(double lat, double lon){
        if(this.map == null)
            return;

        if(mLocation != null){
            this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), DEFAULT_ZOOM));
        }
        this.zoomed = true;
    }

    private void setupCheckPoints(){
        if(this.coordinates == null)
            return;
        double[] coords;
        this.markers = new MarkerWrapper[this.coordinates.length];
        float hue;
        for (int i = 0; i < this.coordinates.length; i++){
            coords = this.coordinates[i].getCoordinates();

            markers[i] = new MarkerWrapper(this.coordinates[i], map.addMarker(new MarkerOptions()
                    .position(new LatLng(coords[0], coords[1]))
                    .snippet(this.coordinates[i].getDescription())
                    .title(this.coordinates[i].getTitle())));
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
