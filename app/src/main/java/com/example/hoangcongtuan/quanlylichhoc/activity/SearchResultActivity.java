package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class SearchResultActivity extends AppCompatActivity implements RVTBAdapter.ILoadMoreCallBack, LoadSearchPostResultHelper.SearchPostCallBack{

    private final static String TAG = SearchResultActivity.class.getName();
    private final static int SHOW_BTN_TOP_THRESHOLD = 3;
    private TextView tvNumResult;
    private TextView tvResult;
    private RecyclerView recyclerView;
    private FloatingActionButton fabTop;
    private RVPostAdapter rvPostAdapter;
    private CoordinatorLayout layout_search_result;

    private ImageView img_no_result;
    private ImageView img_empty_state;
    private LinearLayout layout_result;
    private LoadSearchPostResultHelper searchPostResultHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        init();
        initWidget();

        if (Utils.getsInstance(getApplicationContext()).isNetworkConnected(getApplicationContext())) {
            show_normal();
            search_first_time();
        }
        else
            show_empty_state();

    }

    private void init() {
    }

    private void initWidget() {
        tvNumResult = findViewById(R.id.tv_result_count);
        recyclerView = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchResultActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        tvResult = findViewById(R.id.tvResult);

        fabTop = findViewById(R.id.fab_top);
        fabTop.setVisibility(View.INVISIBLE);


        fabTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getApplicationContext()) {
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_START;
                    }
                };

                smoothScroller.setTargetPosition(0);
                ((LinearLayoutManager)recyclerView.getLayoutManager()).startSmoothScroll(smoothScroller);
            }
        });

        img_empty_state = findViewById(R.id.img_empty_state);
        img_no_result = findViewById(R.id.img_no_result);

        layout_search_result = findViewById(R.id.layout_search_result);
        layout_result = findViewById(R.id.layout_result);
        rvPostAdapter = new RVPostAdapter(recyclerView, SearchResultActivity.this);

        recyclerView.setAdapter(rvPostAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (((LinearLayoutManager)recyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition() > 0)
                    fabTop.show();
                else if (((LinearLayoutManager)recyclerView.getLayoutManager())
                        .findFirstVisibleItemPosition() == 0)
                    fabTop.hide();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.search_activity));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void show_no_result() {
        img_no_result.setVisibility(View.VISIBLE);
        img_empty_state.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void show_empty_state() {
        img_empty_state.setVisibility(View.VISIBLE);
        img_no_result.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void show_normal() {
        recyclerView.setVisibility(View.VISIBLE);
        img_empty_state.setVisibility(View.INVISIBLE);
        img_no_result.setVisibility(View.INVISIBLE);
    }

    private void search_first_time() {
        //get data from intent
        Intent intent = getIntent();
        int category = intent.getIntExtra("CATEGORY", 0);
        String text = intent.getStringExtra("TEXT");
//        if (getSupportActionBar() != null)
//            getSupportActionBar().setTitle(getResources().getString(R.string.search_for).concat(" \"").concat(text).concat("\""));
        tvResult.setText(getResources().getString(R.string.search_for).concat(" \"").concat(text).concat("\""));
        searchPost(text, category);
        Log.d(TAG, "onCreate: ");
    }

    public void searchPost(String text, final int category) {
        String url = "";
        switch (category) {
            case MainActivity.PAGE_TB_CHUNG:
                url = String.format(MainActivity.FIND_URL, MainActivity.CATE_CHUNG, text);
                //url = MainActivity.FIND_URL + text;
                break;
            case MainActivity.PAGE_TB_HP:
                url = String.format(MainActivity.FIND_URL, MainActivity.CATE_HOC_PHAN, text);
                break;
            case MainActivity.PAGE_TKB:
                //url = String.format(MainActivity.FIND_URL, MainActivity.CATE_CHUNG, text);
                break;
        }
        //replace space with %20
        url = url.replaceAll(" ", "%20");
        Log.d(TAG, "searchPost: " + url);

        rvPostAdapter.addThongBao(null);
        rvPostAdapter.notifyItemInserted(rvPostAdapter.getItemCount() - 1);
        rvPostAdapter.addThongBao(null);
        rvPostAdapter.notifyItemInserted(rvPostAdapter.getItemCount() - 1);

        final long startTime = System.nanoTime();

        JsonRequest jsonRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                long duration = System.nanoTime() - startTime;
                Log.d(TAG, "onResponse: Time Execute = " + duration / 1e9f);
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
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference();
                    if (category == MainActivity.PAGE_TB_CHUNG)
                        postRef = database.child("chung/data/");
                    else if (category == MainActivity.PAGE_TB_HP)
                        postRef = database.child("lop_hoc_phan/data/");

                    tvNumResult.setText(String.valueOf(arr_post_key.size()));
                    searchPostResultHelper = new LoadSearchPostResultHelper(rvPostAdapter,
                            postRef, SearchResultActivity.this, SearchResultActivity.this, arr_post_key);
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

        Utils.getsInstance(getApplicationContext()).getRequestQueue().add(jsonRequest);
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
    public void onNoResult() {
        show_no_result();
        //layout_result.setVisibility(View.GONE);
        //layout_search_result.setBackground(getResources().getDrawable(R.drawable.no_result));
    }
}
