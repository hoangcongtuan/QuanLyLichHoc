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
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.LVTKBieuAdapter;
import com.example.hoangcongtuan.quanlylichhoc.customview.CustomDialogBuilderEditMaHP;
import com.example.hoangcongtuan.quanlylichhoc.customview.CustomDialogBuilderLopHP;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 9/27/17.
 */

public class RecognizeFragment extends Fragment {

    private final static String TAG = RecognizeFragment.class.getName();
    private LVTKBieuAdapter adapter;
    CoordinatorLayout recognizeLayout;
    ListView lvLopHP;
    FloatingActionButton fabAdd;
    //ArrayAdapter<String> adapter;
    ArrayList<LopHP> lstMaHP;
    Bitmap bitmap;
    ImageView imageView;
    View rootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_recognize, container, false);
        getWidgets();
        setWidgets();
        setWidgetsEvent();
        return rootView;
    }

    private void init() {
        lstMaHP = new ArrayList<LopHP>();
        adapter = new LVTKBieuAdapter(getContext(), R.layout.lop_hp_item, lstMaHP);
        bitmap = null;
    }

    private void getWidgets() {
        lvLopHP = (ListView)rootView.findViewById(R.id.lstHocPhan);
        imageView = (ImageView)rootView.findViewById(R.id.imgHocPhan);
        recognizeLayout = (CoordinatorLayout)rootView.findViewById(R.id.recognize_layout);
        fabAdd = (FloatingActionButton)rootView.findViewById(R.id.fabAdd);
    }

    public void addLopHP(String maHP) {
        LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan(maHP);
        if (lopHP == null){
            return;
        }

        if (lstMaHP.indexOf(lopHP) == -1)  {
            lstMaHP.add(lopHP);
            adapter.notifyDataSetChanged();
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
                    Snackbar.make(recognizeLayout,
                            getResources().getString(R.string.incorrect_ma_hp), Snackbar.LENGTH_LONG).show();
                }
                else
                    addLopHP(customDialogBuilderLopHP.getCurrentLopHP().getMaHP());
            }
        });

        AlertDialog alertDialog = customDialogBuilderLopHP.create();
        alertDialog.show();
    }

    private void setWidgets() {
        lvLopHP.setAdapter(adapter);
        registerForContextMenu(lvLopHP);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddLopHPDialog();
            }
        });
    }

    private void setWidgetsEvent() {

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
            adapter.notifyDataSetChanged();
            return;
        }
        lstMaHP.clear();
        for (String i : arrayList) {
            i = i.replace(" ", "").replace(",", ".").replace(".", "_");
            lstMaHP.add(
                    getLopHPById(i)
            );

        }
        //Log.d(TAG, "recognize: " + lstMaHP.toString());
        adapter.notifyDataSetChanged();
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
            //Log.e(TAG, "processImage: ");
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
//                for (Text line : tBlock.getComponents()) {
//                    arrayList.add(line.getValue());
//                }
            }
            if (textBlocks.size() == 0) {
                //Toast.makeText(getActivity(), "Scan Failed: Found nothing to scan", Toast.LENGTH_LONG).show();
                return null;
                //scanResults.setText("Scan Failed: Found nothing to scan");
            } else {
                return arrayList;
            }
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.recognize_fragment_menu, menu);
    }

    public void showEditMaHPDialog(final int itemPosition) {
        final CustomDialogBuilderEditMaHP builderEditMaHP = new CustomDialogBuilderEditMaHP(getContext());
        builderEditMaHP.setMaHP(lstMaHP.get(itemPosition).getMaHP());
        builderEditMaHP.setTitle(getString(R.string.edit_ma_hp));
        builderEditMaHP.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                LopHP lopHP = getLopHPById(builderEditMaHP.getMaHP());
                lstMaHP.get(itemPosition).setMaHP(lopHP.getMaHP());
                lstMaHP.get(itemPosition).setTkb(lopHP.getTkb());
                lstMaHP.get(itemPosition).setTenHP(lopHP.getTenHP());
                lstMaHP.get(itemPosition).setTenGV(lopHP.getTenGV());

                adapter.notifyDataSetChanged();
            }
        });

        builderEditMaHP.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builderEditMaHP.create().show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        int id = item.getItemId();
        switch (id){
            case R.id.menu_remove:
                lstMaHP.remove(position);
                adapter.notifyDataSetChanged();
                break;
            case R.id.menu_edit:
                showEditMaHPDialog(position);
                break;
        }
        return super.onContextItemSelected(item);
    }

    //set bitmap tu SetupActivity
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, "onResume: ");
    }
}
