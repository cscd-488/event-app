
/**
 * @author Bruce Emehiser
 *
 * Scan Fragment used for scanning QR codes
 *
 * This uses ZXing technology to decoded images
 * captured with the device camera.
 */

package com.example.jharshman.event;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Scan Fragment used to scan QR codes
 * and decode the string value contained
 * in them
 */
public class ScanFragment extends Fragment implements Camera.AutoFocusCallback, Camera.PreviewCallback, DecodeImageTask.Callback {

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
     * Scan Fragment Interaction listener to notify
     * when scan completes
     */
    private OnScanFragmentInteraction mListener;

    public ScanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHeader();
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // required check for implementation of interface
        if (context instanceof OnScanFragmentInteraction) {
            mListener = (OnScanFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnScanFragmentInteraction");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Starts scan timer
     */
    @Override
    public void onResume() {
        super.onResume();

        // get an instance of the camera
        mCamera = getCameraInstance();

        // if camera is already in use or does not exist, abort
        if(mCamera == null) {
            // notify user
            Toast.makeText(getContext(), "Camera Unavailable", Toast.LENGTH_SHORT).show();
        }

        // assign camera to preview
        mPreview = new CameraPreview(getContext(), mCamera);

        // add preview to frame layout
        FrameLayout preview = (FrameLayout) getActivity().findViewById(R.id.scan_frame);
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
    public void onPause() {
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


    private void setHeader() {
        TextView header = (TextView) getActivity().findViewById(R.id.headerTitle);
        header.setText("  QR Scanner");
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
     * This interface must be implemented by activities that contain this
     * fragment to allow fragment to pass data to the activity.
     */
    public interface OnScanFragmentInteraction {

        /**
         * Scan Fragment Interaction will be called with the
         * String representation of the decoded QR code
         *
         * @param code The String representation of the QR code
         */
        void onScanFragmentInteraction(String code);
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

        // return data to listener
        if(mListener != null) {
            try {
                mListener.onScanFragmentInteraction(code);
            } catch (NullPointerException e) {
                // listener has died
            }
        }
    }
}
