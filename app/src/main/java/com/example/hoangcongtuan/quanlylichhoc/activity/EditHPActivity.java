package com.example.hoangcongtuan.quanlylichhoc.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVHPhanAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.CustomDialogBuilderLopHP;
import com.example.hoangcongtuan.quanlylichhoc.exception.AppException;
import com.example.hoangcongtuan.quanlylichhoc.helper.RecyclerItemTouchHelper;
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

public class EditHPActivity extends AppCompatActivity implements View.OnClickListener
        , RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private FloatingActionButton fabAdd;
    private RecyclerView rvTKB;
    private RVHPhanAdapter rvHPhanAdapter;
    private Boolean modified = false;
    private CoordinatorLayout editHPLayout;

    private DatabaseReference dbUserMaHocPhan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_hp);

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
        ArrayList<LopHP> lstLopHP = DBLopHPHelper.getsInstance().getListUserLopHP();
        rvHPhanAdapter = new RVHPhanAdapter(this, lstLopHP);
    }

    private void getWidgets() {
        rvTKB = findViewById(R.id.rvTKB);
        fabAdd = findViewById(R.id.fabAdds);
        editHPLayout = findViewById(R.id.editHPLayout);
    }

    private void setWidgets() {
        rvTKB.setAdapter(rvHPhanAdapter);
        rvTKB.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvTKB);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTKB.getContext(),
                ((LinearLayoutManager)rvTKB.getLayoutManager()).getOrientation());
        rvTKB.addItemDecoration(dividerItemDecoration);

    }

    public void showNoInternetMessage() {
        Snackbar.make(editHPLayout,
                getResources().getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                }).show();
    }

    private void setWidgetsEvent() {
        registerForContextMenu(rvTKB);
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
                else if (Utils.InternetUitls.getsInstance(getApplicationContext()).isNetworkConnected()) {
                    addUserHP(customDialogBuilderLopHP.getCurrentLopHP().getMaHP());
                }
                else
                    showNoInternetMessage();

            }
        });



        AlertDialog alertDialog = customDialogBuilderLopHP.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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

    public void addUserHP(final String id) {
        //add on firebase
        ArrayList<String> tmp = new ArrayList<>();
        ArrayList<String> lstMaHP = DBLopHPHelper.getsInstance().getListUserMaHP();
        tmp.addAll(lstMaHP);
        tmp.add(id);
        dbUserMaHocPhan.setValue(tmp).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(
                        editHPLayout,
                        getResources().getString(R.string.add_hp_success),
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(
                                R.string.undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        undo_addUserLopHP(id);
                                    }
                                }
                        ).show();
                //insert to local db
                DBLopHPHelper.getsInstance().insertUserMaHocPhan(id);

                //subscrible topic
                Utils.QLLHUtils.getsInstance(getApplicationContext()).subscribeTopic(id);

                modified = true;

                //update ui
                rvHPhanAdapter.addItem(DBLopHPHelper.getsInstance().getLopHocPhan(id));
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                Snackbar.make(
                        editHPLayout,
                        getResources().getString(R.string.add_hp_failed),
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                       addUserHP(id);

                                    }
                                }).show();
            }
        });
    }

    public void undo_addUserLopHP(final String id) {
        ArrayList<String> tmp = new ArrayList<>();
        final ArrayList<String> lstMaHP = DBLopHPHelper.getsInstance().getListUserMaHP();
        tmp.addAll(lstMaHP);
        tmp.remove(
                tmp.indexOf(id)
        );
        //remove on firebase DB
        dbUserMaHocPhan.setValue(tmp).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //delete in local db
                DBLopHPHelper.getsInstance().deleteUserMaHocPhan(id);

                //unbsubscrible topic
                Utils.QLLHUtils.getsInstance(getApplicationContext()).unSubscribeTopic(id);

                //update flag
                modified = true;

                //update ui
                try {
                    rvHPhanAdapter.removeItem(id);
                } catch (AppException e) {
                    e.printStackTrace();
                    Toast.makeText(EditHPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                Snackbar.make(
                        editHPLayout,
                        getResources().getString(R.string.undo_failed),
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        });

    }

    public void undo_removeUserLopHP(final String id) {
        //add on firebase
        ArrayList<String> tmp = new ArrayList<>();
        ArrayList<String> lstMaHP = DBLopHPHelper.getsInstance().getListUserMaHP();
        tmp.addAll(lstMaHP);
        tmp.add(id);
        dbUserMaHocPhan.setValue(tmp).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //insert to local db
                DBLopHPHelper.getsInstance().insertUserMaHocPhan(id);

                //subscrible topic
                Utils.QLLHUtils.getsInstance(getApplicationContext()).subscribeTopic(id);

                modified = true;

                //update ui
                rvHPhanAdapter.addItem(DBLopHPHelper.getsInstance().getLopHocPhan(id));
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                Snackbar.make(
                        editHPLayout,
                        getResources().getString(R.string.undo_failed),
                        Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }

    public void removeUserHP(final String id) {
        ArrayList<String> tmp = new ArrayList<>();
        final ArrayList<String> lstMaHP = DBLopHPHelper.getsInstance().getListUserMaHP();
        tmp.addAll(lstMaHP);
        tmp.remove(
                tmp.indexOf(id)
        );
        //remove on firebase DB
        dbUserMaHocPhan.setValue(tmp).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Snackbar.make(
                        editHPLayout,
                        getResources().getString(R.string.remove_hp_success),
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undo_removeUserLopHP(id);
                            }
                        }).show();
                //delete in local db
                DBLopHPHelper.getsInstance().deleteUserMaHocPhan(id);

                //unbsubscrible topic
                Utils.QLLHUtils.getsInstance(getApplicationContext()).unSubscribeTopic(id);

                //update flag
                modified = true;

                //update ui
                try {
                    rvHPhanAdapter.removeItem(id);
                } catch (AppException e) {
                    e.printStackTrace();
                    Toast.makeText(EditHPActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();

                }

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull final Exception e) {
                Snackbar.make(
                        editHPLayout,
                        getResources().getString(R.string.remove_hp_failed),
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        removeUserHP(id);

                                    }
                                }).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdds:
                showAddLopHPDialog();
                break;
        }
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RVHPhanAdapter.ViewHolder) {
            final LopHP lopHP = rvHPhanAdapter.getItem(position);
            removeUserHP(lopHP.getMaHP());
        }
    }
}
