package com.example.hoangcongtuan.quanlylichhoc.utils;

import android.util.Log;

import com.example.hoangcongtuan.quanlylichhoc.adapter.RVPostAdapter;
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

public class LoadSearchPostResultHelper {

    private final static String TAG = LoadSearchPostResultHelper.class.getName();
    private RVPostAdapter rvPostAdapter;
    private DatabaseReference firebase_ref;
    private ValueEventListener postEvenListener;
    private RVTBAdapter.ILoadMoreCallBack fragmentCallBack;
    private ArrayList<String> key_result;
    private int post_loaded_count;  //count so luong item da load moi lan


    public LoadSearchPostResultHelper(RVPostAdapter adapter, DatabaseReference firebase_ref,
                                      RVTBAdapter.ILoadMoreCallBack fragmentCallBack, ArrayList key_result) {
        this.rvPostAdapter = adapter;
        this.firebase_ref = firebase_ref;
        this.fragmentCallBack = fragmentCallBack;
        this.key_result = key_result;

        init();
    }

    public void init() {
        //call back khi tai thong bao ve
        postEvenListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: ");
                ThongBaoObj tbObj;
                Iterable<DataSnapshot> posts = dataSnapshot.getChildren();

                for (DataSnapshot snapshot :
                        posts) {
                    tbObj = snapshot.getValue(ThongBaoObj.class);
                    rvPostAdapter.addThongBao(new ThongBao(tbObj.day, tbObj.event, tbObj.content, tbObj.key));
                }
                //rvPostAdapter.notifyDataSetChanged();

                post_loaded_count++;
                //load next item
                if (post_loaded_count == 5) {
                    //loadmore finish
                    rvPostAdapter.isLoading = false;
                    rvPostAdapter.notifyDataSetChanged();
                    fragmentCallBack.onLoadMoreFinish();
                }
                else if (rvPostAdapter.getItemCount() == key_result.size()) {
                    //already load all search result post
                    rvPostAdapter.isLoading = false;
                    rvPostAdapter.allItemLoaded = true;
                    rvPostAdapter.notifyDataSetChanged();
                    fragmentCallBack.onLoadMoreFinish();
                }
                else {
                    //continue load item
                    Query qrGetNextItemt = firebase_ref.orderByChild("key").equalTo(key_result.get(rvPostAdapter.getItemCount()));
                    qrGetNextItemt.addListenerForSingleValueEvent(postEvenListener);
                }
                //rvPostAdapter.isLoading = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //set call back cho adapter
        rvPostAdapter.setLoadMoreCallBack(new RVPostAdapter.ILoadMoreCallBack() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore: ");

                if (rvPostAdapter.getItemCount() == key_result.size()) {
                    //all item loaded
                    rvPostAdapter.allItemLoaded = true;
                    return;
                }
                Log.d(TAG, "onLoadMore: ");
                //add empty new feed such as facebook new feed
//                rvPostAdapter.addThongBao(null);
//                rvPostAdapter.addThongBao(null);

               // rvPostAdapter.notifyDataSetChanged();
                //will load .. item
                rvPostAdapter.itemLoadCount += RVTBAdapter.LOAD_MORE_DELTA;

                //query to get next new feed from ..
                post_loaded_count = 0;
                Query qrGetNextItemt = firebase_ref.orderByChild("key").equalTo(key_result.get(rvPostAdapter.getItemCount()));
                qrGetNextItemt.addListenerForSingleValueEvent(postEvenListener);
                fragmentCallBack.onLoadMore();
            }

            @Override
            public void onLoadMoreFinish() {
                fragmentCallBack.onLoadMoreFinish();
            }
        });
    }

    //load du lieu lan dau
    public void loadFirstTime() {
//        rvPostAdapter.addThongBao(null);
//        rvPostAdapter.addThongBao(null);
//        rvPostAdapter.notifyDataSetChanged();

        if (key_result.size() == 0)
            return;
        post_loaded_count = 0;
        rvPostAdapter.itemLoadCount = RVTBAdapter.LOAD_MORE_DELTA;
        Query qrGetNextItemt = firebase_ref.orderByChild("key").equalTo(key_result.get(rvPostAdapter.getItemCount()));
        qrGetNextItemt.addListenerForSingleValueEvent(postEvenListener);
        fragmentCallBack.onLoadMore();
    }

    //ham set call back
    private void setFragmentCallBack(RVTBAdapter.ILoadMoreCallBack fragmentCallBack) {
        this.fragmentCallBack = fragmentCallBack;
    }

    public void setPostsKey(ArrayList<String> arr_post_key) {
        this.key_result = arr_post_key;
    }

}
