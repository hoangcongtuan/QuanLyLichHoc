package com.example.hoangcongtuan.quanlylichhoc.activity.setup;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVHPhanAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.EditMaHPCustomDialogBuilder;
import com.example.hoangcongtuan.quanlylichhoc.customview.LopHPCustomDialogBuilder;
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
    private RVHPhanAdapter rvClassAdapter;
    private CoordinatorLayout layout_setup;
    private RecyclerView rvClass;
    //private FloatingActionButton fabAdd;
    private Bitmap bitmap;
    private ImageView imageView;
    private OnRecognize onRecognize;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recognize, container, false);
        initWidgets(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.layout_setup = ((SetupActivity)getActivity()).get_layout_setup();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecognize)
            onRecognize = (OnRecognize)context;
    }

    private void init() {
        ArrayList<LopHP> lstMaHP = new ArrayList<>();
        rvClassAdapter = new RVHPhanAdapter(getContext(), lstMaHP);
        bitmap = null;
    }

    public ArrayList<LopHP> getListMaHp() {
        return rvClassAdapter.getAllItem();
    }

    private void initWidgets(View rootView) {
        rvClass = rootView.findViewById(R.id.rvHocPhan);
        imageView = rootView.findViewById(R.id.imgHocPhan);
        rvClass.setAdapter(rvClassAdapter);
        rvClass.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(
                0, ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvClass);
        rvClass.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvClass, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                showEditMaHPDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvClass.getContext(),
                ((LinearLayoutManager) rvClass.getLayoutManager()).getOrientation());
        rvClass.addItemDecoration(dividerItemDecoration);
        rvClass.setVisibility(View.GONE);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            Log.d(TAG, "setUserVisibleHint: is Visible");
            recognize();
        }
    }

    public void addUserHP(final String id) {
        LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(id);
        if (lopHP == null){
            //TODO: Up error log to firebase
            Snackbar.make(layout_setup, getResources().getString(R.string.add_hp_failed),
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        if (rvClassAdapter.indexOf(lopHP) == -1)  {
            rvClassAdapter.addItem(DBLopHPHelper.getsInstance().getLopHocPhan(id));

            Snackbar.make(
                    layout_setup,
                    getResources().getString(R.string.add_hp_success),
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(
                            R.string.undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    rvClassAdapter.undo();
                                }
                            }
                    ).show();
        } else {
            Snackbar.make(layout_setup, R.string.duplicated_lop_hp, Snackbar.LENGTH_SHORT).show();
        }
    }

    public void removeUserHP(final String id) {
        try {
            rvClassAdapter.removeItem(id);
            Snackbar.make(
                    layout_setup,
                    getResources().getString(R.string.remove_hp_success),
                    Snackbar.LENGTH_INDEFINITE)
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

    //ham de goi tu SetupActivity
    public void recognize() {
        onRecognize.startRecognize();
        recognizeMaHP(RecognizeFragment.this.bitmap);
        onRecognize.endRecognize();
    }

    public void recognizeMaHP(Bitmap bitmap) {
        ArrayList<String> arrayList;
        arrayList = processImage(bitmap);

        rvClass.setVisibility(View.VISIBLE);

        if(arrayList == null) {
            Snackbar.make(layout_setup, R.string.no_data, Snackbar.LENGTH_LONG).show();
            rvClassAdapter.removeAllItem();
            return;
        }

        rvClassAdapter.removeAllItem();
        for (String i : arrayList) {
            i = i.replace(" ", "").replace(",", ".").replace(".", "_");
            rvClassAdapter.addItemWithoutSort(getLopHPById(i));
        }
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
            Snackbar.make(layout_setup, R.string.not_support_vision, Snackbar.LENGTH_SHORT).show();
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
        final EditMaHPCustomDialogBuilder builderEditMaHP = new EditMaHPCustomDialogBuilder(getContext());
        builderEditMaHP.setMaHP(rvClassAdapter.getItem(itemPosition).getMaHP());
        builderEditMaHP.setTitle(getString(R.string.edit_ma_hp));
        builderEditMaHP.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LopHP lopHP = getLopHPById(builderEditMaHP.getMaHP());

                rvClassAdapter.updateItem(itemPosition, lopHP);

                rvClassAdapter.notifyDataSetChanged();
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
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof RVHPhanAdapter.ViewHolder) {
            final LopHP lopHP = rvClassAdapter.getItem(position);
            removeUserHP(lopHP.getMaHP());
        }
    }

    public interface OnRecognize {
        void startRecognize();
        void endRecognize();
    }
}
