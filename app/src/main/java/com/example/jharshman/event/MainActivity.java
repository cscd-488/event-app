package com.example.jharshman.event;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements ScanFragment.OnScanFragmentInteraction, ButtonFragment.OnButtonFragmentInteraction {

    public static final String TAG = "MainActivity";

    private static final String BUTTON_FRAGMENT_TAG = "button_fragment";
    private static final String SCAN_FRAGMENT_TAG = "scan_fragment";
    private static final String CURRENT_FRAGMENT = "current_fragment";

    private String mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // load fragment into main frame
        Fragment fragment;

        // check for and load current fragment
        if(savedInstanceState != null) {
            mCurrentFragment = savedInstanceState.getString(CURRENT_FRAGMENT);

            fragment = getSupportFragmentManager().findFragmentByTag(mCurrentFragment);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, fragment, mCurrentFragment)
                    .commit();

        }
        else {
            // load button fragment
            fragment = getSupportFragmentManager().findFragmentByTag(BUTTON_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new ButtonFragment();
            }
            mCurrentFragment = BUTTON_FRAGMENT_TAG;

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_frame, fragment, BUTTON_FRAGMENT_TAG)
                    .commit();
        }
    }

    /**
     * Generate QR Code
     * Bitmap returned will be blank (white) if error occurred
     *
     * @param data The data to put into the QR code
     * @return Bitmap representation of QR code
     */
    private Bitmap generateQRCode(String data) {

        // bitmap to return
        int height = 150;
        int width = 150;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        // create a new QR code writer
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        try {
            // encode data to QR code bit matrix
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 150, 150);

            // turn bit matrix into bitmap
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    // set each pixel in bitmap based on bitmatrix position
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

        } catch (WriterException e) {
            // throw up
            Log.e(TAG, "Error creating QR code");
        }

        // return bitmap
        return bitmap;
    }

    /**
     * Write Bitmap to file
     *
     * @param bitmap The bitmap to write
     * @param file The File to write to
     * @return Boolean, true if success, false if failure
     */
    private boolean writeBitmapToFile(Bitmap bitmap, File file) {

        // check for null incoming parameters
        if(bitmap == null) {
            return false;
        }
        if(file == null) {
            return false;
        }

        try {
            // open output stream
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            // write bitmap to stream
            boolean written = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            Log.d(TAG, "File was written " + written);
            // close stream
            fileOutputStream.close();
        } catch(IOException e){
            Log.e(TAG, "Error writing bitmap to file");
            return false;
        }
        return true;
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

    /**
     * Scan Fragment Interaction will be called with the
     * String representation of the decoded QR code
     *
     * @param code The String representation of the QR code
     */
    @Override
    public void onScanFragmentInteraction(String code) {

        // get button fragment
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(BUTTON_FRAGMENT_TAG);
        if(fragment == null) {
            fragment = new ButtonFragment();
        }
        mCurrentFragment = BUTTON_FRAGMENT_TAG;

        // set the text to the fragment
        Bundle bundle = new Bundle();
        bundle.putString("code", code);
        fragment.setArguments(bundle);

        // swap current fragment for button fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, fragment, BUTTON_FRAGMENT_TAG)
                .commit();
    }

    /**
     * Handle button click
     *
     * @param clicked Button was clicked
     */
    @Override
    public void onButtonFragmentInteraction(boolean clicked) {

        // get scan fragment
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SCAN_FRAGMENT_TAG);
        if(fragment == null) {
            fragment = new ScanFragment();
        }
        mCurrentFragment = SCAN_FRAGMENT_TAG;

        // swap the current fragment for the button fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, fragment, SCAN_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save current fragment tag
        outState.putString(CURRENT_FRAGMENT, mCurrentFragment);
    }
}
