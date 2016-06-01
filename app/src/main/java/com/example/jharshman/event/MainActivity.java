package com.example.jharshman.event;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.io.File;
import java.io.FileOutputStream;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements
        CheckPointListFragment.OnFragmentInteractionListener,
        EventFragment.OnFragmentInteractionListener,
        CheckPointFragment.OnFragmentInteractionListener,
        LocationMapFragment.OnFragmentInteractionListener,
        ScanFragment.OnScanFragmentInteraction {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "9x1IkXxYkHIo9cu90EISCRBDJ";
    private static final String TWITTER_SECRET = "J2CVrrG8m9rZkiTjmJWLa0PKb8BQ5Li3rWFi8S4Na0NQBtUxEA";


    private static final String TAG = "MainActivity";

    public static final String PAGER_FRAGMENT = "PAGER_FRAGMENT";
    public static final String EVENT_FRAGMENT = "EVENT_FRAGMENT";
    public static final String CHECK_POINT_LIST_FRAGMENT = "CHECK_POINT_LIST_FRAGMENT";
    public static final String CHECK_POINT_FRAGMENT = "CHECK_POINT_FRAGMENT";
    public static final String LOCATION_MAP_FRAGMENT = "LOCATION_MAP_FRAGMENT";
    public static final String SCAN_FRAGMENT = "SCAN_FRAGMENT";

    private static GpsTracker tracker;

    private Fragment mPagerFragment;
    SharedPreferences mSharedPreferences;

    // private Fragment mEventFragment;
    // private Fragment mCollectionsFragment;


    private int mCurrentCheckpointID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);

        tracker = GpsTracker.create(this);

        setUpHeader();

        if(findViewById(R.id.FragmentContainer)!=null) {
            if (savedInstanceState != null)
                return;

            /* todo: insert fragment(s) for rest of application... */

            Fragment fragment = getSupportFragmentManager().findFragmentByTag(EVENT_FRAGMENT);
            if(fragment == null) {
                fragment = new EventFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.FragmentContainer, fragment, EVENT_FRAGMENT)
                    .commit();
        }

        // check if application is in first use...
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
        boolean firstUse = mSharedPreferences.getBoolean(getString(R.string.first_use), true);
        if(firstUse) {
            mPagerFragment = new ViewPagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FragmentContainer, mPagerFragment, PAGER_FRAGMENT)
                    .addToBackStack(null)
                    .commit();

            mSharedPreferences.edit().putBoolean(getString(R.string.first_use), false).apply();
        }
    }

    public static GpsTracker getTracker(){
        return tracker;
    }

    @Override
    protected void onStart(){
        if(tracker != null)
            tracker.connect();
        super.onStart();
    }

    @Override
    protected void onStop(){
        if(tracker != null)
            tracker.disconnect();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        android.app.Fragment fragment = getFragmentManager().findFragmentByTag(EVENT_FRAGMENT);

        if(ShareDrawer.isOpen()){
            ShareDrawer.exit();
        } else if(false){

        } else if(count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    /**
     * Called from login fragment in login flow.
     * Indicates user's continuation into rest of the application.
     * Calls onBackPressed to pop the login flow fragment off the stack.
     *
     * @param view The view that was clicked
     * */
    public void endLoginFlow(View view) {
        onBackPressed();
    }

    @Override
    public void onEventInteraction(int eventID) {
        // todo implement method to choose fragment based on id

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CHECK_POINT_LIST_FRAGMENT);
        if(fragment == null) {
            fragment = CheckPointListFragment.getInstance(eventID);
        }
        else {
            Bundle args = new Bundle();
            args.putInt(CheckPointFragment.CHECK_POINT_KEY, eventID);
            fragment.setArguments(args);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentContainer, fragment)
                .addToBackStack(CHECK_POINT_LIST_FRAGMENT)
                .commit();
    }

    /**
     * Check Point was clicked
     *
     * @param checkPointID The id of the check point which was clicked
     */
    @Override
    public void onCheckPointListInteraction(int checkPointID) {

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CHECK_POINT_LIST_FRAGMENT);
        if(fragment == null) {
            fragment = CheckPointFragment.instance(checkPointID);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentContainer, fragment, CHECK_POINT_FRAGMENT)
                .addToBackStack(CHECK_POINT_FRAGMENT)
                .commit();
    }

    /**
     * Interaction from Check Point. Launch the appropriate
     * fragment, and pass it the value.
     *
     * @param button_id The button which was clicked to start
     *                      the interaction callback.
     * @param checkpoint_id The id of the check point.
     */
    @Override
    public void onCheckPointInteraction(int button_id, int checkpoint_id) {

        switch (button_id) {

            case R.id.fragment_check_point_map_button:

                // launch map fragment

                Fragment fragment = getSupportFragmentManager().findFragmentByTag(LOCATION_MAP_FRAGMENT);

                if(fragment == null) {
                    fragment = LocationMapFragment.newInstance(checkpoint_id);
                }

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FragmentContainer, fragment, LOCATION_MAP_FRAGMENT)
                        .addToBackStack(LOCATION_MAP_FRAGMENT)
                        .commit();

                break;
            case R.id.fragment_check_point_card_share_button:

                // todo launch the share fragment

                break;

            case R.id.fragment_check_point_check_in_button:

                // todo authenticate the check in, and relaunch the checkpoint fragment

                ScanFragment scanFragment = (ScanFragment) getSupportFragmentManager().findFragmentByTag(SCAN_FRAGMENT);

                if(scanFragment == null) {
                    scanFragment = new ScanFragment();
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.FragmentContainer, scanFragment, SCAN_FRAGMENT)
                        .addToBackStack(SCAN_FRAGMENT)
                        .commit();

                // save the current checkpoint so we know which checkpoint to test scan result against
                mCurrentCheckpointID = checkpoint_id;


                break;
        }
    }

    @Override
    public void onMapFragmentInteraction(CheckPoint checkPoint) {

        Log.d("Map", "Checkpoint Clicked From Map");

        // get the fragment and set the arguments on it
        CheckPointFragment fragment = null;
        try {
            fragment = (CheckPointFragment) getSupportFragmentManager().findFragmentByTag(CHECK_POINT_FRAGMENT);
        } catch (ClassCastException e) {
            Log.e(TAG, "Error casting fragment to checkpoint fragment");
            return;
        }

        if (fragment != null) {
            fragment.setCheckPointID(checkPoint.getID());
            getSupportFragmentManager().popBackStack();
        }
        else {
            fragment = CheckPointFragment.instance(checkPoint.getID());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.FragmentContainer, fragment, CHECK_POINT_FRAGMENT)
                    .addToBackStack(CHECK_POINT_FRAGMENT)
                    .commit();
        }
    }

    /**
     * Scan Fragment Interaction will be called with the
     * String representation of the decoded QR code
     *
     * @param code The String representation of the QR code
     */
    @Override
    public void onScanFragmentInteraction(String code) {

        // todo implement scan fragment interaction

        DataManager dataManager = DataManager.instance(this);

        // get the current checkpoint
        CheckPoint checkpoint = dataManager.getCheckpoint(mCurrentCheckpointID);
        String checkpoint_code = checkpoint.getQR();

        Log.i(TAG, "Scan completed: " + code);
        Log.i(TAG, "Checkpoint QR code: " + checkpoint.getQR());

        // check to see if the codes match
        if(checkpoint_code.compareTo(code) == 0) {
            // updated checked
            dataManager.updateChecked(checkpoint.getID(), true);
            Toast.makeText(this, "Check In Successful", Toast.LENGTH_LONG).show();
        }
        else {
            // notify user of failure
            Toast.makeText(this, "Code Does Not Match", Toast.LENGTH_LONG).show();
        }

        // launch checkpoint fragment again
        getSupportFragmentManager().popBackStack();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ShareDrawer.DEFAULT_SHARE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ShareDrawer.defaultShare();
                    ShareDrawer.exit();
                } else {
                    Toast.makeText(this, "External storage permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case ShareDrawer.CAMERA_SHARE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ShareDrawer.takePicture();
                    ShareDrawer.exit();
                }
                else{
                    Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == ShareDrawer.LOAD_IMAGE && null != data) {
                sharePhoto(data);
                ShareDrawer.exit();
            } else if(requestCode == ShareDrawer.CAMERA_LOAD && null != data){
                shareCameraPhoto(data);
                ShareDrawer.exit();
            } else if (requestCode == ShareDrawer.LOAD_IMAGE){
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG)
                        .show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void sharePhoto(Intent data){
        Uri image = data.getData();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, image);
        startActivity(Intent.createChooser(share , "Share to:"));
    }

    public void shareCameraPhoto(Intent data){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(PackageManager.PERMISSION_GRANTED == permissionCheck){
            try {

                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                File file = new File(this.getCacheDir(), "tmp.png");
                FileOutputStream fOut = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                fOut.flush();
                fOut.close();
                file.setReadable(true, false);

                final Intent intent = new Intent(     android.content.Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                intent.setType("image/*");
                startActivity(intent);

            } catch (Exception e) {

            }
        } else if(PackageManager.PERMISSION_DENIED == permissionCheck){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    ShareDrawer.CAMERA_SHARE);
        }
    }

    private void setUpHeader(){
        TextView header = (TextView) findViewById(R.id.headerTitle);
        GradientDrawable gradient = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{ Color.parseColor("#3FA9F5"), Color.parseColor("#55D883")});
        header.setBackground(gradient);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#3FA9F5"));
        }
    }
}
