package com.ruppal.orbz.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by jchavando on 7/13/17.
 */

public class SongPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] {"Search", "Playlist", "Local"};
    private Context context;
    public static ArrayList<SongListFragment> mFragmentReferences = new ArrayList<>();

    public SongPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            SearchFragment firstFrag = new SearchFragment();
            mFragmentReferences.add(0, firstFrag);
            return firstFrag;
            //return SearchFragment.newInstance(0, "Page #1");
        } else if (position == 1) {
            PlaylistFragment secondFrag = new PlaylistFragment();
            mFragmentReferences.add(1, secondFrag);
            return secondFrag;
            //more else if's
        } else if (position == 2) {
            LocalListFragment thirdFrag = new LocalListFragment();
            mFragmentReferences.add(2, thirdFrag);
            return thirdFrag;
        } else {
            return null;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}