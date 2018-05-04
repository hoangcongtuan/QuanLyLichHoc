package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVHPhanAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 9/6/17.
 */

public class LichHocFragment extends Fragment {

    private View rootView;
    private RecyclerView rvTKB;
    private RVHPhanAdapter rvTKBieuAdapter;
    private ArrayList<LopHP> lstLopHP;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lich_hoc, container, false);

        getWidgets();
        setWidgets();
        setWidgetsEvent();

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }



    private void init() {
        lstLopHP = new ArrayList<>(DBLopHPHelper.getsInstance().getListUserLopHP());
        rvTKBieuAdapter = new RVHPhanAdapter(getActivity(), lstLopHP);
    }

    private void getWidgets() {
        rvTKB = rootView.findViewById(R.id.rvTKB);

    }

    private void setWidgets() {
        rvTKB.setAdapter(rvTKBieuAdapter);

        rvTKB.setLayoutManager(new LinearLayoutManager(getActivity()));


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTKB.getContext(),
                ((LinearLayoutManager)rvTKB.getLayoutManager()).getOrientation());
        rvTKB.addItemDecoration(dividerItemDecoration);
    }

    private void setWidgetsEvent() {

    }

    public void updateUI() {
        lstLopHP.clear();
        ArrayList<LopHP> lst = DBLopHPHelper.getsInstance().getListUserLopHP();
        for(LopHP hp : lst) {
            //lstLopHP.add(hp);
            rvTKBieuAdapter.addItem(hp);
        }

        //Utils.sortLHP(lstLopHP);
        rvTKBieuAdapter.notifyDataSetChanged();
    }
}
