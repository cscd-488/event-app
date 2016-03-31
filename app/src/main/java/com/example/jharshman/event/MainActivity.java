package com.example.jharshman.event;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        LocationMapFragment.OnFragmentInteractionListener{

    public static final String PAGER_FRAGMENT = "PAGER_FRAGMENT";
    public static final String EVENT_FRAGMENT = "EVENT_FRAGMENT";
    public static final String CHECK_POINT_FRAGMENT = "CHECK_POINT_FRAGMENT";

    private LocationMapFragment mPagerFragment;
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
        GpsTracker.create(this);

        if(findViewById(R.id.FragmentContainer)!=null) {
            if (savedInstanceState != null)
                return;

            /*/ todo: apply logic so this is only done on first run
            mPagerFragment = new ViewPagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.FragmentContainer, mPagerFragment)
                    .commit();*/
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            mPagerFragment = new LocationMapFragment();
            mPagerFragment.addLocations(new LocationMapFragment.CoordinateCollection[] {new LocationMapFragment.CoordinateCollection() {
                @Override
                public double[] getCoordinates() {
                    return new double[]{47.490203, -117.585146};
                }

                @Override
                public boolean wasDisplayed() {
                    return false;
                }

                @Override
                public String getTitle() {
                    return "CEB Building";
                }
            }, new LocationMapFragment.CoordinateCollection() {
                @Override
                public double[] getCoordinates() {
                    return new double[]{47.491392, -117.582813};
                }

                @Override
                public boolean wasDisplayed() {
                    return false;
                }

                @Override
                public String getTitle() {
                    return "Campus Mall";
                }
            }});
            transaction.add(R.id.FragmentContainer, mPagerFragment);
            transaction.commit();
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

    public void onFragmentInteraction(Uri uri) {

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

    /**
     * */

    public void onCheckPointInteraction(int id) {
        // todo implement method to handle click based on id

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(EVENT_FRAGMENT);
        if(fragment == null) {
            fragment = new EventFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentContainer, new EventFragment())
                .addToBackStack(EVENT_FRAGMENT)
                .commit();
    }

    /**
     * */

    public void onEventInteraction(int id) {
        // todo implement method to choose fragment based on id

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CHECK_POINT_FRAGMENT);
        if(fragment == null) {
            fragment = CheckPointFragment.newInstance(id);
        }
        else {
            Bundle args = new Bundle();
            args.putInt(CheckPointFragment.EVENT_ID_KEY, id);
            fragment.setArguments(args);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.FragmentContainer, fragment)
                .addToBackStack(CHECK_POINT_FRAGMENT)
                .commit();
    }
}
