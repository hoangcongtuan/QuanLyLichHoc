package com.example.hoangcongtuan.quanlylichhoc.activity.setup;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVHPhanAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.EditMaHPCustomDialogBuilder;
import com.example.hoangcongtuan.quanlylichhoc.customview.LopHPCustomDialogBuilder;
import com.example.hoangcongtuan.quanlylichhoc.exception.AppException;
import com.example.hoangcongtuan.quanlylichhoc.helper.RecyclerItemTouchHelper;
import com.example.hoangcongtuan.quanlylichhoc.listener.RecyclerTouchListener;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/13/17.
 */

public class FinishFragment extends Fragment implements View.OnClickListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private final static String TAG = FinishFragment.class.getName();
    private  final static String TOPIC_TBCHUNG = "TBChung";
    private RecyclerView rvHPhan;
    //private FloatingActionButton fabAdd;
    private RVHPhanAdapter rvhPhanAdapter;
    private ArrayList<LopHP> lstLopHP;
    private ArrayList<String> lstMaHP;
    private CoordinatorLayout layout_setup;
    private OnUpLoadUserDBComplete onUpLoadUserDBComplete;
    private FinishFragCallBack finishFragCallBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finish, container, false);

        initWidgets(rootView);

        return rootView;
    }


    private void init() {
        lstLopHP = new ArrayList<>();
        rvhPhanAdapter = new RVHPhanAdapter(getActivity(), lstLopHP);
        lstMaHP = new ArrayList<>();
    }

    private void initWidgets(View rootView) {

        layout_setup = rootView.findViewById(R.id.layout_finish);
        rvHPhan = rootView.findViewById(R.id.rvTKB);
        //fabAdd = (FloatingActionButton)rootView.findViewById(R.id.fabAdd);
        rvHPhan.setAdapter(rvhPhanAdapter);
        rvHPhan.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvHPhan);
        rvHPhan.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvHPhan, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                showEditMaHPDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvHPhan.getContext(),
                ((LinearLayoutManager)rvHPhan.getLayoutManager()).getOrientation());
        rvHPhan.addItemDecoration(dividerItemDecoration);

        registerForContextMenu(rvHPhan);

        //fabAdd.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.layout_setup = ((SetupActivity)getActivity()).get_layout_setup();
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
                rvhPhanAdapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void showEditMaHPDialog(final int itemPosition) {
        final EditMaHPCustomDialogBuilder builderEditMaHP = new EditMaHPCustomDialogBuilder(getContext());
        builderEditMaHP.setMaHP(rvhPhanAdapter.getItem(itemPosition).getMaHP());
        //builderEditMaHP.setTitle(getString(R.string.edit_ma_hp));
        builderEditMaHP.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builderEditMaHP.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//abc
            }
        });

        builderEditMaHP.setAutoCompleteList(DBLopHPHelper.getsInstance().getListMaHP());

        final AlertDialog alertDialog = builderEditMaHP.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LopHP lopHP = getLopHPById(builderEditMaHP.getMaHP());
                        if (lopHP == null) {
                            builderEditMaHP.showError(R.string.class_id_invailid);
                        }
                        else if (rvhPhanAdapter.indexOf(lopHP.getMaHP()) != -1)
                            builderEditMaHP.showError(R.string.class_is_exist);
                        else {
                            rvhPhanAdapter.updateItem(itemPosition, lopHP);
                            finishFragCallBack.onListClassChangeState(rvhPhanAdapter.getAllItem().isEmpty());
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    public LopHP getLopHPById(String id) {
        //Ensure return value not null, using to add LopHP to listView
        LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(id);
//        if (lopHP == null) {
//            lopHP = new LopHP();
//            lopHP.setMaHP(id);
//            lopHP.setTenGV(getResources().getString(R.string.unknown_symbol));
//            lopHP.setTenHP(getResources().getString(R.string.unknown));
//            lopHP.setTkb(getResources().getString(R.string.unknown_symbol));
//        }
        return lopHP;
    }

    //tao thoi khoa bieu tu danh sach ma hoc phan da nhan dang duoc
    public void setListClass(ArrayList<LopHP> listLopHP) {
        //xoa danh sach cu
        lstLopHP.clear();
        lstMaHP.clear();

        for (LopHP i : listLopHP) {
            LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(i.getMaHP());
            if (lopHP != null){
                lstMaHP.add(lopHP.getMaHP());
                lstLopHP.add(lopHP);
            }
        }
        rvhPhanAdapter.notifyDataSetChanged();
    }


    public void writelstMaHPtoUserDB (DatabaseReference dbUserMaHocPhan) {
        //write to FirebaseDB
        dbUserMaHocPhan.setValue(lstMaHP).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //delete old db
                DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                for (String s : lstMaHP) {
                    DBLopHPHelper.getsInstance().insertUserMaHocPhan(s);
                }

                //subscribe a topics
                Utils.getsInstance(getActivity()).subscribeTopic(lstMaHP);
                Utils.getsInstance(getActivity()).subscribeTopic(
                        TOPIC_TBCHUNG
                );
                onUpLoadUserDBComplete.onSuccess();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Push failed", Toast.LENGTH_LONG).show();
                onUpLoadUserDBComplete.onFailed();
            }
        });
        //write to SQLite DB


    }

    public void addUserHP(final String id) {
        LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(id);
        if (lopHP == null){
            //TODO: Up error log to firebase
            Snackbar.make(layout_setup, getResources().getString(R.string.add_hp_failed),
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        if (lstMaHP.indexOf(lopHP) == -1)  {
            rvhPhanAdapter.addItemWithoutSort(DBLopHPHelper.getsInstance().getLopHocPhan(id));
            lstMaHP.add(id);

            Snackbar.make(
                    layout_setup,
                    getResources().getString(R.string.add_hp_success),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(
                            R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rvhPhanAdapter.undo();
                                }
                            }
                    ).show();
        }
    }

    public void removeUserHP(final String id) {
        try {
            rvhPhanAdapter.removeItem(id);
            Snackbar.make(
                    layout_setup,
                    getResources().getString(R.string.remove_hp_success),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvhPhanAdapter.undo();
                        }
                    }).show();
        } catch (AppException e) {
            e.printStackTrace();
            //TODO: up error log to firebase
            Snackbar.make(layout_setup, e.getMessage(), Snackbar.LENGTH_LONG).show();
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
        final LopHPCustomDialogBuilder lopHPCustomDialogBuilder = new LopHPCustomDialogBuilder(getContext());
        lopHPCustomDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        lopHPCustomDialogBuilder.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LopHP lopHP = lopHPCustomDialogBuilder.getCurrentLopHP();
                if (lopHP == null) {
                    Snackbar.make(layout_setup,
                            getResources().getString(R.string.incorrect_ma_hp), Snackbar.LENGTH_LONG).show();
                }
                else
                    addUserHP(lopHPCustomDialogBuilder.getCurrentLopHP().getMaHP());
            }
        });

        AlertDialog alertDialog = lopHPCustomDialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdd:
                showAddLopHPDialog();
                break;
        }
    }

    public void setOnUpLoadUserDBComplete(OnUpLoadUserDBComplete onUpLoadUserDBComplete) {
        this.onUpLoadUserDBComplete = onUpLoadUserDBComplete;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FinishFragCallBack)
            this.finishFragCallBack = (FinishFragCallBack)context;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RVHPhanAdapter.ViewHolder) {
            final LopHP lopHP = rvhPhanAdapter.getItem(position);
            removeUserHP(lopHP.getMaHP());
        }
    }

    public interface FinishFragCallBack {
        void onListClassChangeState(boolean isEmpty);
    }

    public interface OnUpLoadUserDBComplete {
        void onSuccess();
        void onFailed();
    }
}
