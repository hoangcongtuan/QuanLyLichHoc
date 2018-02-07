package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBAdapter;
import com.example.hoangcongtuan.quanlylichhoc.listener.HidingScrollListener;
import com.example.hoangcongtuan.quanlylichhoc.utils.LoadFeedHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hoangcongtuan on 9/6/17.
 * Java code cua fragment thong bao lop hoc phan
 */

public class TBHocPhanFragment extends Fragment implements RVTBAdapter.ILoadMoreCallBack {

    private final static String TAG = TBHocPhanFragment.class.getName();

    private RVTBAdapter hocPhanAdapter;
    private RecyclerView recyclerView;
    private HidingScrollListener hidingScrollListener;
    private LoadFeedHelper loadFeedHelper;

    private String hash;
    private boolean isScrollTo = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = (ViewGroup)inflater.inflate(R.layout.fragment_tb_hocphan, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.rvTBHocPhan);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        int paddingTop = Utils.getToolbarHeight(getContext()) + Utils.getTabsHeight(getContext());
        recyclerView.setPadding(recyclerView.getPaddingLeft(), paddingTop, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());


        hocPhanAdapter = new RVTBAdapter(recyclerView, getContext());

        recyclerView.setAdapter(hocPhanAdapter);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tbHPhanRef = database.child("lop_hoc_phan/data/");

        loadFeedHelper = new LoadFeedHelper(hocPhanAdapter, tbHPhanRef, this);
        return  rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.addOnScrollListener(hidingScrollListener);

        loadFeedHelper.loadFirstTime();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isScrollTo) {
            loadFeedHelper.scrollTo(hash);
            isScrollTo = false;
        }
    }

    public void scrollTo(String hash) {
        this.hash = hash;
        this.isScrollTo = true;
    }

    public void setOnHidingScrollListener(HidingScrollListener hsl) {
        hidingScrollListener = hsl;
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onLoadMoreFinish() {

    }
}
