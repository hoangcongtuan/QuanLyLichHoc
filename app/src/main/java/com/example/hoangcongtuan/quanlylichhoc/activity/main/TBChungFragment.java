package com.example.hoangcongtuan.quanlylichhoc.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBChungAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBao;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBaoObj;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by hoangcongtuan on 9/6/17.
 */

public class TBChungFragment extends Fragment {
    private static final String TAG = TBChungFragment.class.getName();
    private RecyclerView recyclerView;
    private RVTBChungAdapter tbChungAdapter;
    private DatabaseReference database;
    private DatabaseReference tbChungRef;
    private ValueEventListener tbChungEvenListener;
    private RVTBChungAdapter.ICallBack iCallBack;
    private RVTBChungAdapter.ICallBack privCallBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup;
        viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_tb_chung, container, false);
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

        tbChungAdapter.setICallBack(new RVTBChungAdapter.ICallBack() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore: ");
                tbChungAdapter.addThongBao(null);
                tbChungAdapter.addThongBao(null);
                tbChungAdapter.notifyDataSetChanged();
                tbChungAdapter.itemLoadCount += tbChungAdapter.LOAD_MORE_DELTA;
                tbChungRef.removeEventListener(tbChungEvenListener);
                tbChungRef.limitToLast(tbChungAdapter.itemLoadCount).addListenerForSingleValueEvent(tbChungEvenListener);
            }

            @Override
            public void onLoadMoreFinish() {

            }

            @Override
            public void onFirstLoadFinish() {
            }
        });
        loadData();
    }

    private void loadMore() {
        Log.d(TAG, "loadMore: ");
        tbChungAdapter.addThongBao(null);
        tbChungAdapter.addThongBao(null);
        tbChungAdapter.notifyDataSetChanged();
        tbChungAdapter.itemLoadCount += tbChungAdapter.LOAD_MORE_DELTA;
        tbChungRef.removeEventListener(tbChungEvenListener);
        tbChungRef.limitToLast(tbChungAdapter.itemLoadCount).addListenerForSingleValueEvent(tbChungEvenListener);
    }


    public void scrollTo(final String hash) {
        setPrivCallBack(new RVTBChungAdapter.ICallBack() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onLoadMoreFinish() {
                ArrayList<ThongBao> lstTBChung;
                lstTBChung = tbChungAdapter.getLstThongBao();

                if (tbChungAdapter.allItemLoaded) {
                    Toast.makeText(getActivity(), "Khong tim thay thong bao!", Toast.LENGTH_SHORT).show();
                    setPrivCallBack(null);
                    return;
                }

                for(ThongBao tb : lstTBChung) {
                    if (hash.compareTo(tb.getKey()) == 0) {
                        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                            @Override
                            protected int getVerticalSnapPreference() {
                                return SNAP_TO_START;
                            }
                        };
                        int position = lstTBChung.indexOf(tb);
                        smoothScroller.setTargetPosition(position);
                        recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
                        setPrivCallBack(null);
                        return;
                    }
                }
                loadMore();
            }

            @Override
            public void onFirstLoadFinish() {

            }
        });
    }

    private void scrollTo(final int position) {
        setPrivCallBack(new RVTBChungAdapter.ICallBack() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onLoadMoreFinish() {

                if (tbChungAdapter.allItemLoaded) {
                    Toast.makeText(getActivity(), "Khong tim thay thong bao!", Toast.LENGTH_SHORT).show();
                    setPrivCallBack(null);
                    return;
                }


                if (tbChungAdapter.getLstThongBao().size() - 1 < position)
                    loadMore();
                else {
                    RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                        @Override
                        protected int getVerticalSnapPreference() {
                            return SNAP_TO_START;
                        }
                    };
                    smoothScroller.setTargetPosition(position);
                    recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
                    setPrivCallBack(null);
                }
            }

            @Override
            public void onFirstLoadFinish() {

            }
        });
    }


    private void loadData() {
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
                ArrayList<ThongBao> lstTmp = new ArrayList<>();

                tbChungAdapter.removeLast();
                tbChungAdapter.removeLast();
                int count = 0;
                for (DataSnapshot dtSnapShot :
                        lstThongBao) {
                    if (count < dataSnapshot.getChildrenCount() - tbChungAdapter.itemLoaded) {
                        tbObj = dtSnapShot.getValue(ThongBaoObj.class);
                        lstTmp.add(new ThongBao(tbObj.day, tbObj.event, tbObj.context, tbObj.key));
                    }
                    count++;
                }
                Collections.reverse(lstTmp);
                for(ThongBao tb : lstTmp) {
                    tbChungAdapter.addThongBao(tb);
                }

                if (count == tbChungAdapter.itemLoaded)
                    tbChungAdapter.allItemLoaded = true;
                tbChungAdapter.itemLoaded = count;
                tbChungAdapter.itemLoadCount = count;
                tbChungAdapter.notifyDataSetChanged();
                tbChungAdapter.isLoading = false;
                if (privCallBack != null)
                    privCallBack.onLoadMoreFinish();
                if (iCallBack != null)
                    iCallBack.onLoadMoreFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tbChungRef.limitToLast(tbChungAdapter.itemLoadCount).addListenerForSingleValueEvent(tbChungEvenListener);
    }

    private void setPrivCallBack(RVTBChungAdapter.ICallBack privCallBack) {
        this.privCallBack = privCallBack;
    }

    public void setiCallBack(RVTBChungAdapter.ICallBack iCallBack) {
        this.iCallBack = iCallBack;
    }
}
