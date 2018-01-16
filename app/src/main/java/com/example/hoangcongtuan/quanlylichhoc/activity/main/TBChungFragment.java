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
import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTBAdapter;
import com.example.hoangcongtuan.quanlylichhoc.listener.HidingScrollListener;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBao;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBaoObj;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;
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
    private RVTBAdapter tbChungAdapter;
    private DatabaseReference database;
    private DatabaseReference tbChungRef;
    private ValueEventListener tbChungEvenListener;
    private RVTBAdapter.ICallBack iCallBack;
    private RVTBAdapter.ICallBack privCallBack;
    private HidingScrollListener hidingScrollListener;

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
        recyclerView.addOnScrollListener(hidingScrollListener);

        int paddingTop = Utils.getToolbarHeight(getContext()) + Utils.getTabsHeight(getContext());
        recyclerView.setPadding(recyclerView.getPaddingLeft(), paddingTop, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());

        tbChungAdapter = new RVTBAdapter(recyclerView, getContext());
        tbChungAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(tbChungAdapter);

        //set call back cho adapter
        tbChungAdapter.setICallBack(new RVTBAdapter.ICallBack() {
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

    //load them du lieu khi keo xuong
    private void loadMore() {
        Log.d(TAG, "loadMore: ");
        tbChungAdapter.addThongBao(null);
        tbChungAdapter.addThongBao(null);
        tbChungAdapter.notifyDataSetChanged();
        tbChungAdapter.itemLoadCount += tbChungAdapter.LOAD_MORE_DELTA;
        tbChungRef.removeEventListener(tbChungEvenListener);
        tbChungRef.limitToLast(tbChungAdapter.itemLoadCount).addListenerForSingleValueEvent(tbChungEvenListener);
    }


    //scroll man hinh den thong bao co ma hash
    public void scrollTo(final String hash) {
        //set call back cho apdapter
        setPrivCallBack(new RVTBAdapter.ICallBack() {
            @Override
            public void onLoadMore() {

            }

            //sau khi load them du lieu
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

    public void setOnHidingScrollListener(HidingScrollListener hsl) {
        hidingScrollListener = hsl;
    }

    //scroll toi thong bao thu position
    private void scrollTo(final int position) {
        setPrivCallBack(new RVTBAdapter.ICallBack() {
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


    //load du lieu lan dau
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
                        lstTmp.add(new ThongBao(tbObj.day, tbObj.event, tbObj.content, tbObj.key));
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

    //ham set call back
    private void setPrivCallBack(RVTBAdapter.ICallBack privCallBack) {
        this.privCallBack = privCallBack;
    }

    public void setiCallBack(RVTBAdapter.ICallBack iCallBack) {
        this.iCallBack = iCallBack;
    }
}
