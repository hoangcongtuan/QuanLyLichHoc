package com.example.hoangcongtuan.quanlylichhoc;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.adapter.TKBAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.CustomDialogBuilderLopHP;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/13/17.
 */

public class FinishFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = FinishFragment.class.getName();

    ListView lvTKB;
    Button btnAdd;

    private TKBAdapter tkbAdapter;
    private ArrayList<LopHP> listLopHP;
    private ArrayList<String> lstMaHP;
    private AlertDialog alertDialog;
    private CustomDialogBuilderLopHP customDialogBuilderLopHP;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finish, container, false);

        Log.d(TAG, "onCreateView: " );
        getWidgets(rootView);
        setWidget();
        setWidgetEvent();

        return rootView;
    }

    public void initAlertDialog() {
        customDialogBuilderLopHP.updateData();
    }

    private void init() {

        listLopHP = new ArrayList<>();
        tkbAdapter = new TKBAdapter(getActivity(), android.R.layout.simple_list_item_1, listLopHP);
        lstMaHP = new ArrayList<>();
        customDialogBuilderLopHP = new CustomDialogBuilderLopHP(getContext());
        customDialogBuilderLopHP.setNegativeButton("Huy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        customDialogBuilderLopHP.setPositiveButton("Them", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addLopHP(customDialogBuilderLopHP.getCurrentLopHP().getMaHP());
            }
        });

        //alertDialog = customDialogBuilderLopHP.create();

    }

    private void getWidgets(View rootView) {

        lvTKB = (ListView)rootView.findViewById(R.id.lvTKB);
        btnAdd = (Button)rootView.findViewById(R.id.btnAdd);

    }

    private void setWidget() {

        lvTKB.setAdapter(tkbAdapter);
        registerForContextMenu(lvTKB);

    }

    private void setWidgetEvent() {

        btnAdd.setOnClickListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.tkb_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        int id = item.getItemId();
        switch (id){
            case R.id.menu_remove:
                listLopHP.remove(position);
                lstMaHP.remove(position);
                tkbAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_edit:
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void processTKB(ArrayList<String> listMaHP) {

        Toast.makeText(getContext(), "listMaHP.size() = " + listMaHP.size(), Toast.LENGTH_SHORT).show();
        listLopHP.clear();

        for (String maHP : listMaHP) {

            LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(maHP);
            if (lopHP != null){
                lstMaHP.add(lopHP.getMaHP());
                listLopHP.add(lopHP);
            }
        }

        Toast.makeText(getContext(), "listLopHP.size() = " + listLopHP.size(), Toast.LENGTH_SHORT).show();
        tkbAdapter.notifyDataSetChanged();
    }

    public void writelstMaHPtoUserDB() {
        for (String s : lstMaHP) {
            DBLopHPHelper.getsInstance().insertUserMaHocPhan(s);
        }
    }

    public void addLopHP(String maHP) {
        LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(maHP);
        if (lopHP == null){
            return;
        }
        int index = getIndexOf(maHP);
        if (index == -1) {
            listLopHP.add(lopHP);
            lstMaHP.add(maHP);
        } else {
            listLopHP.set(index, lopHP);
            lstMaHP.add(maHP);
        }

        tkbAdapter.notifyDataSetChanged();
    }

    public void removeLopHP(String maHP) {
        int index = getIndexOf(maHP);
        if (index == -1) {
            Log.d(TAG, "removeLopHP: don't have " + maHP);
        } else {
            listLopHP.remove(index);
            lstMaHP.remove(index);
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                customDialogBuilderLopHP.create().show();
                break;
        }
    }
}
