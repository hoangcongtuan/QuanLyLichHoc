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

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String str;
        str = processImage(bitmap);
        str = str.replace(',', '.').replace('.', '_').replace(" ", "");

        List<String> list;
        list = Arrays.asList(str.split("\\n"));


        lstMaHP.clear();
        for (String i : list) {
            lstMaHP.add(i);

        }

        //Log.d(TAG, "recognize: " + lstMaHP.toString());

        adapter.notifyDataSetChanged();
    }

    public String processImage(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getActivity()).build();
        if(!textRecognizer.isOperational()) {
            Log.e(TAG, "processImage: ");
            return null;
        }

        else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
            StringBuilder list = new StringBuilder();
            if (textBlocks.size() == 0)
                return null;
            for(int i = 0; i < textBlocks.size(); i++) {
                TextBlock item = textBlocks.get(i);
                if (item != null)
                    list.append(item.getValue().toString() + System.lineSeparator());
            }
            return list.toString();
        }

    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        imageView.setImageBitmap(bitmap);
    }
}
