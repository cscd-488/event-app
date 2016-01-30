package com.example.jharshman.event;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {

    private static final int NUM_PAGES = 4;

    private PagerAdapter mAdapter;



    public ViewPagerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view_pager_fragment, container, false);
        ViewPager pager = (ViewPager)rootView.findViewById(R.id.pager);


        //set adapter
        mAdapter = new customPagerAdapter(getFragmentManager());
        pager.setAdapter(mAdapter);

        return rootView;
    }

    private class customPagerAdapter extends FragmentStatePagerAdapter {
        public customPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            // case statement
            switch (position) {
                case 0:
                    return new SliderFragment();
                case 1:
                    return new DifferentFragment();
                case 2:
                    return new SliderFragment();
                default:
                    return new SliderFragment();
            }

        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }


}
