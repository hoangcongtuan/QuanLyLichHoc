package com.example.hoangcongtuan.quanlylichhoc;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recognize, container, false);
        lvLopHP = (ListView)rootView.findViewById(R.id.lstHocPhan);
        lstMaHP = new ArrayList<String>();
        lstMaHP.add("Demo 1");
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, lstMaHP);
        lvLopHP.setAdapter(adapter);
        bitmap = null;

        imageView = (ImageView)rootView.findViewById(R.id.imgHocPhan);

        return rootView;
    }

    public void recongnize() {
        recognize(this.bitmap);
    }

    public void recognize(Bitmap bitmap) {
        ArrayList<String> arrayList;
        arrayList = processImage(bitmap);
        //str = str.replace(',', '.').replace('.', '_').replace(" ", "");



        lstMaHP.clear();
        for (String i : arrayList) {
            lstMaHP.add(i);

        }

        //Log.d(TAG, "recognize: " + lstMaHP.toString());

        adapter.notifyDataSetChanged();
    }

    public ArrayList<String> processImage(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity()).build();
        if(!textRecognizer.isOperational()) {
            Log.e(TAG, "processImage: ");
            return null;
        }

        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
            String blocks = "";
            String lines = "";
            ArrayList<String> arrayList = new ArrayList<>();
            String words = "";
            for (int index = 0; index < textBlocks.size(); index++) {
                //extract scanned text blocks here
                TextBlock tBlock = textBlocks.valueAt(index);
                for (Text line : tBlock.getComponents()) {
                    arrayList.add(line.getValue());
                }
            }
            if (textBlocks.size() == 0) {
                Toast.makeText(getActivity(), "Scan Failed: Found nothing to scan", Toast.LENGTH_LONG).show();
                return null;
                //scanResults.setText("Scan Failed: Found nothing to scan");
            } else {
                return arrayList;
            }
        }

    }

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
