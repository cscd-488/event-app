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
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationMapFragment extends Fragment implements OnMapReadyCallback{
    private class MarkerWrapper{
        public CheckPoint location;
        public Marker marker;

        public MarkerWrapper(CheckPoint location, Marker marker){
            this.location = location;
            this.marker = marker;
        }
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final float METERS_TO_FEET = 3.28084f;
    private static final float METERS_TO_KILO = 1000f;
    private static final float FEET_TO_MILES = 5280f;
    private static final int DEFAULT_ZOOM = 17;

    private static Location mLocation;

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

            distance = distanceFromUserMiles(coordinates[displayIndex]);
            TextView view = (TextView) getView().findViewById(R.id.distanceTextView);
            view.setText(String.format("%.2f", distance) + "miles");
        }
    };

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

    public static float distanceFromUserKilometers(CheckPoint coordinateCollection) {
        return distanceFromUserMeter(coordinateCollection) / METERS_TO_KILO;
    }

    public static float distanceFromUserMiles(CheckPoint coordinateCollection) {
        return distanceFromUserFeet(coordinateCollection) / FEET_TO_MILES;
    }
    /**
     * Returns the distance in meters from the last known user location
     *
     * @param  coordinateCollection  the coordinate to compare from
     * @return      the distance in meters from the last known location. -1 if no known location
     */
    public static float distanceFromUserMeter(CheckPoint coordinateCollection){
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
    public static float distanceFromUserFeet(CheckPoint coordinateCollection){
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

    public void addLocations(CheckPoint[] coordinates) {
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
                    .title(this.coordinates[i].getTitle())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_event_test_image))));
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
