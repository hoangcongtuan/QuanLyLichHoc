package com.example.hoangcongtuan.quanlylichhoc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/13/17.
 */

public class FinishFragment extends Fragment {

    ListView lvTKB;
    Button btnAdd;
    Button btnRemove;
    ArrayList<String> lstTKB;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finish, container, false);

        getWidgets(rootView);
        setWidget();
        setWidgetEvent();


        return rootView;
    }

    private void init() {

        lstTKB = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, lstTKB);

    }

    private void getWidgets(View rootView) {

        lvTKB = (ListView)rootView.findViewById(R.id.lvTKB);
        btnAdd = (Button)rootView.findViewById(R.id.btnAdd);
        btnRemove = (Button)rootView.findViewById(R.id.btnRemove);

    }

    private void setWidget() {

        lvTKB.setAdapter(adapter);

    }

    private void setWidgetEvent() {

    }

    public void processTKB(ArrayList<String> arrayList) {

        lstTKB.clear();

        for (String i : arrayList) {
            LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(i);
            if(lopHP == null)
                return;
            lstTKB.add(lopHP.getTen_hoc_phan() + " - " + lopHP.getTen_giang_vien());
        }

        adapter.notifyDataSetChanged();
    }



}
