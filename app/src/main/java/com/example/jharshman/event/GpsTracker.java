package com.example.jharshman.event;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.ArrayList;

/**
 * Created by Aaron on 1/24/2016.
 */
public class GpsTracker implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static class Event implements Comparable<Event>{
        private String name;
        private String notification;

        private double lat;
        private double longi;
        private boolean displayed = false;

        public Event(String _name, String _notification, double _lat, double _longi){
            this.name = _name;
            this.notification = _notification;
            this.lat = _lat;
            this.longi = _longi;
        }

        public void displayed(){
            this.displayed = true;
        }

        public boolean wasDisplayed(){return this.displayed;}

        public String[] getStrings(){
            return new String[] {this.name, this.notification};
        }

        public double[] getCoordinate(){
            return new double[] {this.lat, this.longi};
        }

        @Override
        public int compareTo(Event another) {
            if(this.longi == another.longi && this.lat == another.lat)
                return 0;
            return -1;
        }
    }

    private static final double buffer = 0.00100000;
    private static GpsTracker gpsTracker = null;
    private static Context context;
    private static GoogleApiClient googleApiClient;

    private ArrayList<Event> locations;
    private Event inRange;

    private GpsTracker(){
        this.inRange = null;
        this.locations = new ArrayList<>();
    }

    public static GpsTracker create(Context _context){
        context = _context;
        if(gpsTracker == null)
            gpsTracker = new GpsTracker();

        googleApiClient = null;

        locationServicesCheck();
        setupGps();

        return gpsTracker;
    }

    private static void setupGps(){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsTracker);
    }

    private static void locationServicesCheck() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(gpsTracker)
                    .addOnConnectionFailedListener(gpsTracker).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        (Activity) context, 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    public static void addLocation(String _name, String _notification, double _lat, double _longi){
        addLocation(new Event(_name , _notification, _lat, _longi));
    }

    public static void addLocation(Event ... location){
        for(Event loc : location)
            gpsTracker.locations.add(loc);
    }

    public static void addLocation(Event location){
        gpsTracker.locations.add(location);
    }

    public static void removeLocation(Event ... locations) {
        for(Event loc : locations)
            removeLocation(loc);
    }
    public static boolean removeLocation(Event location){
        Event toRemove = null;

        for(Event loc : gpsTracker.locations){
            if(loc.compareTo(location) == 0) {
                toRemove = loc;
                break;
            }
        }

        gpsTracker.locations.remove(toRemove);
        return (toRemove != null);
    }

    public static Event getEventInRange(){return gpsTracker.inRange;}

    private static Event inRange(double _lat, double _longi){
        double[] coord = {};

        for(Event event : gpsTracker.locations){
            coord = event.getCoordinate();

            if(_lat - buffer < coord[0] && coord[0] < _lat + buffer && _longi - buffer < coord[1] && coord[1] < _longi + buffer) {
                gpsTracker.inRange = event;
                return event;
            }
        }

        Toast.makeText(context, _lat + " : " + coord[0], Toast.LENGTH_SHORT).show();

        return null;
    }

    private void sendNotification(Context context){
        int mId = 0;
        String[] details = inRange.getStrings();

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(details[0])
                .setContentText(details[1]);

        Intent notIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = (location.getLatitude());
        double longitude = (location.getLongitude());
        Event event = inRange(latitude, longitude);

        if(event != null){
            if(!event.wasDisplayed()) {
                sendNotification(context);
                event.displayed();
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
