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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVClassAdapter.RVClassAdapter;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVClassAdapter.RepositoryObserver;
import com.example.hoangcongtuan.quanlylichhoc.customview.EditClassIDCustomDialogBuilder;
import com.example.hoangcongtuan.quanlylichhoc.customview.AddClassCustomDialogBuilder;
import com.example.hoangcongtuan.quanlylichhoc.exception.AppException;
import com.example.hoangcongtuan.quanlylichhoc.helper.RecyclerItemTouchHelper;
import com.example.hoangcongtuan.quanlylichhoc.listener.RecyclerTouchListener;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.helper.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 10/13/17.
 */

public class FinishFragment extends Fragment implements View.OnClickListener,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener, RepositoryObserver {
    private final static String TAG = FinishFragment.class.getName();
    private  final static String TOPIC_TBCHUNG = "TBChung";
    private RVClassAdapter rvClassAdapter;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finish, container, false);
        initWidgets(rootView);
        return rootView;
    }

    private void init() {
        ArrayList<LopHP> lstLopHP = new ArrayList<>();
        rvClassAdapter = new RVClassAdapter(getActivity(), lstLopHP);
        rvClassAdapter.registerObserver(this);
    }

    private void initWidgets(View rootView) {
        layout_setup = rootView.findViewById(R.id.layout_finish);
        RecyclerView rvHPhan = rootView.findViewById(R.id.rvTKB);

        rvHPhan.setAdapter(rvClassAdapter);
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
                ((LinearLayoutManager) rvHPhan.getLayoutManager()).getOrientation());
        rvHPhan.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.layout_setup = ((SetupActivity)getActivity()).get_layout_setup();
    }

    public void showEditMaHPDialog(final int itemPosition) {
        final EditClassIDCustomDialogBuilder builderEditMaHP = new EditClassIDCustomDialogBuilder(getContext());
        builderEditMaHP.setMaHP(rvClassAdapter.getItem(itemPosition).getMaHP());
        //builderEditMaHP.setTitle(getString(R.string.edit_ma_hp));
        builderEditMaHP.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builderEditMaHP.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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
                        else if (rvClassAdapter.indexOf(lopHP.getMaHP()) != -1)
                            builderEditMaHP.showError(R.string.class_is_exist);
                        else {
                            rvClassAdapter.updateItem(itemPosition, lopHP);
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
        //Can be null value
        return DBLopHPHelper.getsInstance().getLopHocPhan(id);
    }

    //tao thoi khoa bieu tu danh sach ma hoc phan da nhan dang duoc
    public void setListClass(ArrayList<LopHP> listLopHP) {
        rvClassAdapter.copyFrom(listLopHP);
    }

    public void writelstMaHPtoUserDB (DatabaseReference dbUserMaHocPhan) {
        final ArrayList<String> listId = rvClassAdapter.getAllId();
        for(int i = 0; i < listId.size(); i++) {
            listId.set(i,
                    Utils.getsInstance(getContext())
                            .dotToUnderLine(
                                    listId.get(i)
                            )
            );
        }
        //write to FirebaseDB
        dbUserMaHocPhan.setValue(listId).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //delete old db
                DBLopHPHelper.getsInstance().deleteAllUserMaHocPhan();
                for (String s : listId) {
                    DBLopHPHelper.getsInstance().insertUserMaHocPhan(
                            Utils.getsInstance(getContext()).underLineToDot(s)
                    );
                }

                //subscribe a topics
                Utils.getsInstance(getActivity()).subscribeTopic(listId);
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
        rvClassAdapter.addItemWithoutSort(DBLopHPHelper.getsInstance().getLopHocPhan(id));
        Snackbar.make(
                layout_setup,
                getResources().getString(R.string.add_hp_success),
                Snackbar.LENGTH_LONG)
                .setAction(
                        R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rvClassAdapter.undo();
                            }
                        }
                        ).show();
    }

    public void removeUserHP(final String id) {
        try {
            rvClassAdapter.removeItem(id);
            Snackbar.make(
                    layout_setup,
                    getResources().getString(R.string.remove_hp_success),
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rvClassAdapter.undo();
                        }
                    }).show();
        } catch (AppException e) {
            e.printStackTrace();
            //TODO: up error log to firebase
            Snackbar.make(layout_setup, e.getMessage(), Snackbar.LENGTH_LONG).show();
        }
    }

    public void showAddLopHPDialog() {
        final AddClassCustomDialogBuilder addClassCustomDialogBuilder = new AddClassCustomDialogBuilder(getContext());
        addClassCustomDialogBuilder.setAutoCompleteList();

        addClassCustomDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        addClassCustomDialogBuilder.setPositiveButton(getResources().getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        final AlertDialog alertDialog = addClassCustomDialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //override positive button, prevent dismiss when click to it in some case
                        LopHP lopHP = addClassCustomDialogBuilder.getCurrentLopHP();
                        if (lopHP == null) {
                            addClassCustomDialogBuilder.showError(R.string.class_id_invailid);
                        }
                        else if (rvClassAdapter.indexOf(lopHP.getMaHP()) != -1)
                            addClassCustomDialogBuilder.showError(R.string.class_is_exist);
                        else {
                            addUserHP(addClassCustomDialogBuilder.getCurrentLopHP().getMaHP());
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
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
        if (viewHolder instanceof RVClassAdapter.ViewHolder) {
            final LopHP lopHP = rvClassAdapter.getItem(position);
            removeUserHP(lopHP.getMaHP());
        }
    }

    @Override
    public void onDataStateChange(boolean isEmpty) {
        finishFragCallBack.onListClassChangeState(isEmpty);
    }

    public interface FinishFragCallBack {
        void onListClassChangeState(boolean isEmpty);
    }

    public interface OnUpLoadUserDBComplete {
        void onSuccess();
        void onFailed();
    }
}
