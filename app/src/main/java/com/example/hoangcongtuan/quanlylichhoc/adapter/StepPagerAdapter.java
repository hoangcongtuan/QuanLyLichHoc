package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 9/22/17.
 */

public class StepPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> lstFragment = new ArrayList<>();
    private ArrayList<String> lstFragmentTitle = new ArrayList<>();

    public StepPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        lstFragmentTitle.add(title);
        lstFragment.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return lstFragment.get(position);
    }

    @Override
    public int getCount() {
        return lstFragment.size();
    }

    public String getFragmentTitle(int position) {
        return lstFragmentTitle.get(position);
    }


}
