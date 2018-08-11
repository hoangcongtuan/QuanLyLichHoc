package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
 * Java code cua fragment thong bao lop hoc phan
 */

public class TBHocPhanFragment extends Fragment implements RVPostAdapter.ILoadMoreCallBack {
    private final static String TAG = TBHocPhanFragment.class.getName();
    private RecyclerView recyclerView;
    private LoadFeedHelper loadPostHelper;
    private ImageView img_empty_state;
    private String hash;
    private boolean isScrollTo = false;
    private boolean isEmptyState = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tb_hocphan, container, false);

        recyclerView = rootView.findViewById(R.id.rvTBHocPhan);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RVPostAdapter hocPhanAdapter = new RVPostAdapter(recyclerView, getContext());
        img_empty_state = rootView.findViewById(R.id.img_empty_state);
        recyclerView.setAdapter(hocPhanAdapter);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tbHPhanRef = database.child("lop_hoc_phan/data/");

        loadPostHelper = new LoadFeedHelper(hocPhanAdapter, tbHPhanRef, this);
        return  rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadPostHelper.loadFirstTime();
    }

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

    // must use flag variable because this function would be called on mainActivity, when this fragment not created yet
    public void show_empty_state() {
        isEmptyState = true;
    }

    public void hide_empty_state() {
        isEmptyState = false;
    }

    public void scrollTo(String hash) {
        this.hash = hash;
        this.isScrollTo = true;
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onLoadMoreFinish() {

    }
}
