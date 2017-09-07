package com.example.hoangcongtuan.quanlylichhoc;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.hoangcongtuan.quanlylichhoc.adapter.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity {
    ViewPagerAdapter pagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    String[] strTabs;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        strTabs = getResources().getStringArray(R.array.tab_name);
        setupViewPager(viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new TBHocPhan(), strTabs[0]);
        pagerAdapter.addFragment(new TBChung(), strTabs[1]);
        pagerAdapter.addFragment(new LichHoc(), strTabs[2]);
        viewPager.setAdapter(pagerAdapter);
    }

}
