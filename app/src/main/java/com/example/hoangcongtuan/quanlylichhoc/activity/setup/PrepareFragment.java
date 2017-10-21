package com.example.hoangcongtuan.quanlylichhoc.activity.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;

/**
 * Created by hoangcongtuan on 10/10/17.
 */

public class PrepareFragment extends android.support.v4.app.Fragment {

    //interface callback toi Setup Activity
    private PrepareFinish prepareFinish;

    //set callBack
    public void setPrepareFinish(PrepareFinish prepareFinish) {
        this.prepareFinish = prepareFinish;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_prepare, container, false);

        DBLopHPHelper.getsInstance().setOnCheckDB(new DBLopHPHelper.OnCheckDB() {
            @Override
            public void onDBAvailable() {
                //Toast.makeText(getActivity(), "DB Available!", Toast.LENGTH_SHORT).show();
                //LopHP lopHP = DBLopHPHelper.getsInstance().getLopHocPhan("4130403_1710_15_11");
                //Toast.makeText(getActivity(), lopHP.getTenHP(), Toast.LENGTH_SHORT).show();
                prepareFinish.onPrepareFinish();
            }

            @Override
            public void onDownloadFinish() {
                //Toast.makeText(getActivity(), "Download finish!", Toast.LENGTH_SHORT).show();
                prepareFinish.onPrepareFinish();

            }

            @Override
            public void onStartDownload() {
                //Toast.makeText(getActivity(), "Start Download!", Toast.LENGTH_SHORT).show();
                //prepareFinish.onPrepareFinish();

            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        DBLopHPHelper.getsInstance().checkDB();
        super.onStart();
    }


    public interface PrepareFinish {
        public void onPrepareFinish();
    }
}
