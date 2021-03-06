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
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {


    private static final int NUM_PAGES = 4;
    private ViewPager mPager;
    private PagerAdapter mAdapter;


    public ViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_view_pager, container, false);
        mPager = (ViewPager)viewGroup.findViewById(R.id.pager);
        mAdapter = new ViewPagerAdapter(getFragmentManager());
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.setAdapter(mAdapter);

        setHeader();

        return viewGroup;
    }

    private void setHeader() {
        TextView header = (TextView) getActivity().findViewById(R.id.headerTitle);
        header.setText("  Magpie");
    }

    /**
     * ViewPagerAdapter
     * */
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position) {
                case 0:
                    return new login();
                case 1:
                    return new UsageOne();
                case 2:
                    return new UsageTwo();
                default:
                    return new UsageThree();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
