
/**
 * @author Bruce Emehiser
 *
 * Camera Preview used for camera preview
 * set in a frame layout
 */

package com.example.jharshman.event;

import android.content.Context;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * Surface Created
     * Set up and start preview
     * @param holder Surface View Holder
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (IOException e) {
            // do nothing
        }
    }

    /**
     * Surface Destroyed
     * @param holder Surface View Holder
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // camera is released by the calling activity
    }

    /**
     * Surface Changed
     * @param holder Surface View Holder
     * @param format Format
     * @param width Width
     * @param height Height
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or reformatting changes here

        // get window size
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        // get camera parameters and valid preview size
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size cs = sizes.get(0); //todo find optimal size
        width = cs.width;
        height = cs.height;

        // set camera preview rotation based on screen rotation
        if(display.getRotation() == Surface.ROTATION_0) {
            mCamera.setDisplayOrientation(90);
            parameters.setPreviewSize(width, height);

        }
        if(display.getRotation() == Surface.ROTATION_90) {
            parameters.setPreviewSize(width, height);
        }
        if(display.getRotation() == Surface.ROTATION_180) {
            parameters.setPreviewSize(width, height);
        }
        if(display.getRotation() == Surface.ROTATION_270) {
            mCamera.setDisplayOrientation(180);
            parameters.setPreviewSize(width, height);
        }

        mCamera.setParameters(parameters);

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            // do nothing
        }
    }
}