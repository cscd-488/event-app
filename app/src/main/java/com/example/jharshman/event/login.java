package com.example.jharshman.event;

/* standard android libraries */
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/* google gms libraries */
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import de.hdodenhof.circleimageview.CircleImageView;

public class login extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "LoginFragment";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private String mOauth2Token;
    private String mPersonName;
    private Uri mPersonPhoto;

    private CircleImageView mProfileImage;
    private ImageView mMainLogo;
    private TextView mWelcomeText;
    private SignInButton mSignInButton;

    public login() {
        // Required empty public constructor
    }

    // todo: remove if no use found
    /*
    public static login newInstance() {
        login fragment = new login();
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return fragment;
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide the toolbar
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        } catch(NullPointerException npe) {
            Log.e("LoginFragment","Hide Action Bar",npe);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // request user id, email address, and basic profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestServerAuthCode(getString(R.string.OAuth_client_ID, false))
                .requestProfile()
                .build();

        // create GoogleApiClient object
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity() /* Fragment Activity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // register button
        mSignInButton = (SignInButton) view.findViewById(R.id.loginButton);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);
        mSignInButton.setScopes(gso.getScopeArray());

        // register other elements
        mMainLogo = (ImageView) view.findViewById(R.id.mainLogo);
        mProfileImage = (CircleImageView) view.findViewById(R.id.profilePicture);
        mWelcomeText = (TextView) view.findViewById(R.id.welcomeText);


        // register OnClickListener
        view.findViewById(R.id.loginButton).setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //connects client to google play services
        //if(mGoogleApiClient != null)
        //    mGoogleApiClient.connect();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if(opr.isDone()) {
            // if the users cached creds are still valid, OptionalPendingResult will be done and sign in
            // is available instantly
            Log.d(TAG,"Got cached sign in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } /*else {
            // if the user has not previously signed in on this device, or the sign-in has expired
            // this async branch with attempt to sign in the user silently
            // cross device single sign-on will occur in this branch
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();

        mGoogleApiClient.stopAutoManage(getActivity());

        //disconnects client to google play services
        //if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
        //    mGoogleApiClient.disconnect();
    }

    /**
     *
     * @param   requestCode integer code corresponding to request
     * @param   resultCode  integer code indicating result
     * @param   data        operation intent data
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result from launching sign-in api intent
        if(requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Log the result
        Log.d(TAG, "onConnectionFailed" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.loginButton:
                // execute sign in
                signIn();
                break;
            // ...
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()) {
            // sign in success, show authed UI
            GoogleSignInAccount account = result.getSignInAccount();
            mPersonName = account.getDisplayName();
            mPersonPhoto = account.getPhotoUrl();
            mOauth2Token = account.getServerAuthCode();

            Log.i(TAG, mOauth2Token);

            /* todo: Pass Token To Backend Server using HTTPS POST */
            Ion.with(getContext())
                    .load(getString(R.string.magpie_server_login))
                    .setBodyParameter("code", mOauth2Token)
                    .asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            /* todo: check result */
                        }
                    });
            /* end post request */

            updateUI(true);
        } else {
            // show un-authed UI
            updateUI(false);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                updateUI(false);
            }
        });
    }

    private void showProgressDialog() {
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        View view = getView();

        if(signedIn) {

            // set un-authed elements invisible
            mSignInButton.setVisibility(View.GONE);
            mMainLogo.setVisibility(View.GONE);


            mWelcomeText.setText(getString(R.string.Welcome, mPersonName.split("\\s+")));

            // Load image using Picasso
            Picasso.with(getContext())
                    .load(mPersonPhoto.toString())
                    .into(mProfileImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            mProfileImage.setVisibility(View.VISIBLE);
                            mWelcomeText.setVisibility(View.VISIBLE);

                            // drop profile picture in with animation
                            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.image_zoom);
                            mProfileImage.startAnimation(animation);

                        }

                        @Override
                        public void onError() {
                            // display some sort of error

                        }
                    });


        } else {

            // set authed elements invisible
            mProfileImage.setVisibility(View.GONE);
            mWelcomeText.setVisibility(View.GONE);

            // set un-authed elements visible
            mSignInButton.setVisibility(View.VISIBLE);
            mMainLogo.setVisibility(View.VISIBLE);

        }
    }

}


