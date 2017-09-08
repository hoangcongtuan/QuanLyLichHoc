package com.example.hoangcongtuan.quanlylichhoc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoangcongtuan.quanlylichhoc.adapter.TBHocPhanAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBao;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBaoObj;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by hoangcongtuan on 9/6/17.
 */

public class TBHocPhan extends Fragment {

    private final static String TAG = TBHocPhan.class.getName();

    private TBHocPhanAdapter hocPhanAdapter;
    RecyclerView recyclerView;
    DatabaseReference database;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_tb_hocphan, container, false);
        return  viewGroup;
    }

    public void loadData() {
        database = FirebaseDatabase.getInstance().getReference();
        ValueEventListener tbHocPhanEvenListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ThongBaoObj tbObj;
                ThongBao tb;
                Iterable<DataSnapshot> lstThongBao;
                lstThongBao  = dataSnapshot.getChildren();
                for (DataSnapshot dtSnapshot :
                     lstThongBao) {
                    tbObj = dtSnapshot.getValue(ThongBaoObj.class);
                    hocPhanAdapter.addThongBao(new ThongBao(tbObj.day, tbObj.event, tbObj.context));
                }
                hocPhanAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        DatabaseReference tbHocPhanRef = database.child("lop_hoc_phan/data/");
        tbHocPhanRef.addListenerForSingleValueEvent(tbHocPhanEvenListener);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hocPhanAdapter = new TBHocPhanAdapter();

        hocPhanAdapter.notifyDataSetChanged();

        recyclerView = (RecyclerView)getView().findViewById(R.id.rvTBHocPhan);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(hocPhanAdapter);
        loadData();
    }
}
