package com.example.jharshman.event;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private static Hashtable<DecodeHintType, Boolean> hints;
    static {
        hints = new Hashtable<DecodeHintType, Boolean>(1);
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
    }

    //camera
    private SurfaceView surfaceView;
    private SurfaceHolderCallback surfaceHolderCallback;
    private Camera.PreviewCallback cameraPreviewCallback;
    //views
    private ImageView pictureTakenView;
    private View centerView;
    private TextView txtScanResult;
    //timers...
    private Timer cameraTimer;
    private TimerTask cameraTimerTask;
    // HVGA
    final static int width = 1920; // 480
    final static int height = 1080; // 320
    int dstLeft, dstTop, dstWidth, dstHeight;

    private class SurfaceHolderCallback implements SurfaceHolder.Callback {

        private SurfaceHolder holder = null;
        private Camera camera;

        public SurfaceHolderCallback(SurfaceHolder holder) {
            this.holder = holder;
            this.holder.addCallback(this);
            this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
            Camera.Parameters parameters = camera.getParameters();

            //todo fix supported sizes
            // get supported preview size
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            height = sizes.get(0).height;
            width = sizes.get(0).width;

            Toast.makeText(getApplicationContext(), String.format("Height:%d Width:%d", height, width), Toast.LENGTH_SHORT).show();

            Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

            //todo fix crash on camera rotation
            // rotate based on phone orientation
            if(display.getRotation() == Surface.ROTATION_0) {
//                parameters.setPreviewSize(height, width);
                parameters.setPreviewSize(width, height);
                camera.setDisplayOrientation(90);
            }
            else if(display.getRotation() == Surface.ROTATION_90) {
                parameters.setPreviewSize(width, height);
            }
            else if(display.getRotation() == Surface.ROTATION_180) {
//                parameters.setPreviewSize(height, width);
                parameters.setPreviewSize(width, height);
            }
            else if(display.getRotation() == Surface.ROTATION_270) {
                parameters.setPreviewSize(width, height);
                camera.setDisplayOrientation(180);
            }

            camera.setParameters(parameters);
            camera.startPreview();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            camera = Camera.open();
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                camera.release();
                camera = null;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera = null;
        }

        /**
         * Auto focus and callback Camera.PreviewCallback
         */
        public void autoFocusAndPreviewCallback() {
            if (camera != null)
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            camera.setOneShotPreviewCallback(cameraPreviewCallback);
                        }
                    }
                });
        }
    }

    private class CameraTimerTask extends TimerTask {
        @Override
        public void run() {
            if (dstLeft == 0) {
                dstLeft = centerView.getLeft() * width / getWindowManager().getDefaultDisplay().getWidth();
                dstTop = centerView.getTop() * height / getWindowManager().getDefaultDisplay().getHeight();
                dstWidth = (centerView.getRight() - centerView.getLeft()) * width / getWindowManager().getDefaultDisplay().getWidth();
                dstHeight = (centerView.getBottom() - centerView.getTop()) * height / getWindowManager().getDefaultDisplay().getHeight();
            }
            surfaceHolderCallback.autoFocusAndPreviewCallback();
        }
    }

    private class DecodeImageTask extends AsyncTask<BinaryBitmap, Void, String> {
        @Override
        protected String doInBackground(BinaryBitmap... bitmap) {
            String decodedText = null;
            final Reader reader = new QRCodeReader();
            try {
                final Result result = reader.decode(bitmap[0], hints);
                decodedText = result.getText();
                cameraTimer.cancel();
            } catch (Exception e) {
                decodedText = e.toString();
            }
            return decodedText;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT)
                    .show();
            txtScanResult.setText(result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get main content image view
        ImageView mainImage = (ImageView) findViewById(R.id.main_image);
        // generate bitmap
        Bitmap bitmap = generateQRCode("Hello World!");

        // set image view to bitmap
        mainImage.setImageBitmap(bitmap);

        // write bitmap to file
        File file = new File(Environment.getExternalStorageDirectory(), "QRCode.bmp");
        writeBitmapToFile(bitmap, file);

        // read QR code

        txtScanResult = (TextView) this.findViewById(R.id.main_text);
        centerView = (View) this.findViewById(R.id.main_center_view);
        pictureTakenView = (ImageView) this.findViewById(R.id.main_image);

        surfaceView = (SurfaceView) this.findViewById(R.id.main_surface);
        surfaceHolderCallback = new SurfaceHolderCallback(
                surfaceView.getHolder());

        cameraPreviewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                // access to the specified range of frames of data
                final PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, dstLeft, dstTop, dstWidth, dstHeight, true);
                // output a preview image of the picture taken by the camera
                final Bitmap previewImage = source.renderCroppedGreyscaleBitmap();

                pictureTakenView.setImageBitmap(previewImage);
                // set this one as the source to decode
                final BinaryBitmap bitmap = new BinaryBitmap(
                        new HybridBinarizer(source));
                new DecodeImageTask().execute(bitmap);

            }
        };

        // Initialize the timer
        cameraTimer = new Timer();
        cameraTimerTask = new CameraTimerTask();
        cameraTimer.schedule(cameraTimerTask, 0, 80);

//        // get scan button
//        Button mainScanButton = (Button) findViewById(R.id.main_scan_button);
//        // listen for click to scan
//        mainScanButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // read bitmap
//                readQRCode();
//            }
//        });

    }

    /**
     * Read QR Code
     */
    private void readQRCode() {

        // launch intent to scan
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");

        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {

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
