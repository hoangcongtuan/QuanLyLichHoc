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
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBHPhanAdapter;
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
 * Java code cua fragment thong bao lop hoc phan
 */

public class TBHocPhanFragment extends Fragment {

    private final static String TAG = TBHocPhanFragment.class.getName();

    private RVTBHPhanAdapter hocPhanAdapter;
    RecyclerView recyclerView;
    DatabaseReference database;
    DatabaseReference tbHocPhanRef;
    ValueEventListener tbHocPhanEvenListener;
    RVTBChungAdapter.ICallBack iCallBack;
    RVTBChungAdapter.ICallBack privCallBack;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "onCreate: ");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = (ViewGroup)inflater.inflate(R.layout.fragment_tb_hocphan, container, false);
        //Log.d(TAG, "onCreateView: ");

        recyclerView = (RecyclerView)rootView.findViewById(R.id.rvTBHocPhan);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        hocPhanAdapter = new RVTBHPhanAdapter(recyclerView, getContext());
        hocPhanAdapter.notifyDataSetChanged();

        //set call back
        hocPhanAdapter.setiCallBack(new RVTBChungAdapter.ICallBack() {
            @Override
            public void onLoadMore() {
                //Log.d(TAG, "onLoadMore: ");
                hocPhanAdapter.addThongBao(null);
                hocPhanAdapter.addThongBao(null);
                hocPhanAdapter.notifyDataSetChanged();
                hocPhanAdapter.itemLoadCount += hocPhanAdapter.LOAD_MORE_DELTA;
                tbHocPhanRef.removeEventListener(tbHocPhanEvenListener);
                tbHocPhanRef.limitToLast(hocPhanAdapter.itemLoadCount).addListenerForSingleValueEvent(tbHocPhanEvenListener);
            }

            @Override
            public void onLoadMoreFinish() {

            }

            @Override
            public void onFirstLoadFinish() {

            }
        });
        recyclerView.setAdapter(hocPhanAdapter);
        loadData();
        return  rootView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.d(TAG, "onViewCreated: ");
    }

    public void loadMore() {
        Log.d(TAG, "loadMore: ");
        hocPhanAdapter.addThongBao(null);
        hocPhanAdapter.addThongBao(null);
        hocPhanAdapter.notifyDataSetChanged();
        hocPhanAdapter.itemLoadCount += hocPhanAdapter.LOAD_MORE_DELTA;
        tbHocPhanRef.removeEventListener(tbHocPhanEvenListener);
        tbHocPhanRef.limitToLast(hocPhanAdapter.itemLoadCount).addListenerForSingleValueEvent(tbHocPhanEvenListener);
//        recyclerView.scrollToPosition(
//                tbChungAdapter.getLstThongBao().size() - 1
//        );
    }

    public void scrollTo(final String hash) {
        setPrivCallBack(new RVTBChungAdapter.ICallBack() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onLoadMoreFinish() {
                ArrayList<ThongBao> lstTBHocPhan;
                lstTBHocPhan = hocPhanAdapter.getLstThongBao();

                if (hocPhanAdapter.allItemLoaded) {
                    Toast.makeText(getActivity(), "Khong tim thay thong bao!", Toast.LENGTH_SHORT).show();
                    setPrivCallBack(null);
                    return;
                }

                for(ThongBao tb : lstTBHocPhan) {
                    if (hash.compareTo(tb.getKey()) == 0) {
                        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                            @Override
                            protected int getVerticalSnapPreference() {
                                return SNAP_TO_START;
                            }
                        };
                        int position = lstTBHocPhan.indexOf(tb);
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

    public void scrollTo(final int position) {
        setPrivCallBack(new RVTBChungAdapter.ICallBack() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onLoadMoreFinish() {

                if (hocPhanAdapter.allItemLoaded) {
                    Toast.makeText(getActivity(), "Khong tim thay thong bao!", Toast.LENGTH_SHORT).show();
                    setPrivCallBack(null);
                    return;
                }


                if (hocPhanAdapter.getLstThongBao().size() - 1 < position)
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
                ArrayList<ThongBao> lstTmp = new ArrayList<>();

                //xoa loading item
                hocPhanAdapter.removeLastThongBao();
                hocPhanAdapter.removeLastThongBao();
                int count = 0;
                for (DataSnapshot dtSnapShot :
                        lstThongBao) {
                    if (count < dataSnapshot.getChildrenCount() - hocPhanAdapter.itemLoaded) {
                        tbObj = dtSnapShot.getValue(ThongBaoObj.class);
                        lstTmp.add(new ThongBao(tbObj.day, tbObj.event, tbObj.context, tbObj.key));
                    }
                    count++;
                }
                Collections.reverse(lstTmp);
                for(ThongBao tb : lstTmp) {
                    hocPhanAdapter.addThongBao(tb);
                }
                if (count == hocPhanAdapter.itemLoaded) {
                    //Log.d(TAG, "onDataChange: all item loaded");
                    hocPhanAdapter.allItemLoaded = true;
                }

                //tinh lai so item da load
                hocPhanAdapter.itemLoaded = count;
                hocPhanAdapter.itemLoadCount = count;
                hocPhanAdapter.notifyDataSetChanged();
                hocPhanAdapter.isLoading = false;

                if (privCallBack != null)
                    privCallBack.onLoadMoreFinish();
                if (iCallBack != null)
                    iCallBack.onLoadMoreFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tbHocPhanRef = database.child("lop_hoc_phan/data/");
        tbHocPhanRef.limitToLast(hocPhanAdapter.itemLoadCount).addListenerForSingleValueEvent(tbHocPhanEvenListener);

    }

    public void setiCallBack(RVTBChungAdapter.ICallBack iCallBack) {
        this.iCallBack = iCallBack;
    }

    private void setPrivCallBack(RVTBChungAdapter.ICallBack privCallBack) {
        this.privCallBack = privCallBack;
    }
}
