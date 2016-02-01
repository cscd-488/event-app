
/**
 * @author Bruce Emehiser
 *
 * Scan Activity used for scanning QR codes
 *
 * This uses ZXing technology to decoded images
 * captured with the device camera.
 */

package com.example.jharshman.event;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.util.Timer;
import java.util.TimerTask;

public class ScanActivity extends AppCompatActivity implements Camera.AutoFocusCallback, Camera.PreviewCallback, DecodeImageTask.Callback {

    /**
     * Intent result key for retrieving intent data
     */
    public static final String SCAN_RESULT = "SCAN_RESULT";

    /**
     * Image capture period in milliseconds
     */
    private static final int PERIOD = 100;

    /**
     * Camera for capturing images
     */
    private Camera mCamera;

    /**
     * Camera preview
     */
    private CameraPreview mPreview;

    /**
     * Timer used for timing QR code scans
     */
    private Timer mTimer;

    /**
     * Set layout to activity_scan.xml
     *
     * @param savedInstanceState Bundled saved instance data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
    }

    /**
     * Starts scan timer
     */
    @Override
    protected void onResume() {
        super.onResume();

        // get an instance of the camera
        mCamera = getCameraInstance();

        // if camera is already in use or does not exist, abort
        if(mCamera == null) {
            // notify user
            Toast.makeText(ScanActivity.this, "Camera Unavailable", Toast.LENGTH_SHORT).show();
            // finish activity
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

        // assign camera to preview
        mPreview = new CameraPreview(this, mCamera);

        // add preview to frame layout
        FrameLayout preview = (FrameLayout) findViewById(R.id.scan_frame);
        preview.addView(mPreview);

        // timer to take pictures on a schedule
        mTimer = new Timer();

        // start timer
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // begin single scan for QR code
                singleScan();
            }
        }, 0, PERIOD);
    }

    /**
     * Stops scan timer, and releases
     * timer, camera, and preview
     */
    @Override
    protected void onPause() {
        super.onPause();

        // stop timer
        if(mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }

        // stop preview
        if(mPreview != null) {
            mPreview.getHolder().removeCallback(mPreview);
            mPreview = null;
        }

        // stop camera
        if(mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * Begin singe QR code scan process
     */
    private void singleScan() {
        if(mCamera != null) {

            // set camera parameters to produce JPEG data
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(PixelFormat.JPEG);
            mCamera.setParameters(parameters);

            // tell camera to focus and take picture, with this as on focus callback
            mCamera.autoFocus(this);
        }
    }


    /**
     * Gets Camera instance safely
     *
     * @return Camera Instance, or null if camera is already in use
     */
    private static Camera getCameraInstance(){
        Camera camera = null;
        try {
            camera = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return camera; // returns null if camera is unavailable
    }

    /**
     * Called when the camera auto focus completes.
     * When auto focus completes, one shot preview callback is set.
     *
     * @param success true if focus was successful, false if otherwise
     * @param camera  the Camera service object
     */
    @Override
    public void onAutoFocus(boolean success, Camera camera) {

        // check for successful focus
        if(success) {
            // take a single preview shot
            camera.setOneShotPreviewCallback(this);
        }
    }

    /**
     * Called as preview frames are displayed.  This callback is invoked
     * on the event thread open(int) was called from.
     * <p/>
     * <p>If using the {@link ImageFormat#YV12} format,
     * refer to the equations in {@link Camera.Parameters#setPreviewFormat}
     * for the arrangement of the pixel data in the preview callback
     * buffers.
     *
     * @param data   the contents of the preview frame in the format defined
     *               by {@link ImageFormat}, which can be queried
     *               with {@link Camera.Parameters#getPreviewFormat()}.
     *               If {@link Camera.Parameters#setPreviewFormat(int)}
     *               is never called, the default will be the YCbCr_420_SP
     *               (NV21) format.
     * @param camera the Camera service object.
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        // get the size of the picture
        int frameHeight = camera.getParameters().getPreviewSize().height;
        int frameWidth = camera.getParameters().getPreviewSize().width;

        // create new Luminance Source
        final PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, frameWidth, frameHeight, 0, 0, frameWidth, frameHeight);

        // convert to binary bitmap which can be used by our image decoder
        final BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        // set self as listener, and start decode image
        DecodeImageTask decodeImageTask = new DecodeImageTask();
        decodeImageTask.setCallbackListener(this);
        decodeImageTask.execute(binaryBitmap);
    }

    /**
     * On Post Execute is called by the Async Task when it successfully finishes
     * You should set yourself as a listener before executing task, or else it may
     * finish before you are added.
     *
     * @param code The String representation of the successfully decoded QR code.
     */
    @Override
    public void onTaskCompleted(String code) {

        // return data via intent
        Intent result = new Intent();
        result.putExtra(SCAN_RESULT, code);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
