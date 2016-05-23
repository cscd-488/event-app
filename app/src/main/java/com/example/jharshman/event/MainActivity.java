package com.example.jharshman.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements
        CheckPointListFragment.OnFragmentInteractionListener,
        EventFragment.OnFragmentInteractionListener,
        CheckPointFragment.OnFragmentInteractionListener,
        LocationMapFragment.OnFragmentInteractionListener,
        ScanFragment.OnScanFragmentInteraction{

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
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tracker = GpsTracker.create(this);

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
                    .addToBackStack(null)
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
        if(count == 0) {
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
            fragment = CheckPointFragment.newInstance(checkPointID);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentContainer, fragment)
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
    public void onMapFragmentInteraction(int checkPoint) {
        Log.d("Map", "Checkpoint Clicked From Map");

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CHECK_POINT_FRAGMENT);
        if(fragment == null) {
            fragment = CheckPointFragment.newInstance(checkPoint);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContainer, fragment).commit();
        getSupportFragmentManager().beginTransaction().addToBackStack(null);

        int id = getSupportFragmentManager().beginTransaction().commit();

        getSupportFragmentManager().popBackStack(id, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Bundle args = new Bundle();
        args.putInt(CheckPointFragment.CHECK_POINT_KEY, checkPoint);

        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .addToBackStack(CHECK_POINT_FRAGMENT)
                .commit();
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
}
