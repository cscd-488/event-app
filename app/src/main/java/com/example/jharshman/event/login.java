package com.example.jharshman.event;

/**
 * @login.java
 * @author Joshua D. Harshman
 * @date 2016 01 26
 * @date 2016 02 29
 *
 * This fragment is part of the initial log-in flow.  It is created by the ViewPager Fragment
 * and works to provide login functionality for Magpie by allowing users to sign-in using their
 * existing Google Plus accounts.  The fragment is able to fetch and forward the user's OAuth2
 * token provided by Google to the Magpie backend server(s) where it is then processed for validity.
 * This fragment will then receive a JWT token from a Magpie server as a result.  This JWT token is
 * then stored in app data to be used as a method of authentication to the server for future requests.
 * */

/* standard android libraries */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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

/* Picasso */
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/* Circle Image View */
import de.hdodenhof.circleimageview.CircleImageView;

/* OkHttp */
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/* Exceptions */
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

/**
 * This class consists of all the code needed to inflate the login fragment and provide
 * a sign-in option to the user.  It initializes, hides, and reveals UI elements based on
 * whether a user has opted to sign-in or not and based on whether that sign-in was successful.
 * */
public class login extends Fragment implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private static final String TAG = "LoginFragment";
    private static final int RC_SIGN_IN = 9001;
    private static final MediaType MEDIA_TYPE = MultipartBody.FORM;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;
    private String mOauth2Token;
    private String mPersonName;
    private Uri mPersonPhoto;

    private CircleImageView mProfileImage;
    private ImageView mMainLogo;
    private TextView mWelcomeText;
    private SignInButton mSignInButton;
    private Button mContinueButton;

    private SharedPreferences mSharedPreferences;

    public login() {
        // Required empty public constructor
    }

    /**
     * Fragment Called to do initial creation of a fragment. This is called after onAttach(Activity)
     * and before onCreateView(LayoutInflater, ViewGroup, Bundle).  Note that this can be called
     * while the fragment's activity is still in the process of being created.
     * As such, you can not rely on things like the activity's content view hierarchy being
     * initialized at this point. If you want to do work once the activity itself is created,
     * see onActivityCreated(Bundle).
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     *
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Called to have the fragment instantiate its user interface view. This is optional,
     * and non-graphical fragments can return null (which is the default implementation).
     * This will be called between onCreate(Bundle) and onActivityCreated(Bundle).
     * If you return a View from here, you will later be called in onDestroyView when the
     * view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     *
     * @return View for fragment's UI, or null
     * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        setHeader();

        // request user id, email address, and basic profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestServerAuthCode(getString(R.string.OAuth_client_ID, false))
                .build();

        // create GoogleApiClient object
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity() /* Fragment Activity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // register google sign-in button
        mSignInButton = (SignInButton) view.findViewById(R.id.loginButton);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);
        mSignInButton.setScopes(gso.getScopeArray());

        // register UI elements
        mMainLogo = (ImageView) view.findViewById(R.id.mainLogo);
        mProfileImage = (CircleImageView) view.findViewById(R.id.profilePicture);
        mWelcomeText = (TextView) view.findViewById(R.id.welcomeText);
        mContinueButton = (Button) view.findViewById(R.id.continue_button);


        // register OnClickListener
        view.findViewById(R.id.loginButton).setOnClickListener(this);

        return view;
    }

    private void setHeader() {
        TextView header = (TextView) getActivity().findViewById(R.id.headerTitle);
        header.setText("  Magpie");
    }

    /**
     * Called when the Fragment is visible to the user.
     * This is generally tied to Activity.onStart of the containing Activity's lifecycle.
     * */
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

    /**
     * Called when the Fragment is visible to the user.
     * This is generally tied to Activity.onStart of the containing Activity's lifecycle.
     * */
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
     * Receive the result from a previous call to startActivityForResult(Intent, int).
     * This follows the related Activity API as described there in Activity.onActivityResult(int, int, Intent).
     *
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     *
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

    /**
     * Implementation of onConnectionFailed in interface OnConnectionFailedListener
     *
     * @param connectionResult Result of connection
     * */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Log the result
        Log.d(TAG, "onConnectionFailed" + connectionResult);
    }

    /**
     * View.OnClickListener Called when a view has been clicked.
     * Implementation of onClick in interface OnClickListener
     *
     * @param v The view that was clicked
     * */
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

    /**
     * Handles sign in result forwarded from onActivityResult method.
     * Modifies UI based on the status of the result.
     *
     * @param result GoogleSignInResult passed from onActivityResult.
     * */
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()) {
            // sign in success, show authed UI
            GoogleSignInAccount account = result.getSignInAccount();
            mPersonName = account.getDisplayName();
            mPersonPhoto = account.getPhotoUrl();
            mOauth2Token = account.getServerAuthCode();

            Log.i(TAG, mOauth2Token);

            // make POST request
            try {
                postData();
            } catch(IOException ioe) {
                ioe.printStackTrace();
            }


            updateUI(true);
        } else {
            // show un-authed UI
            updateUI(false);
        }
    }


    /**
     * Launches GoogleSignInApi sign-in intent.
     * */
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Requests GoogleSignInApi sign-out
     * */
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                updateUI(false);
            }
        });
    }

    /**
     * Show progress dialog
     * */
    private void showProgressDialog() {
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        mProgressDialog.show();
    }

    /**
     * Hide progress dialog
     * */
    private void hideProgressDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    /**
     * Update UI to either hide or un-hide UI elements
     *
     * @param signedIn boolean value indicating sign-in status
     * */
    private void updateUI(boolean signedIn) {

        if(signedIn) {

            /* If the user is signed in,
            * 1. set un-authenticated UI elements invisible
            * 2. pull data for authenticated UI elements
            * 3. set authenticated UI elements visible */

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
                            mContinueButton.setVisibility(View.VISIBLE);

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

            /* If the user is NOT signed in,
            * 1. ensure authenticated UI elements are invisible
            * 2. set un-authenticated UI elements visible */

            mProfileImage.setVisibility(View.GONE);
            mWelcomeText.setVisibility(View.GONE);
            mContinueButton.setVisibility(View.GONE);

            mSignInButton.setVisibility(View.VISIBLE);
            mMainLogo.setVisibility(View.VISIBLE);

        }
    }

    /**
     * Leverage OkHttp to send POST data to magpie server(s)
     * Receives JWT token from server in response
     * Throws IOException
     * */
    private void postData() throws IOException {
        OkHttpClient client = new OkHttpClient();

        // create formBody
        RequestBody formBody = new MultipartBody.Builder()
                .setType(MEDIA_TYPE)
                .addFormDataPart("code", mOauth2Token)
                .build();

        // request builder
        Request request = new Request.Builder()
                .url(getString(R.string.magpie_server_login))
                .post(formBody)
                .build();

        // async call
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i(TAG, "POST exception");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.i(TAG, "Response Received");

                /* Response contains JSON token
                * Extract and save to app data */
                try {
                    String jwtToken = extractToken(response.body().string());
                    saveToSharedPreferences(jwtToken);
                } catch (JSONException je) {
                    je.printStackTrace();
                }

            }
        });

    }

    /**
     * Extract JWT token from JSON reply from server
     *
     * @param responseBody JSON string reply from server
     * */
    private String extractToken(String responseBody) throws JSONException {
        JSONObject jsonObject = new JSONObject(responseBody);
        return jsonObject.getString("token");
    }

    /**
     * Write JWT token to shared preferences
     *
     * @param jwtToken value to write to shared preferences
     * */
    private void saveToSharedPreferences(String jwtToken) {
        mSharedPreferences = this.getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(getString(R.string.jwt_server_token), jwtToken);
        editor.apply();
    }

}
