/**
 * @file Event.java
 * @author Aaron Young
 * @date 2016 03 01
 * @date 2016 03 18
 *
 * Map fragment class to display
 * locations
 */
package com.example.jharshman.event;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationMapFragment extends Fragment implements OnMapReadyCallback {
    public interface CoordinateCollection{
        public double[] getCoordinates();
        public boolean wasDisplayed();
        public String getTitle();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final float METERS_TO_FEET = 3.28084f;
    private static final int DEFAULT_ZOOM = 10;

    private static Location mLocation;

    private MapView mapView;
    private GoogleMap map;
    private List<Marker> markers = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private CoordinateCollection[] coordinates;
    private boolean zoomed = false;

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            mLocation = location;
            if(!zoomed)
                centerToCurrentLocation();
        }
    };

    public LocationMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationMapFragment newInstance(String param1, String param2) {
        LocationMapFragment fragment = new LocationMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_location_map, container, false);

       this.buildMap(v, savedInstanceState);

        return v;
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_location_map, container, false);
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

    /**
     * Returns the distance in meters from the last known user location
     *
     * @param  coordinateCollection  the coordinate to compare from
     * @return      the distance in meters from the last known location. -1 if no known location
     */
    public static float distanceFromUserMeter(CoordinateCollection coordinateCollection){
        if(mLocation == null)
            return -1f;

        float[] results = new float[1];
        Location.distanceBetween(mLocation.getLatitude(), mLocation.getLongitude(), coordinateCollection.getCoordinates()[0], coordinateCollection.getCoordinates()[1], results);
        return results[0];
    }

    /**
     * Returns the distance in feet from the last known user location
     *
     * @param  coordinateCollection  the coordinate to compare from
     * @return      the distance in feet from the last known location. -1 if no known location
     */
    public static float distanceFromUserFeet(CoordinateCollection coordinateCollection){
        if(mLocation == null)
            return -1f;

        float[] results = new float[1];
        Location.distanceBetween(mLocation.getLatitude(), mLocation.getLongitude(), coordinateCollection.getCoordinates()[0], coordinateCollection.getCoordinates()[1], results);
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

    public void addLocations(CoordinateCollection[] coordinates) {
        if(coordinates == null)
            throw new NullPointerException("CoordinateCollection array cannot be null");
        this.coordinates = coordinates;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.map.setMyLocationEnabled(true);

        this.setupCheckPoints();
        this.map.setOnMyLocationChangeListener(this.myLocationChangeListener);
    }

    private void centerToCurrentLocation(){
        if(this.map == null)
            return;

        if(mLocation != null){
            this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), DEFAULT_ZOOM));
        }
        this.zoomed = true;
    }

    private void setupCheckPoints(){
        if(this.coordinates == null)
            return;
        double[] coords;
        float hue;
        for (CoordinateCollection c : this.coordinates) {
            if (c.wasDisplayed())
                hue = BitmapDescriptorFactory.HUE_GREEN;
            else
                hue = BitmapDescriptorFactory.HUE_RED;

            coords = c.getCoordinates();
            markers.add(map.addMarker(new MarkerOptions()
                    .position(new LatLng(coords[0], coords[1]))
                    .title(c.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(hue))));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
