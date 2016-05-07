
/**
 * @author Bruce Emehiser
 *
 * Decode Image task Decodes a BinaryBitmap image, and notifies a listener
 * when the task finishes.
 *
 * Decoding an image is generally a very quick process, so it would be a good
 * idea to set the listener BEFORE telling the task to execute.
 */

package com.example.jharshman.event;

import android.os.AsyncTask;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.qrcode.QRCodeReader;

public class DecodeImageTask extends AsyncTask<BinaryBitmap, Void, String> {

    /**
     * The listener who wants to be notified when the task completes.
     */
    DecodeImageTask.Callback mListener;

    /**
     * To get result of DecodeImageTask,
     * users must implement the callback
     * interface, so they will know when
     * the task completes successfully.
     */
    public interface Callback {

        /**
         * On Post Execute is called by the Async Task when it successfully finishes
         * You should set yourself as a listener before executing task, or else it may
         * finish before you are added.
         *
         * @param code The String representation of the successfully decoded QR code.
         */
        void onTaskCompleted(String code);
    }

    /**
     * Sets the callback listener to be notified when a QR code is decoded
     * @param listener The listener who wants to be notified.
     */
    public void setCallbackListener(DecodeImageTask.Callback listener) {

        // check for null listener
        if(listener != null) {
            // set callback listener
            mListener = listener;
        }
    }

    /**
     * Searches Bitmap image for a QR code, and returns the String representation
     * of it if a valid QR code was found.
     * Returns empty String if no valid QR code was found.
     *
     * @param bitmap The Bitmap to decode
     * @return The string representation of the Bitmap, or "" if no valid QR code was found
     */
    @Override
    protected String doInBackground(BinaryBitmap... bitmap) {
        String decodedText;
        // get QR reader
        final Reader reader = new QRCodeReader();
        // try to decode QR code
        try {
            // get Result from decoder
            final Result result = reader.decode(bitmap[0]);
            // get text from Result
            decodedText = result.getText();
        } catch (Exception e) {
            // set text to blank, no QR code found
            decodedText = "";
        }
        // return text
        return decodedText;
    }

    /**
     * On Post Execute will be called when the async task completes.
     * This will notify anyone who has chosen to listen for a completion event.
     *
     * @param result The String representation of the QR code to send to a listener
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        // check for successfully decoded of length > 0
        if(result != null && result.length() > 0 && mListener != null) {
            // notify listener, if any
            // listener may have died, so catch exception
            try {
                mListener.onTaskCompleted(result);
            } catch (NullPointerException e) {
                // do nothing
            }

        }
    }
}