package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 9/22/17.
 */

public class StepPagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> mFragmentList = new ArrayList<>();
    ArrayList<String> mFragmentTitleList = new ArrayList<>();

    public StepPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public String getFragmentTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentTitleList.add(title);
        mFragmentList.add(fragment);
    }


}
