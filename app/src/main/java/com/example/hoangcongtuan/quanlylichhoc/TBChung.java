package com.example.hoangcongtuan.quanlylichhoc;

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

import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBChungAdapter;
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

public class TBChung extends Fragment {
    private static final String TAG = TBChung.class.getName();
    private RecyclerView recyclerView;
    private RVTBChungAdapter tbChungAdapter;
    DatabaseReference database;
    DatabaseReference tbChungRef;
    ValueEventListener tbChungEvenListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_tb_chung, container, false);
        return  viewGroup;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView)getView().findViewById(R.id.rvTBChung);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        tbChungAdapter = new RVTBChungAdapter(recyclerView, getContext());
        tbChungAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(tbChungAdapter);

        tbChungAdapter.setOnLoadMoreListentner(new RVTBChungAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore: ");
                tbChungAdapter.addThongBao(null);
                tbChungAdapter.addThongBao(null);
                tbChungAdapter.notifyDataSetChanged();
                tbChungAdapter.itemLoadCount += tbChungAdapter.LOAD_MORE_DELTA;
                tbChungRef.removeEventListener(tbChungEvenListener);
                tbChungRef.limitToFirst(tbChungAdapter.itemLoadCount).addListenerForSingleValueEvent(tbChungEvenListener);
            }
        });
        loadData();
    }

    public void loadData() {
        database = FirebaseDatabase.getInstance().getReference();
        tbChungAdapter.addThongBao(null);
        tbChungAdapter.addThongBao(null);
        tbChungAdapter.notifyDataSetChanged();
        tbChungRef = database.child("chung/data/");
        tbChungEvenListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ThongBaoObj tbObj;
                Iterable<DataSnapshot> lstThongBao;
                lstThongBao = dataSnapshot.getChildren();

                tbChungAdapter.removeLast();
                tbChungAdapter.removeLast();
                int count = 0;
                for (DataSnapshot dtSnapShot :
                        lstThongBao) {
                    if (count >= tbChungAdapter.itemLoaded) {
                        tbObj = dtSnapShot.getValue(ThongBaoObj.class);
                        tbChungAdapter.addThongBao(new ThongBao(tbObj.day, tbObj.event, tbObj.context));
                        Log.d(TAG, "onDataChange: ");
                    }
                    count++;
                }

                if (count == tbChungAdapter.itemLoaded)
                    tbChungAdapter.allItemLoaded = true;
                tbChungAdapter.itemLoaded = count;
                tbChungAdapter.itemLoadCount = count;
                tbChungAdapter.notifyDataSetChanged();
                tbChungAdapter.isLoading = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tbChungRef.limitToFirst(tbChungAdapter.itemLoadCount).addListenerForSingleValueEvent(tbChungEvenListener);
    }

}
