package com.example.jharshman.event;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // get main content image view
        ImageView mainImage = (ImageView) findViewById(R.id.main_image);
        // generate bitmap
        Bitmap bitmap = generateQRCode("Hello World!");

        // set image view to bitmap
        mainImage.setImageBitmap(bitmap);

        // write bitmap to file
        File file = new File(Environment.getExternalStorageDirectory(), "QRCode.bmp");
        writeBitmapToFile(bitmap, file);
    }

    /**
     * Generate QR Code
     *  Bitmap returned will be blank (white) if error occurred
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
