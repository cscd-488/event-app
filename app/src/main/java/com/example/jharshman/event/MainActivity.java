package com.example.jharshman.event;

<<<<<<< HEAD
import android.net.Uri;
=======
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
>>>>>>> refs/remotes/origin/master
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

<<<<<<< HEAD
public class MainActivity extends AppCompatActivity implements LocationMapFragment.OnFragmentInteractionListener {

    private LocationMapFragment mPagerFragment;
=======
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        CheckPointFragment.OnFragmentInteractionListener,
        EventFragment.OnEventInteraction {

    public static final String PAGER_FRAGMENT = "PAGER_FRAGMENT";
    public static final String EVENT_FRAGMENT = "EVENT_FRAGMENT";
    public static final String CHECK_POINT_FRAGMENT = "CHECK_POINT_FRAGMENT";

    private Fragment mPagerFragment;
    SharedPreferences mSharedPreferences;

    // private Fragment mEventFragment;
    // private Fragment mCollectionsFragment;


    ArrayList<CheckPoint> mCheckPoints;
>>>>>>> refs/remotes/origin/master

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

<<<<<<< HEAD
            /*/ todo: apply logic so this is only done on first run
            mPagerFragment = new ViewPagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.FragmentContainer, mPagerFragment)
                    .commit();*/
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            mPagerFragment = new LocationMapFragment();
            transaction.add(R.id.FragmentContainer, mPagerFragment);
            transaction.commit();

            this.mPagerFragment.addLocations(new CheckPoint[]{new CheckPoint("Test", "Discription", "Image", new double[]{47.489681, -117.58534})});
=======
            /* todo: insert fragment(s) for rest of application... */


        }

        // check if application is in first use...
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
        boolean firstUse = mSharedPreferences.getBoolean(getString(R.string.first_use), true);
        if(firstUse) {
            mPagerFragment = new ViewPagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.FragmentContainer, mPagerFragment, PAGER_FRAGMENT)
                    .addToBackStack(null)
                    .commit();

            mSharedPreferences.edit().putBoolean(getString(R.string.first_use), false).apply();
>>>>>>> refs/remotes/origin/master
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

<<<<<<< HEAD
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
=======
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
    @Override
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
    @Override
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

>>>>>>> refs/remotes/origin/master
}
