package com.example.hoangcongtuan.quanlylichhoc.activity.setup;

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

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.LVTKBieuAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.CustomDialogBuilderLopHP;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/13/17.
 */

public class FinishFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = FinishFragment.class.getName();

    ListView lvTKB;
    Button btnAdd;

    private LVTKBieuAdapter LVTKBieuAdapter;
    private ArrayList<LopHP> lstLopHP;
    private ArrayList<String> lstMaHP;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finish, container, false);

        //Log.d(TAG, "onCreateView: " );
        getWidgets(rootView);
        setWidget();
        setWidgetEvent();

        return rootView;
    }


    private void init() {
        lstLopHP = new ArrayList<>();
        LVTKBieuAdapter = new LVTKBieuAdapter(getActivity(), android.R.layout.simple_list_item_1, lstLopHP);
        lstMaHP = new ArrayList<>();
    }

    private void getWidgets(View rootView) {
        lvTKB = (ListView)rootView.findViewById(R.id.lvTKB);
        btnAdd = (Button)rootView.findViewById(R.id.btnAdd);
    }

    private void setWidget() {
        lvTKB.setAdapter(LVTKBieuAdapter);
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
                lstLopHP.remove(position);
                lstMaHP.remove(position);
                LVTKBieuAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_edit:
                break;
        }
        return super.onContextItemSelected(item);
    }

    //tao thoi khoa bieu tu danh sach ma hoc phan da nhan dang duoc
    public void processTKB(ArrayList<String> listMaHP) {

        //Toast.makeText(getContext(), "listMaHP.size() = " + listMaHP.size(), Toast.LENGTH_SHORT).show();
        //xoa danh sach cu
        lstLopHP.clear();
        lstMaHP.clear();

        for (String maHP : listMaHP) {
            LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(maHP);
            if (lopHP != null){
                lstMaHP.add(lopHP.getMaHP());
                lstLopHP.add(lopHP);
            }
        }
        //Toast.makeText(getContext(), "lstLopHP.size() = " + lstLopHP.size(), Toast.LENGTH_SHORT).show();
        LVTKBieuAdapter.notifyDataSetChanged();
    }


    public void writelstMaHPtoUserDB(DatabaseReference dbUserMaHocPhan) {
        //write to SQLite DB
        for (String s : lstMaHP) {
            DBLopHPHelper.getsInstance().insertUserMaHocPhan(s);
        }

        //write to FirebaseDB
        dbUserMaHocPhan.setValue(lstMaHP);

        //subscribe a topics
        Utils.QLLHUtils.getsInstance(getActivity()).subscribeTopic(lstMaHP);
        Utils.QLLHUtils.getsInstance(getActivity()).subscribeTopic("TBChung");

    }

    public void addLopHP(String maHP) {
        LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(maHP);
        if (lopHP == null){
            return;
        }
        int index = getIndexOf(maHP);
        if (index == -1) {
            lstLopHP.add(lopHP);
            lstMaHP.add(maHP);
        } else {
            lstLopHP.set(index, lopHP);
            lstMaHP.set(index, maHP);
        }

        LVTKBieuAdapter.notifyDataSetChanged();
    }

    public void removeLopHP(String maHP) {
        int index = getIndexOf(maHP);
        if (index == -1) {
            Log.d(TAG, "removeLopHP: don't have " + maHP);
        } else {
            lstLopHP.remove(index);
            lstMaHP.remove(index);
        }
    }

    private int getIndexOf(String maHP) {
        for (int i = 0; i < lstLopHP.size(); i++) {
            if (lstLopHP.get(i).maHP.equals(maHP)) {
                return i;
            }
        }
        return -1;
    }

    public void showAddLopHPDialog() {
        final CustomDialogBuilderLopHP customDialogBuilderLopHP = new CustomDialogBuilderLopHP(getContext());
        customDialogBuilderLopHP.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        customDialogBuilderLopHP.setPositiveButton("Thêm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addLopHP(customDialogBuilderLopHP.getCurrentLopHP().getMaHP());
            }
        });

        AlertDialog alertDialog = customDialogBuilderLopHP.create();
        alertDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAdd:
                showAddLopHPDialog();
                break;
        }
    }
}
