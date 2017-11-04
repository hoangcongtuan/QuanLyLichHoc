package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.LVTKBieuAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 9/6/17.
 */

public class LichHocFragment extends Fragment {

    View rootView;
    ListView lvTKB;
    private LVTKBieuAdapter LVTKBieuAdapter;
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
        LVTKBieuAdapter = new LVTKBieuAdapter(getActivity(), android.R.layout.simple_list_item_1, lstLopHP);
    }

    private void getWidgets() {
        lvTKB = (ListView) rootView.findViewById(R.id.lvTKB);
    }

    private void setWidgets() {
        lvTKB.setAdapter(LVTKBieuAdapter);
    }

    private void setWidgetsEvent() {

    }

    public void updateUI() {
        lstLopHP.clear();
        ArrayList<LopHP> lst = DBLopHPHelper.getsInstance().getListUserLopHP();
        for(LopHP hp : lst) {
            lstLopHP.add(hp);
        }
        LVTKBieuAdapter.notifyDataSetChanged();
    }
}
