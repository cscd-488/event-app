package com.example.jharshman.event;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        CheckPointListFragment.OnFragmentInteractionListener,
        EventFragment.OnFragmentInteractionListener,
        CheckPointFragment.OnFragmentInteractionListener,
        LocationMapFragment.OnFragmentInteractionListener {

    public static final String PAGER_FRAGMENT = "PAGER_FRAGMENT";
    public static final String EVENT_FRAGMENT = "EVENT_FRAGMENT";
    public static final String CHECK_POINT_LIST_FRAGMENT = "CHECK_POINT_LIST_FRAGMENT";
    public static final String CHECK_POINT_FRAGMENT = "CHECK_POINT_FRAGMENT";
    public static final String LOCATION_MAP_FRAGMENT = "LOCATION_MAP_FRAGMENT";

    private Fragment mPagerFragment;
    SharedPreferences mSharedPreferences;

    // private Fragment mEventFragment;
    // private Fragment mCollectionsFragment;


    ArrayList<CheckPoint> mCheckPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
     * @param checkPoint The check point which was clicked
     */
    @Override
    public void onCheckPointListInteraction(CheckPoint checkPoint) {

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CHECK_POINT_LIST_FRAGMENT);
        if(fragment == null) {
            fragment = CheckPointFragment.newInstance(checkPoint);
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
        }
    }

    @Override
    public void onMapFragmentInteraction(CheckPoint checkPoint) {
        Log.d("Map", "Checkpoint Clicked From Map");
    }
}
