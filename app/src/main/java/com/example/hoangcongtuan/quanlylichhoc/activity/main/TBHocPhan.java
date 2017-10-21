package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBHPhanAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBao;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBaoObj;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by hoangcongtuan on 9/6/17.
 * Java code cua fragment thong bao lop hoc phan
 */

public class TBHocPhan extends Fragment {

    private final static String TAG = TBHocPhan.class.getName();

    private RVTBHPhanAdapter hocPhanAdapter;
    RecyclerView recyclerView;
    DatabaseReference database;
    DatabaseReference tbHocPhanRef;
    ValueEventListener tbHocPhanEvenListener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = (ViewGroup)inflater.inflate(R.layout.fragment_tb_hocphan, container, false);
        Log.d(TAG, "onCreateView: ");

        recyclerView = (RecyclerView)rootView.findViewById(R.id.rvTBHocPhan);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        hocPhanAdapter = new RVTBHPhanAdapter(recyclerView, getContext());
        hocPhanAdapter.notifyDataSetChanged();

        //set call back
        hocPhanAdapter.setOnLoadMoreListener(new RVTBHPhanAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore: ");
                hocPhanAdapter.addThongBao(null);
                hocPhanAdapter.addThongBao(null);
                hocPhanAdapter.notifyDataSetChanged();
                hocPhanAdapter.itemLoadCount += hocPhanAdapter.LOAD_MORE_DELTA;
                tbHocPhanRef.removeEventListener(tbHocPhanEvenListener);
                tbHocPhanRef.limitToFirst(hocPhanAdapter.itemLoadCount).addListenerForSingleValueEvent(tbHocPhanEvenListener);
            }
        });
        recyclerView.setAdapter(hocPhanAdapter);
        loadData();
        return  rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
    }

    public void loadData() {
        database = FirebaseDatabase.getInstance().getReference();
        //them loading item
        hocPhanAdapter.addThongBao(null);
        hocPhanAdapter.addThongBao(null);
        hocPhanAdapter.notifyDataSetChanged();
        tbHocPhanEvenListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ThongBaoObj tbObj;
                Iterable<DataSnapshot> lstThongBao;
                lstThongBao  = dataSnapshot.getChildren();

                //xoa loading item
                hocPhanAdapter.removeLastThongBao();
                hocPhanAdapter.removeLastThongBao();
                int count = 0;
                for (DataSnapshot dtSnapshot :
                        lstThongBao) {
                    if (count >= hocPhanAdapter.itemLoaded) {
                        //load tin moi
                        tbObj = dtSnapshot.getValue(ThongBaoObj.class);
                        hocPhanAdapter.addThongBao(new ThongBao(tbObj.day, tbObj.event, tbObj.context));
                        Log.d(TAG, "onDataChange: load item moi");
                    }
                    count++;

                }
                if (count == hocPhanAdapter.itemLoaded) {
                    Log.d(TAG, "onDataChange: all item loaded");
                    hocPhanAdapter.allItemLoaded = true;
                }

                //tinh lai so item da load
                hocPhanAdapter.itemLoaded = count;
                hocPhanAdapter.itemLoadCount = count;
                hocPhanAdapter.notifyDataSetChanged();
                hocPhanAdapter.isLoading = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tbHocPhanRef = database.child("lop_hoc_phan/data/");
        tbHocPhanRef.limitToFirst(hocPhanAdapter.itemLoadCount).addListenerForSingleValueEvent(tbHocPhanEvenListener);

    }

}
