package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBao;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBaoObj;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 1/16/18.
 */

public class LoadFeedHelper {

    private final static String TAG = LoadFeedHelper.class.getName();
    private RVTBAdapter rvtbAdapter;
    private DatabaseReference firebase_ref;
    private ValueEventListener feedEvenListener;
    private RVTBAdapter.ILoadMoreCallBack privCallBack;
    private RVTBAdapter.ILoadMoreCallBack scrollToCallBack;


    public void setScrollToCallBack(RVTBAdapter.ILoadMoreCallBack scrollToCallBack) {
        this.scrollToCallBack = scrollToCallBack;
    }

    public LoadFeedHelper(RVTBAdapter adapter, DatabaseReference firebase_ref, RVTBAdapter.ILoadMoreCallBack privCallBack) {
        this.rvtbAdapter = adapter;
        this.firebase_ref = firebase_ref;
        this.privCallBack = privCallBack;

        init();
    }

    public void init() {
        //call back khi tai thong bao ve
        feedEvenListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ThongBaoObj tbObj;
                Iterable<DataSnapshot> lstThongBao;
                lstThongBao = dataSnapshot.getChildren();
                ArrayList<ThongBao> lstTmp = new ArrayList<>();

                rvtbAdapter.removeLast();
                rvtbAdapter.removeLast();
                int count = 0;
                for (DataSnapshot dtSnapShot :
                        lstThongBao) {
                    tbObj = dtSnapShot.getValue(ThongBaoObj.class);
                    lstTmp.add(new ThongBao(tbObj.day, tbObj.event, tbObj.content, tbObj.key));
                    count++;
                }
                for(ThongBao tb : lstTmp) {
                    rvtbAdapter.addThongBao(tb);
                }

                rvtbAdapter.notifyDataSetChanged();

                if (count < RVTBAdapter.LOAD_MORE_DELTA)
                    //load het roi
                    rvtbAdapter.allItemLoaded = true;
                rvtbAdapter.itemLoaded += count;
                rvtbAdapter.isLoading = false;

                if (scrollToCallBack != null)
                    scrollToCallBack.onLoadMoreFinish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //set call back cho adapter
        rvtbAdapter.setILoadMoreCallBack(new RVTBAdapter.ILoadMoreCallBack() {
            @Override
            public void onLoadMore() {

                Log.d(TAG, "onLoadMore: ");
                //add empty new feed such as facebook new feed
                rvtbAdapter.addThongBao(null);
                rvtbAdapter.addThongBao(null);

                rvtbAdapter.notifyDataSetChanged();


                //will load .. item
                rvtbAdapter.itemLoadCount += RVTBAdapter.LOAD_MORE_DELTA;

                //query to get next new feed from ..
                Query qrGetNextItemt = firebase_ref.orderByKey().startAt(rvtbAdapter.itemLoaded + "")
                        .limitToFirst(RVTBAdapter.LOAD_MORE_DELTA);
                qrGetNextItemt.addListenerForSingleValueEvent(feedEvenListener);

                privCallBack.onLoadMore();
            }

            @Override
            public void onLoadMoreFinish() {
                privCallBack.onLoadMoreFinish();
            }
        });
    }

    //call when using scroll to
    private void loadMore() {
        //called when call scroll to
        rvtbAdapter.addThongBao(null);
        rvtbAdapter.addThongBao(null);
        rvtbAdapter.itemLoadCount += RVTBAdapter.LOAD_MORE_DELTA;
        Query qrGetNextItemt = firebase_ref.orderByKey().startAt(rvtbAdapter.itemLoaded + "")
                .limitToFirst(RVTBAdapter.LOAD_MORE_DELTA);
        qrGetNextItemt.addListenerForSingleValueEvent(feedEvenListener);
    }

    //scroll man hinh den thong bao co ma hash
    public void scrollTo(final String hash) {
        //set call back cho apdapter
        setScrollToCallBack(new RVTBAdapter.ILoadMoreCallBack() {
            @Override
            public void onLoadMore() {

            }

            //sau khi load them du lieu
            @Override
            public void onLoadMoreFinish() {
                ArrayList<ThongBao> lstTBChung;
                lstTBChung = rvtbAdapter.getLstThongBao();

                for(ThongBao tb : lstTBChung) {
                    if (hash.compareTo(tb.getKey()) == 0) {
                        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(rvtbAdapter.getContext()) {
                            @Override
                            protected int getVerticalSnapPreference() {
                                return SNAP_TO_START;
                            }
                        };
                        int position = lstTBChung.indexOf(tb);
                        smoothScroller.setTargetPosition(position);

                        rvtbAdapter.getLinearLayoutManager().startSmoothScroll(smoothScroller);
                        setScrollToCallBack(null);
                        return;
                    }
                }

                if (rvtbAdapter.allItemLoaded) {
                    Toast.makeText(rvtbAdapter.getContext(), "Khong tim thay thong bao!", Toast.LENGTH_SHORT).show();
                    setScrollToCallBack(null);
                    return;
                }

                loadMore();
            }
        });

       //loadMore();
    }

    //scroll toi thong bao thu position
    private void scrollTo(final int position) {
        setPrivCallBack(new RVTBAdapter.ILoadMoreCallBack() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onLoadMoreFinish() {

                if (rvtbAdapter.allItemLoaded) {
                    Toast.makeText(rvtbAdapter.getContext(), "Khong tim thay thong bao!", Toast.LENGTH_SHORT).show();
                    setPrivCallBack(null);
                    return;
                }


                if (rvtbAdapter.getLstThongBao().size() - 1 < position)
                    loadMore();
                else {
                    RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(rvtbAdapter.getContext()) {
                        @Override
                        protected int getVerticalSnapPreference() {
                            return SNAP_TO_START;
                        }
                    };
                    smoothScroller.setTargetPosition(position);
                    rvtbAdapter.getLinearLayoutManager().startSmoothScroll(smoothScroller);
                    setPrivCallBack(null);
                }
            }
        });
    }


    //load du lieu lan dau
    public void loadFirstTime() {
        rvtbAdapter.addThongBao(null);
        rvtbAdapter.addThongBao(null);
        rvtbAdapter.notifyDataSetChanged();
        rvtbAdapter.itemLoadCount = RVTBAdapter.LOAD_MORE_DELTA;
        Query qrGetNextItemt = firebase_ref.orderByKey().startAt(rvtbAdapter.itemLoaded + "")
                .limitToFirst(RVTBAdapter.LOAD_MORE_DELTA);
        qrGetNextItemt.addListenerForSingleValueEvent(feedEvenListener);
    }

    //ham set call back
    private void setPrivCallBack(RVTBAdapter.ILoadMoreCallBack privCallBack) {
        this.privCallBack = privCallBack;
    }
}
