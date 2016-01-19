package com.example.jharshman.event;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int READ_QR_INTENT_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get main content image view
        ImageView mainImage = (ImageView) findViewById(R.id.main_image);

        // generate bitmap
//        Bitmap bitmap = generateQRCode("Hello World!");

        // set image view to bitmap
//        mainImage.setImageBitmap(bitmap);

        // write bitmap to file
//        File file = new File(Environment.getExternalStorageDirectory(), "QRCode.bmp");
//        writeBitmapToFile(bitmap, file);

        // read QR code via Intent
//        readQRCodeIntent();

        // read QR code via ScanFragment
        readQRCodeScan();
    }

    /**
     * Opens ScanFragment
     *
     * QR code String representation returned in Fragment callback
     */
    private void readQRCodeScan() {

        // get fragment manager
        FragmentManager fragmentManager = getFragmentManager();
        // try to find fragment, if it exists
        ScanFragment fragment =  (ScanFragment) fragmentManager.findFragmentByTag("scan_fragment");
        // if fragment does not exist, create a new one
        if(fragment == null) {
            fragment = new ScanFragment();
        }

        // fragment transaction

        final String FRAGMENT_SCAN_TAG = "fragment_scan_tag";

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(fragmentManager.findFragmentById(R.id.main_frame) == null) {
            fragmentTransaction.add(R.id.main_frame, fragment, FRAGMENT_SCAN_TAG);
        } else {
            fragmentTransaction.replace(R.id.main_frame, fragment, FRAGMENT_SCAN_TAG);
        }
        fragmentTransaction.commit();
    }

    /**
     * Read QR Code via Intent
     *
     * Result is recieved in onActivityResult
     */
    private void readQRCodeIntent() {

        // launch intent to scan
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

        startActivityForResult(intent, READ_QR_INTENT_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_OK) {
            if (requestCode == READ_QR_INTENT_REQUEST_CODE) {

                // get scan result
                String result = intent.getStringExtra("SCAN_RESULT");
                // get scan format
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");

                // update text view with data read
                TextView mainText = (TextView) findViewById(R.id.main_text);
                mainText.setText(result);
            }
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
}
