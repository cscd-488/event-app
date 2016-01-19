package com.example.jharshman.event;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {


    private static final String BASEURL = "http://securityvoodoo.net/~jharshman/";
    private static final String CONFIGS = "master.zones";

    /**
     * */
    private class AsyncNetworkOperation extends AsyncTask<URL, Integer, List<String>> {

        @Override
        protected List<String> doInBackground(URL... urls) {
            ArrayList<String>listConfigs = new ArrayList<>();
            BufferedReader bufferedReader = null;
            try {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(urls[0].openStream()));
                    String config;
                    while((config = bufferedReader.readLine())!= null) {
                        listConfigs.add(config);
                    }
                } finally {
                    bufferedReader.close();
                }
            } catch(MalformedURLException mue) {
                mue.printStackTrace();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }
            return listConfigs;
        }
    }

    private ArrayAdapter<Event> mListAdapter;
    private ListView mListView;
    private ArrayList<Event> mEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mListView = (ListView)findViewById(R.id.listView);

        // Todo: test reading json config
        List<String> configs;
        try {
            configs = new AsyncNetworkOperation().execute((new URL(BASEURL+CONFIGS))).get();
            Event temp;
            for(String config : configs) {
                temp = new Event(new URL(BASEURL+config));
                mEvents.add(temp);
            }

        }catch (MalformedURLException e) {
            e.printStackTrace();
        }catch (ExecutionException ee) {
            ee.printStackTrace();
        }catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        mListAdapter = new EventListAdapter(this, mEvents);
        mListView.setAdapter(mListAdapter);

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

    private String getJson(String filename) throws IOException {
        String json = null;
        InputStream inputStream = this.getAssets().open(filename);
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        inputStream.close();
        json = new String(buffer, "UTF-8");
        return json;
    }
}
