package com.example.hoangcongtuan.quanlylichhoc.activity.setup;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVHPhanAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.CustomDialogBuilderEditMaHP;
import com.example.hoangcongtuan.quanlylichhoc.customview.CustomDialogBuilderLopHP;
import com.example.hoangcongtuan.quanlylichhoc.exception.AppException;
import com.example.hoangcongtuan.quanlylichhoc.helper.RecyclerItemTouchHelper;
import com.example.hoangcongtuan.quanlylichhoc.listener.RecyclerTouchListener;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 9/27/17.
 */

public class RecognizeFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private final static String TAG = RecognizeFragment.class.getName();
    private RVHPhanAdapter rvhPhanAdapter;
    private CoordinatorLayout layout_setup;
    private RecyclerView rvLopHP;
    //private FloatingActionButton fabAdd;
    public ArrayList<LopHP> lstMaHP;
    private Bitmap bitmap;
    private ImageView imageView;
    private View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recognize, container, false);
        initWidgets();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.layout_setup = ((SetupActivity)getActivity()).get_layout_setup();
    }

    private void init() {
        lstMaHP = new ArrayList<>();
        rvhPhanAdapter = new RVHPhanAdapter(getContext(), lstMaHP);
        bitmap = null;
    }

    private void initWidgets() {
        rvLopHP = rootView.findViewById(R.id.rvHocPhan);
        imageView = rootView.findViewById(R.id.imgHocPhan);
        //fabAdd = rootView.findViewById(R.id.fabAdd);

        rvLopHP.setAdapter(rvhPhanAdapter);
        rvLopHP.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvLopHP);

        rvLopHP.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvLopHP, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                showEditMaHPDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvLopHP.getContext(),
                ((LinearLayoutManager)rvLopHP.getLayoutManager()).getOrientation());
        rvLopHP.addItemDecoration(dividerItemDecoration);

//        fabAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showAddLopHPDialog();
//            }
//        });

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
            rvhPhanAdapter.addItem(DBLopHPHelper.getsInstance().getLopHocPhan(id));

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

    public void showAddLopHPDialog() {
        final CustomDialogBuilderLopHP customDialogBuilderLopHP = new CustomDialogBuilderLopHP(getContext());
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
                    Snackbar.make(layout_setup,
                            getResources().getString(R.string.incorrect_ma_hp), Snackbar.LENGTH_LONG).show();
                }
                else
                    addUserHP(customDialogBuilderLopHP.getCurrentLopHP().getMaHP());
            }
        });

        AlertDialog alertDialog = customDialogBuilderLopHP.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    //ham de goi tu SetupActivity
    public void recognize() {
        recognizeMaHP(this.bitmap);
    }

    public void recognizeMaHP(Bitmap bitmap) {
        ArrayList<String> arrayList;
        arrayList = processImage(bitmap);

        if(arrayList == null) {
            Toast.makeText(getActivity(), "Khong co du lieu nao!", Toast.LENGTH_SHORT).show();
            lstMaHP.clear();
            rvhPhanAdapter.notifyDataSetChanged();
            return;
        }
        lstMaHP.clear();
        for (String i : arrayList) {
            i = i.replace(" ", "").replace(",", ".").replace(".", "_");
            lstMaHP.add(
                    getLopHPById(i)
            );
        }
        rvhPhanAdapter.notifyDataSetChanged();
    }

    public LopHP getLopHPById(String id) {
        //Ensure return value not null, using to add LopHP to listView
        LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(id);
        if (lopHP == null) {
            lopHP = new LopHP();
            lopHP.setMaHP(id);
            lopHP.setTenGV(getResources().getString(R.string.unknown_symbol));
            lopHP.setTenHP(getResources().getString(R.string.unknown));
            lopHP.setTkb(getResources().getString(R.string.unknown_symbol));
        }
        return lopHP;
    }

    public ArrayList<String> processImage(Bitmap bitmap) throws NullPointerException{
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity()).build();
        if(!textRecognizer.isOperational()) {
            Toast.makeText(getActivity(), "Vision Err", Toast.LENGTH_LONG).show();
            return null;
        }

        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
            ArrayList<String> arrayList = new ArrayList<>();
            for (int index = 0; index < textBlocks.size(); index++) {
                //extract scanned text blocks here
                TextBlock tBlock = textBlocks.valueAt(index);
                for(int i = 0; i < tBlock.getComponents().size(); i++) {
                    arrayList.add(tBlock.getComponents().get(i).getValue());
                    Log.d(TAG, "processImage: " + tBlock.getComponents().get(i).getValue());
                }
            }
            if (textBlocks.size() == 0) {
                return null;
            } else {
                return arrayList;
            }
        }

    }

    public void showEditMaHPDialog(final int itemPosition) {
        final CustomDialogBuilderEditMaHP builderEditMaHP = new CustomDialogBuilderEditMaHP(getContext());
        builderEditMaHP.setMaHP(rvhPhanAdapter.getItem(itemPosition).getMaHP());
        builderEditMaHP.setTitle(getString(R.string.edit_ma_hp));
        builderEditMaHP.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LopHP lopHP = getLopHPById(builderEditMaHP.getMaHP());

                rvhPhanAdapter.updateItem(itemPosition, lopHP);

                rvhPhanAdapter.notifyDataSetChanged();
            }
        });

        builderEditMaHP.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builderEditMaHP.create();
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alertDialog.show();
    }

    //set bitmap tu SetupActivity
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RVHPhanAdapter.ViewHolder) {
            final LopHP lopHP = rvhPhanAdapter.getItem(position);
            removeUserHP(lopHP.getMaHP());
        }
    }
}
