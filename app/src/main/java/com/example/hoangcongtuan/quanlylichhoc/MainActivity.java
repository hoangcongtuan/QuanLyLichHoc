package com.example.hoangcongtuan.quanlylichhoc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.hoangcongtuan.quanlylichhoc.adapter.ViewPagerAdapter;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final static String TAG = MainActivity.class.getName();

    ViewPagerAdapter pagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    String[] strTabs;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();


        viewPager = (ViewPager)findViewById(R.id.viewPager);
        strTabs = getResources().getStringArray(R.array.tab_name);
        setupViewPager(viewPager);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        navigationView = (NavigationView)findViewById(R.id.navigaionView);
        navigationView.setNavigationItemSelectedListener(this);

        //get Firebase photo url
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        Uri url = user.getPhotoUrl();
        TextView tvUserName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.tvUserName);
        tvUserName.setText(user.getDisplayName());
        ImageRequest avatarRequest = new ImageRequest(url.toString(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        Log.d(TAG, "onResponse: bit map get");
                        ImageView imageView = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.imgAvatar);
                        imageView.setImageBitmap(response);
                    }
                },
                0, 0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: error");
                    }
                }

        );

        Utils.VolleyUtils.getsInstance(getApplicationContext()).getRequestQueue().add(avatarRequest);

    }

    private void setupViewPager(ViewPager viewPager) {
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(new TBHocPhan(), strTabs[0]);
        pagerAdapter.addFragment(new TBChung(), strTabs[1]);
        pagerAdapter.addFragment(new LichHoc(), strTabs[2]);

        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_dang_xuat:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

        }
        drawerLayout.closeDrawers();
        return true;
    }
}
