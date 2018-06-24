package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBAdapter;
import com.example.hoangcongtuan.quanlylichhoc.utils.LoadFeedHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.LoadSearchPostResultHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hoangcongtuan on 9/6/17.
 */

public class TBChungFragment extends Fragment implements RVTBAdapter.ILoadMoreCallBack {
    private static final String TAG = TBChungFragment.class.getName();
    private RecyclerView recyclerView;
    private RVTBAdapter tbChungAdapter;
    private ImageView img_empty_state;
    private LoadSearchPostResultHelper searchPostResultHelper;
    private LoadFeedHelper loadFeedHelper;
    //if hash not null, scroll to new feed has hashkey == hash
    private String hash;
    private boolean isScrollTo = false;
    private boolean isEmptyState = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup;
        viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_tb_chung, container, false);
        return  viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isScrollTo) {
            loadFeedHelper.scrollTo(hash);
            isScrollTo = false;
        }

        if (isEmptyState) {
            img_empty_state.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
        else {
            img_empty_state.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupWidget();
        //get tb chung ref
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tbChungRef = database.child("chung/data/");
        loadFeedHelper = new LoadFeedHelper(tbChungAdapter, tbChungRef, this);
        loadFeedHelper.loadFirstTime();

    }

    private void setupWidget() {
        recyclerView = getView().findViewById(R.id.rvTBChung);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        img_empty_state = getView().findViewById(R.id.img_empty_state);


        tbChungAdapter = new RVTBAdapter(recyclerView, getContext());
        recyclerView.setAdapter(tbChungAdapter);
    }

    public void scrollTo(String hash) {
        this.hash = hash;
        this.isScrollTo = true;
    }

    // must use flag variable because this function would be called on mainActivity, when this fragment not created yet
    public void show_empty_state() {
        isEmptyState = true;
    }

    public void hide_empty_state() {
        isEmptyState = false;
    }


    public void searchPost(String text) {
//        JsonRequest jsonRequest = new JsonArrayRequest(MainActivity.FIND_URL + text, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                //Log.d(TAG, "onResponse: JSON = " + response.toString());
//                //json array to array list
//                ArrayList<String> arr_post_key = new ArrayList<>();
//                try {
//                    for (int i = 0; i < response.length(); i++)
//                        arr_post_key.add(response.get(i).toString());
//                }
//                catch (JSONException e) {
//                        e.printStackTrace();
//                }
//                finally {
//                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//                    DatabaseReference tbChungRef = database.child("chung/data/");
//
//                    searchPostAdapter = new RVTBAdapter(recyclerView, getContext());
//                    recyclerView.setAdapter(searchPostAdapter);
//                    searchPostResultHelper = new LoadSearchPostResultHelper(searchPostAdapter,
//                            tbChungRef, TBChungFragment.this, arr_post_key);
//
//                    searchPostResultHelper.loadFirstTime();
//                }
//
//                Log.d(TAG, "onResponse: json lenght = " + response.length());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, "onErrorResponse: " + error.getMessage());
//            }
//        });
//        Utils.VolleyUtils.getsInstance(this.getActivity()).getRequestQueue().add(jsonRequest);
    }

    @Override
    public void onLoadMore() {
        //Toast.makeText(getContext(), "Load more", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onLoadMore: ");
    }

    @Override
    public void onLoadMoreFinish() {
       // Toast.makeText(getContext(), "Load more Finsh", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onLoadMoreFinish: ");
    }

}
