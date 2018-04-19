package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.main.MainActivity;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVPostAdapter;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBAdapter;
import com.example.hoangcongtuan.quanlylichhoc.utils.LoadSearchPostResultHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity implements RVTBAdapter.ILoadMoreCallBack {

    private final static String TAG = SearchResultActivity.class.getName();
    private TextView tvNumResult;
    private RecyclerView recyclerView;
    private FloatingActionButton fabTop;
    private RVPostAdapter rvPostAdapter;
    private CoordinatorLayout layout_search_result;
    private LoadSearchPostResultHelper searchPostResultHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        init();
        initWidget();

        search_first_time();
    }

    private void init() {
    }

    private void initWidget() {
        tvNumResult = findViewById(R.id.tv_result_count);
        recyclerView = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchResultActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        fabTop = findViewById(R.id.fab_top);
        layout_search_result = findViewById(R.id.layout_search_result);
        rvPostAdapter = new RVPostAdapter(recyclerView, SearchResultActivity.this);

        recyclerView.setAdapter(rvPostAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.search_activity));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void search_first_time() {
        //get data from intent
        Intent intent = getIntent();
        int category = intent.getIntExtra("CATEGORY", 0);
        String text = intent.getStringExtra("TEXT");
        searchPost(text, category);
        Log.d(TAG, "onCreate: ");
    }

    public void searchPost(String text, int category) {
        String url = "";
        switch (category) {
            case MainActivity.PAGE_TB_CHUNG:
                url = MainActivity.FIND_URL + text;
                break;
            case MainActivity.PAGE_TB_HP:
                break;
            case MainActivity.PAGE_TKB:
                break;
        }

        JsonRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //Log.d(TAG, "onResponse: JSON = " + response.toString());
                //json array to array list
                ArrayList<String> arr_post_key = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++)
                        arr_post_key.add(response.get(i).toString());
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                finally {
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference tbChungRef = database.child("chung/data/");

                    tvNumResult.setText(String.valueOf(arr_post_key.size()));
                    searchPostResultHelper = new LoadSearchPostResultHelper(rvPostAdapter,
                            tbChungRef, SearchResultActivity.this, arr_post_key);
                    searchPostResultHelper.loadFirstTime();
                }

                Log.d(TAG, "onResponse: json lenght = " + response.length());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        });
        Utils.VolleyUtils.getsInstance(getApplicationContext()).getRequestQueue().add(jsonRequest);
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onLoadMoreFinish() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }
}
