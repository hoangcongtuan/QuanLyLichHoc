package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.LVTKBieuAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.CustomDialogBuilderLopHP;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class EditHPActivity extends AppCompatActivity implements View.OnClickListener{

    FloatingActionButton fabAdd;
    ListView lvTKB;
    LVTKBieuAdapter lvtkBieuAdapter;
    ArrayList<LopHP> lstLopHP;
    ArrayList<String> lstMaHP;
    ArrayList<String> lstMaHPOld;
    Toolbar toolbar;

    DatabaseReference dbUserMaHocPhan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hp);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.hoc_phan_act_title));

        init();
        getWidgets();
        setWidgets();
        setWidgetsEvent();

    }

    private void init() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        dbUserMaHocPhan = database.child("userInfo").child(user.getUid()).child("listMaHocPHan");
        lstLopHP = DBLopHPHelper.getsInstance().getListUserLopHP();
        lstMaHP = DBLopHPHelper.getsInstance().getListUserMaHP();
        lstMaHPOld = DBLopHPHelper.getsInstance().getListUserMaHP();
        lvtkBieuAdapter = new LVTKBieuAdapter(this, android.R.layout.simple_list_item_1, lstLopHP);

    }

    private void getWidgets() {
        lvTKB = (ListView) findViewById(R.id.lvTKB);
        fabAdd = (FloatingActionButton)findViewById(R.id.fabAdds);
    }

    private void setWidgets() {
        lvTKB.setAdapter(lvtkBieuAdapter);

    }

    private void setWidgetsEvent() {
        registerForContextMenu(lvTKB);
        fabAdd.setOnClickListener(this);
    }
    public void showAddLopHPDialog() {
        final CustomDialogBuilderLopHP customDialogBuilderLopHP = new CustomDialogBuilderLopHP(this);
        customDialogBuilderLopHP.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        customDialogBuilderLopHP.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addLopHP(customDialogBuilderLopHP.getCurrentLopHP().getMaHP());
            }
        });

        AlertDialog alertDialog = customDialogBuilderLopHP.create();
        alertDialog.show();
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

        lvtkBieuAdapter.notifyDataSetChanged();
    }

    private int getIndexOf(String maHP) {
        for (int i = 0; i < lstLopHP.size(); i++) {
            if (lstLopHP.get(i).maHP.equals(maHP)) {
                return i;
            }
        }
        return -1;
    }

    private void writelstHPtoLocalDB() {
        //clear old data
        DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
        for (String s : lstMaHP)
            DBLopHPHelper.getsInstance().insertUserMaHocPhan(s);
    }

    private void writelstHPtoFirebaseDB() {
        dbUserMaHocPhan.setValue(lstMaHP);
    }

    private void updateSubscribeTopic() {
        Utils.QLLHUtils.getsInstance(this).unSubscribeAllTopics(lstMaHPOld);
        Utils.QLLHUtils.getsInstance(this).subscribeTopic(lstMaHP);
        Utils.QLLHUtils.getsInstance(this).subscribeTopic("TBChungFragment");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_hp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_save:
                writelstHPtoLocalDB();
                //write to firebase DB
                writelstHPtoFirebaseDB();

                //update subscribe
                updateSubscribeTopic();
                setResult(RESULT_OK);
                finish();
                break;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tkb_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_remove:
                lstLopHP.remove(info.position);
                lstMaHP.remove(info.position);
                lvtkBieuAdapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdds:
                showAddLopHPDialog();
                break;
        }
    }
}
