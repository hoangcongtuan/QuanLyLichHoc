package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    Boolean modified;
    CoordinatorLayout editHPLayout;

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
        modified = false;

    }

    private void getWidgets() {
        lvTKB = (ListView) findViewById(R.id.lvTKB);
        fabAdd = (FloatingActionButton)findViewById(R.id.fabAdds);
        editHPLayout = (CoordinatorLayout)findViewById(R.id.editHPLayout);
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
                LopHP lopHP = customDialogBuilderLopHP.getCurrentLopHP();
                if (lopHP == null) {
                    Snackbar.make(editHPLayout,
                             getResources().getString(R.string.incorrect_ma_hp), Snackbar.LENGTH_LONG).show();
                }
                else
                    addUserHP(customDialogBuilderLopHP.getCurrentLopHP().getMaHP());
            }
        });

        AlertDialog alertDialog = customDialogBuilderLopHP.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (modified)
                    setResult(RESULT_OK);
                else
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

    public void addUserHP(final String id) {
        //add on firebase
        ArrayList<String> tmp = new ArrayList<>();
        tmp.addAll(lstMaHP);
        tmp.add(id);
        dbUserMaHocPhan.setValue(tmp).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(
                        editHPLayout,
                        getResources().getString(R.string.add_hp_success),
                        Snackbar.LENGTH_LONG).show();
                //insert to local db
                DBLopHPHelper.getsInstance().insertUserMaHocPhan(id);

                //subscrible topic
                Utils.QLLHUtils.getsInstance(getApplicationContext()).subscribeTopic(id);

                //update flag
                modified = true;

                //update UI
                lstLopHP.add(DBLopHPHelper.getsInstance().getLopHocPhan(id));
                lstMaHP.add(id);
                lvtkBieuAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                Snackbar.make(
                        editHPLayout,
                        getResources().getString(R.string.add_hp_failed),
                        Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.details),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(EditHPActivity.this);
                                        builder.setTitle(getResources().getString(R.string.hoc_phan_act_title));
                                        builder.setMessage(e.getMessage());
                                        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                        builder.create().show();

                                    }
                                }).show();
            }
        });
    }

    public void removeUserHP(final int position) {
        String id;
        id = lstMaHP.get(position);
        ArrayList<String> tmp = new ArrayList<>();
        tmp.addAll(lstMaHP);
        tmp.remove(position);
        //remove on firebase DB
        dbUserMaHocPhan.setValue(tmp).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(
                        editHPLayout,
                        getResources().getString(R.string.remove_hp_success),
                        Snackbar.LENGTH_LONG).show();
                //delete in local db
                DBLopHPHelper.getsInstance().deleteUserMaHocPhan(lstMaHP.get(position));

                //unbsubscrible topic
                Utils.QLLHUtils.getsInstance(getApplicationContext()).unSubscribeTopic(lstMaHP.get(position));

                //update flag
                modified = true;

                //update UI
                lstLopHP.remove(position);
                lstMaHP.remove(position);
                lvtkBieuAdapter.notifyDataSetChanged();

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                Snackbar.make(
                        editHPLayout,
                        getResources().getString(R.string.remove_hp_failed),
                        Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.details),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(EditHPActivity.this);
                                        builder.setTitle(getResources().getString(R.string.hoc_phan_act_title));
                                        builder.setMessage(e.getMessage());
                                        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                        builder.create().show();

                                    }
                                }).show();
            }
        });

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.menu_remove:
                removeUserHP(info.position);
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
