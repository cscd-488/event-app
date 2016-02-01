package com.example.jharshman.event;

/* standard android libraries */
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/* facebook library */
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class login extends Fragment {

    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;

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

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // register button
        mLoginButton = (LoginButton)view.findViewById(R.id.login_facebook);

        //todo: is excluding this call impairing functionality?
        //mLoginButton.setFragment(this);

        // register callback
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("onSuccess","successful login");
            }

            @Override
            public void onCancel() {
                Log.i("onCancel","canceled login");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("onError","error login");
            }
        });

        return view;
    }

    /**
     * hands off to Facebook's Callback Manager
     *
     * @param   requestCode integer code corresponding to request
     * @param   resultCode  integer code indicating result
     * @param   data        operation intent data
     * */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}