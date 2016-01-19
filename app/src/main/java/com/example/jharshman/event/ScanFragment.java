package com.example.jharshman.event;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFragment extends Fragment {

    public static final String TAG = "ScanFragment";

    private static Hashtable<DecodeHintType, Boolean> mHints;
    static {
        mHints = new Hashtable<DecodeHintType, Boolean>(1);
        mHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
    }

    //camera
    private SurfaceView mSurfaceView;
    private SurfaceHolderCallback mSurfaceHolderCallback;
    private Camera.PreviewCallback mCameraPreviewCallback;
    // views
    private ImageView mPictureTakenView;
    private View mCenterView;
    private TextView mScanResultText;
    // timers
    private Timer mCameraTimer;
    private TimerTask mCameraTimerTask;
    // video size
    final static int mWidth = 1920; // 480
    final static int mHeight = 1080; // 320
    int mDistanceLeft;
    int mDistanceTop;
    int mDistanceWidth;
    int mDistanceHeight;

    private class SurfaceHolderCallback implements SurfaceHolder.Callback {

        private SurfaceHolder mHolder = null;
        private Camera mCamera;

        public SurfaceHolderCallback(SurfaceHolder holder) {
            this.mHolder = holder;
            this.mHolder.addCallback(this);
            this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
        {
            Camera.Parameters parameters = mCamera.getParameters();

            //todo fix supported sizes
            // get supported preview size
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            height = sizes.get(0).height;
            width = sizes.get(0).width;

            Toast.makeText(getActivity().getApplicationContext(), String.format("Height:%d Width:%d", height, width), Toast.LENGTH_SHORT).show();

            Display display = ((WindowManager) getActivity().getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay();

            // stop preview if running, and change orientation
            mCamera.stopPreview();

            //todo fix crash on camera rotation
            // rotate based on phone orientation
            if(display.getRotation() == Surface.ROTATION_0) {
//                parameters.setPreviewSize(height, width);
                parameters.setPreviewSize(width, height);
                mCamera.setDisplayOrientation(90);
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
                mCamera.setDisplayOrientation(180);
            }

            mCamera.setParameters(parameters);
            mCamera.startPreview();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mCamera = Camera.open();
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                mCamera.release();
                mCamera = null;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera = null;
        }

        /**
         * Auto focus and callback Camera.PreviewCallback
         */
        public void autoFocusAndPreviewCallback() {
            if (mCamera != null)
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (success) {
                            camera.setOneShotPreviewCallback(mCameraPreviewCallback);
                        }
                    }
                });
        }
    }

    private class CameraTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mDistanceLeft == 0) {
                mDistanceLeft = mCenterView.getLeft() * mWidth / getActivity().getWindowManager().getDefaultDisplay().getWidth();
                mDistanceTop = mCenterView.getTop() * mHeight / getActivity().getWindowManager().getDefaultDisplay().getHeight();
                mDistanceWidth = (mCenterView.getRight() - mCenterView.getLeft()) * mWidth / getActivity().getWindowManager().getDefaultDisplay().getWidth();
                mDistanceHeight = (mCenterView.getBottom() - mCenterView.getTop()) * mHeight / getActivity().getWindowManager().getDefaultDisplay().getHeight();
            }
            mSurfaceHolderCallback.autoFocusAndPreviewCallback();
        }
    }

    private class DecodeImageTask extends AsyncTask<BinaryBitmap, Void, String> {
        @Override
        protected String doInBackground(BinaryBitmap... bitmap) {
            String decodedText = null;
            final Reader reader = new QRCodeReader();
            try {
                final Result result = reader.decode(bitmap[0], mHints);
                decodedText = result.getText();
                mCameraTimer.cancel();
            } catch (Exception e) {
                decodedText = e.toString();
            }
            return decodedText;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT)
                    .show();
            mScanResultText.setText(result);
        }
    }

    public ScanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan, container, false);


        // read QR code
        mScanResultText = (TextView) view.findViewById(R.id.main_text);
        mCenterView = (View) view.findViewById(R.id.main_center_view);
        mPictureTakenView = (ImageView) view.findViewById(R.id.main_image);

        mSurfaceView = (SurfaceView) view.findViewById(R.id.main_surface);
        mSurfaceHolderCallback = new SurfaceHolderCallback(mSurfaceView.getHolder());

        // initialize camera preview callback
        mCameraPreviewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                // access to the specified range of frames of data
                final PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data, mWidth, mHeight, mDistanceLeft, mDistanceTop, mDistanceWidth, mDistanceHeight, true);
                // output a preview image of the picture taken by the camera
                final Bitmap previewImage = source.renderCroppedGreyscaleBitmap();
                mPictureTakenView.setImageBitmap(previewImage);

                // set this one as the source to decode
                final BinaryBitmap bitmap = new BinaryBitmap(
                        new HybridBinarizer(source));
                new DecodeImageTask().execute(bitmap);
            }
        };

        // Initialize the timer
        mCameraTimer = new Timer();
        mCameraTimerTask = new CameraTimerTask();
        mCameraTimer.schedule(mCameraTimerTask, 0, 80);

        return view;
    }

}
