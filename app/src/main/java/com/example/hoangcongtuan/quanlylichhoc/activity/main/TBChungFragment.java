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
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBAdapter;
import com.example.hoangcongtuan.quanlylichhoc.listener.HidingScrollListener;
import com.example.hoangcongtuan.quanlylichhoc.utils.LoadFeedHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by hoangcongtuan on 9/6/17.
 */

public class TBChungFragment extends Fragment implements RVTBAdapter.ILoadMoreCallBack {
    private static final String TAG = TBChungFragment.class.getName();
    private RecyclerView recyclerView;
    private RVTBAdapter tbChungAdapter;
    private LoadFeedHelper loadFeedHelper;
    private HidingScrollListener hidingScrollListener;
    //if hash not null, scroll to new feed has hashkey == hash
    private String hash;
    private boolean isScrollTo = false;

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
        recyclerView.addOnScrollListener(hidingScrollListener);

        int paddingTop = Utils.getToolbarHeight(getContext()) + Utils.getTabsHeight(getContext());
        recyclerView.setPadding(recyclerView.getPaddingLeft(), paddingTop, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());

        tbChungAdapter = new RVTBAdapter(recyclerView, getContext());
        recyclerView.setAdapter(tbChungAdapter);
    }

    public void scrollTo(String hash) {
        //loadFeedHelper.scrollTo(hash);
        this.hash = hash;
        this.isScrollTo = true;
    }




    public void setOnHidingScrollListener(HidingScrollListener hsl) {
        hidingScrollListener = hsl;
    }


    @Override
    public void onLoadMore() {
        Toast.makeText(getContext(), "Load more", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadMoreFinish() {
        Toast.makeText(getContext(), "Load more Finsh", Toast.LENGTH_SHORT).show();
    }

}
