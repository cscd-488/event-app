package com.example.jharshman.event;

import android.support.v4.app.Fragment;
import android.os.Bundle;
<<<<<<< HEAD
import android.support.design.widget.FloatingActionButton;
=======
>>>>>>> refs/remotes/origin/master
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private Fragment mPagerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

<<<<<<< HEAD
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GpsTracker.Event event = new GpsTracker.Event("Home", "This is my home", 47.48300354, -117.57133897);
                GpsTracker.Event event2 = new GpsTracker.Event("CEB", "You've arrived at the CEB", 47.48967, -117.585313);
                GpsTracker.create(view.getContext());
                GpsTracker.addLocation(event, event2);
            }
        });
=======

        if(findViewById(R.id.FragmentContainer)!=null) {
            if(savedInstanceState!=null)
                return;

            // todo: apply logic so this is only done on first run
            mPagerFragment = new ViewPagerFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.FragmentContainer, mPagerFragment)
                    .commit();
        }

>>>>>>> refs/remotes/origin/master
    }

    @Override
    public void onStop(){
        super.onStop();
        GpsTracker.enableNotifications();
    }

    @Override
    public void onResume(){
        super.onResume();
        GpsTracker.disableNotifications();
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
}
