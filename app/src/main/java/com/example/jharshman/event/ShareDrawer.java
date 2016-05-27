package com.example.jharshman.event;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.plus.PlusShare;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.acl.Permission;

/**
 * Created by dmacy on 5/22/2016.
 */

public class ShareDrawer {
    public static final int LOAD_IMAGE = 555;
    public static final int DEFAULT_SHARE = 777;
    public static final int CAMERA_SHARE = 888;

    private static CheckPoint mMediaContent;
    private static Activity mActivity;
    private static boolean mSharable;

    private static ListView mDrawerList;
    private static DrawerLayout mDrawerLayout;

    public static void run(Activity activity, CheckPoint content){
        mActivity = activity;
        mMediaContent = content;

        if(isSharable()) {
            setupDrawer();
            mDrawerLayout.openDrawer(mDrawerList);
        }
    }

    public static void exit(){
        mDrawerLayout.closeDrawers();
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mActivity = null;
        mMediaContent = null;
        mSharable = false;
    }

    private static void setupDrawer(){
        mDrawerLayout = (DrawerLayout) mActivity.findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) mActivity.findViewById(R.id.share_drawer);
        ShareAdapter shareAdapter = new ShareAdapter(mActivity);
        mDrawerList.setAdapter(shareAdapter);
        mDrawerList.setClickable(true);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                share(position);
            }
        });
    }

    private static boolean isSharable(){
        if(mActivity != null && mMediaContent != null) mSharable = true;
        else mSharable = false;

        return mSharable;
    }

    private static void share(int position) {
        if(position == 0)
            shareOnFacebook();
        else if(position == 1)
            tweet();
        else if(position == 2)
            shareWithGooglePlus();
        else if(position == 3)
            getPhotoOptions();
        else
            Toast.makeText(mActivity, "Share failed.", Toast.LENGTH_SHORT).show();
    }

    private static void sharePhoto(int position){
        if(position == 0)
            defaultShare();
        else if(position == 1)
            galleryShare();
        else if(position == 2)
            cameraShare();
        else
            Toast.makeText(mActivity, "Photo share failed.", Toast.LENGTH_SHORT).show();
    }

    private static void defaultShare(){
        int permissionCheck = ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(PackageManager.PERMISSION_GRANTED == permissionCheck)
            shareImageURL();
        else if(PackageManager.PERMISSION_DENIED == permissionCheck){
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    DEFAULT_SHARE);
        }
        else
            Toast.makeText(mActivity, "Default photo share failed.", Toast.LENGTH_SHORT).show();
    }

    public static void shareImageURL(){
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if(  PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(mActivity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    try {
                        URL url = new URL(mMediaContent.getImageSrc());

                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();

                        InputStream input = connection.getInputStream();
                        Bitmap immutableBpm = BitmapFactory.decodeStream(input);
                        Bitmap mutableBitmap = immutableBpm.copy(Bitmap.Config.ARGB_8888, true);

                        String path = MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), mutableBitmap, "Nur", null);

                        Uri uri = Uri.parse(path);

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/*");
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        mActivity.startActivity(Intent.createChooser(share , "Share to:"));

                    } catch (MalformedURLException e){
                        Toast.makeText(mActivity, "Invalid link.", Toast.LENGTH_SHORT).show();
                    } catch (IOException e){
                        Toast.makeText(mActivity, "Unable to connect to image.", Toast.LENGTH_SHORT).show();
                    }
            }
        });

        thread.start();
    }


    public static void galleryShare(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        mActivity.startActivityForResult(galleryIntent, LOAD_IMAGE);
    }

    public static void cameraShare(){
        int permissionCheck = ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.CAMERA);

        if(PackageManager.PERMISSION_GRANTED == permissionCheck)
            takePicture();
        else if(PackageManager.PERMISSION_DENIED == permissionCheck){
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_SHARE);
        }
        else
            Toast.makeText(mActivity, "Default photo share failed.", Toast.LENGTH_SHORT).show();

    }

    public static void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
            mActivity.startActivityForResult(takePictureIntent, LOAD_IMAGE);
        }
    }

    private static void getPhotoOptions(){
        mDrawerList = (ListView) mActivity.findViewById(R.id.share_drawer);
        PhotoAdapter photoAdapter = new PhotoAdapter(mActivity);
        mDrawerList.setAdapter(photoAdapter);
        mDrawerList.setClickable(true);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sharePhoto(position);
            }
        });
    }

    private static void shareOnFacebook(){
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle(mMediaContent.getTitle())
                .setContentDescription(mMediaContent.getDescription())
                .setContentUrl(Uri.parse(mMediaContent.getImageSrc()))
                .build();

        ShareDialog.show(mActivity, content);
    }

    private static void shareWithGooglePlus(){
        Intent shareIntent = new PlusShare.Builder(mActivity)
                .setText(mMediaContent.getTitle() + " - " + mMediaContent.getDescription())
                .getIntent()
                .setPackage("com.google.android.apps.plus");

        try {
            mActivity.startActivity(shareIntent);
        } catch (ActivityNotFoundException e){
            Toast.makeText(mActivity, "Google+ Not Installed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private static void tweet(){
        TweetComposer.Builder builder = new TweetComposer.Builder(mActivity)
                .text(mMediaContent.getTitle() + " - " + mMediaContent.getDescription());
        builder.show();
    }
}
