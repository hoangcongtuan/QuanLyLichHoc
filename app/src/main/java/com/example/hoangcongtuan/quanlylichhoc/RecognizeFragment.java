package com.example.hoangcongtuan.quanlylichhoc;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 9/27/17.
 */

public class RecognizeFragment extends Fragment {

    private final static String TAG = RecognizeFragment.class.getName();
    ListView lvLopHP;
    ArrayAdapter<String> adapter;
    ArrayList<String> lstMaHP;
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
        lstMaHP = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, lstMaHP);
        bitmap = null;
    }

    private void getWidgets() {
        lvLopHP = (ListView)rootView.findViewById(R.id.lstHocPhan);
        imageView = (ImageView)rootView.findViewById(R.id.imgHocPhan);
    }

    private void setWidgets() {
        lvLopHP.setAdapter(adapter);
        registerForContextMenu(lvLopHP);
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
            lstMaHP.add(i);

        }

        //Log.d(TAG, "recognize: " + lstMaHP.toString());

        adapter.notifyDataSetChanged();
    }

    public ArrayList<String> processImage(Bitmap bitmap) throws NullPointerException{
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity()).build();
        if(!textRecognizer.isOperational()) {
            Log.e(TAG, "processImage: ");
            return null;
        }

        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
            ArrayList<String> arrayList = new ArrayList<>();
            for (int index = 0; index < textBlocks.size(); index++) {
                //extract scanned text blocks here
                TextBlock tBlock = textBlocks.valueAt(index);
                for (Text line : tBlock.getComponents()) {
                    arrayList.add(line.getValue());
                }
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
        inflater.inflate(R.menu.tkb_menu, menu);
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
        Log.d(TAG, "onStart: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }
}
