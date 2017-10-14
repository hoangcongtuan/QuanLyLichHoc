package com.example.hoangcongtuan.quanlylichhoc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.adapter.TKBAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/13/17.
 */

public class FinishFragment extends Fragment {
    private final static String TAG = FinishFragment.class.getName();

    ListView lvTKB;
    Button btnAdd;
    Button btnRemove;

    private TKBAdapter tkbAdapter;
    private ArrayList<LopHP> listLopHP;

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

        listLopHP = new ArrayList<>();
        tkbAdapter = new TKBAdapter(getActivity(), android.R.layout.simple_list_item_1, listLopHP);

    }

    private void getWidgets(View rootView) {

        lvTKB = (ListView)rootView.findViewById(R.id.lvTKB);
        btnAdd = (Button)rootView.findViewById(R.id.btnAdd);
        btnRemove = (Button)rootView.findViewById(R.id.btnRemove);

    }

    private void setWidget() {

        lvTKB.setAdapter(tkbAdapter);

    }

    private void setWidgetEvent() {

    }

    public void processTKB(ArrayList<String> listMaHP) {

        Toast.makeText(getContext(), "listMaHP.size() = " + listMaHP.size(), Toast.LENGTH_SHORT).show();
        listLopHP.clear();

        for (String maHP : listMaHP) {

            LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(maHP);
            if (lopHP != null){
                listLopHP.add(lopHP);
            }
        }

        Toast.makeText(getContext(), "listLopHP.size() = " + listLopHP.size(), Toast.LENGTH_SHORT).show();
        tkbAdapter.notifyDataSetChanged();
    }
    public void addLopHP(String maHP) {
        LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(maHP);
        if (lopHP == null){
            return;
        }
        int index = getIndexOf(maHP);
        if (index == -1) {
            listLopHP.add(lopHP);
        } else {
            listLopHP.set(index, lopHP);
        }

        tkbAdapter.notifyDataSetChanged();
    }

    public void removeLopHP(String maHP) {
        int index = getIndexOf(maHP);
        if (index == -1) {
            Log.d(TAG, "removeLopHP: don't have " + maHP);
        } else {
            listLopHP.remove(index);
        }
    }

    private int getIndexOf(String maHP) {
        for (int i = 0; i < listLopHP.size(); i++) {
            if (listLopHP.get(i).maHP.equals(maHP)) {
                return i;
            }
        }
        return -1;
    }



}
