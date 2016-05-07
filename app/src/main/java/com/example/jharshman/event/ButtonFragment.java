package com.example.jharshman.event;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ButtonFragment.OnButtonFragmentInteraction} interface
 * to handle interaction events.
 */
public class ButtonFragment extends Fragment implements View.OnClickListener {

    /**
     * Listener listening for fragment interaction
     */
    private OnButtonFragmentInteraction mListener;

    public ButtonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_button, container, false);

        // set the text result
        Bundle arguments = getArguments();
        if(arguments != null) {
            // get text from bundle
            String code = arguments.getString("code");

            // set text on text view
            TextView scanText = (TextView) view.findViewById(R.id.scan_result_text);
            scanText.setText(code);
        }

        // set on click listener scan button
        Button scanButton = (Button) view.findViewById(R.id.scan_button);
        scanButton.setOnClickListener(this);

        return view;
    }

    /**
     * Respond to view clicks
     *
     * @param view The view that was clicked
     */
    @Override
    public void onClick(View view) {

        // notify listener that button was clicked
        if(view.getId() == R.id.scan_button && mListener != null) {
            mListener.onButtonFragmentInteraction(true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnButtonFragmentInteraction) {
            mListener = (OnButtonFragmentInteraction) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Button Fragment Interaction Listener
     */
    public interface OnButtonFragmentInteraction {

        /**
         * Handle button click
         *
         * @param clicked Button was clicked
         */
        void onButtonFragmentInteraction(boolean clicked);
    }
}
