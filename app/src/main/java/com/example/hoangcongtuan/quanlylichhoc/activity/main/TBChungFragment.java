package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVPostAdapter;
import com.example.hoangcongtuan.quanlylichhoc.helper.LoadFeedHelper;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hoangcongtuan on 9/6/17.
 */

public class TBChungFragment extends Fragment implements RVPostAdapter.ILoadMoreCallBack {
    private static final String TAG = TBChungFragment.class.getName();
    private RecyclerView recyclerView;
    private RVPostAdapter tbChungAdapter;
    private ImageView img_empty_state;
    private LoadFeedHelper loadPostHelper;
    //if hash not null, scroll to new feed has hashkey == hash
    private String hash;
    private boolean isScrollTo = false;
    private boolean isEmptyState = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup;
        viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_tb_chung, container, false);
        return  viewGroup;
    }

    /**
     * Scroll to a Post that has a hash key is hash
     * If is empty state, change layout to empty state
     * @param savedInstanceState ke me no
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isScrollTo) {
            loadPostHelper.scrollTo(hash);
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupWidget();
        //get tb chung ref
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tbChungRef = database.child("chung/data/");
        loadPostHelper = new LoadFeedHelper(tbChungAdapter, tbChungRef, this);
        loadPostHelper.loadFirstTime();
    }

    private void setupWidget() {
        recyclerView = getView().findViewById(R.id.rvTBChung);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        img_empty_state = getView().findViewById(R.id.img_empty_state);
        tbChungAdapter = new RVPostAdapter(recyclerView, getContext());
        recyclerView.setAdapter(tbChungAdapter);
    }

    /**
     * Scroll hash'post
     * @param hash post'hash want to scroll to
     */
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
