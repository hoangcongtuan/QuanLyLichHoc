package com.example.hoangcongtuan.quanlylichhoc.helper;

import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.adapter.RVPostAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.Post;
import com.example.hoangcongtuan.quanlylichhoc.models.PostObj;
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
    private RVPostAdapter rvtbAdapter;
    private DatabaseReference firebase_ref;
    private ValueEventListener postReceiveListener;
    private RVPostAdapter.ILoadMoreCallBack fragmentCallBack;
    private RVPostAdapter.ILoadMoreCallBack scrollToCallBack;

    private void setScrollToCallBack(RVPostAdapter.ILoadMoreCallBack scrollToCallBack) {
        this.scrollToCallBack = scrollToCallBack;
    }

    public LoadFeedHelper(RVPostAdapter adapter, DatabaseReference firebase_ref, RVPostAdapter.ILoadMoreCallBack fragmentCallBack) {
        this.rvtbAdapter = adapter;
        this.firebase_ref = firebase_ref;
        this.fragmentCallBack = fragmentCallBack;
        init();
    }

    public void init() {
        postReceiveListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> posts;

                posts = dataSnapshot.getChildren();
                ArrayList<Post> tmpPosts = new ArrayList<>();

                int count = 0;
                for (DataSnapshot dtSnapShot : posts) {
                    PostObj tbObj = dtSnapShot.getValue(PostObj.class);
                    tmpPosts.add(new Post(tbObj.day, tbObj.event, tbObj.content, tbObj.key));
                    count++;
                }

                //remove loading item
                rvtbAdapter.removeItem(rvtbAdapter.getItemCount() - 1);
                rvtbAdapter.removeItem(rvtbAdapter.getItemCount() - 1);

                for(Post post : tmpPosts) {
                    rvtbAdapter.addThongBao(post);
                    rvtbAdapter.notifyItemInserted(rvtbAdapter.getItemCount() - 1);
                }

                //all post is loaded?
                if (count < RVPostAdapter.LOAD_MORE_DELTA)
                    rvtbAdapter.allItemLoaded = true;
                rvtbAdapter.isLoading = false;

                if (scrollToCallBack != null)
                    scrollToCallBack.onLoadMoreFinish();

                if (!rvtbAdapter.isFirstTimeLoaded)
                    rvtbAdapter.isFirstTimeLoaded = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        rvtbAdapter.setLoadMoreCallBack(new RVPostAdapter.ILoadMoreCallBack() {
            @Override
            public void onLoadMore() {
                //add 2 empty post such as facebook new feed, REMEMBER it's TWO empty item
                rvtbAdapter.addThongBao(null);
                rvtbAdapter.getRecyclerView().post(new Runnable() {
                    @Override
                    public void run() {
                        rvtbAdapter.notifyItemInserted(rvtbAdapter.getItemCount() - 1);
                    }
                });
                rvtbAdapter.addThongBao(null);
                rvtbAdapter.getRecyclerView().post(new Runnable() {
                    @Override
                    public void run() {
                        rvtbAdapter.notifyItemInserted(rvtbAdapter.getItemCount() - 1);
                    }
                });

                //query to get next new feed from ..
                Query qrGetNextItemt = firebase_ref.orderByKey().startAt(String.valueOf(rvtbAdapter.getItemCount() - 2))
                        .limitToFirst(RVPostAdapter.LOAD_MORE_DELTA);
                qrGetNextItemt.addListenerForSingleValueEvent(postReceiveListener);
                fragmentCallBack.onLoadMore();
            }

            @Override
            public void onLoadMoreFinish() {
                fragmentCallBack.onLoadMoreFinish();
            }
        });
    }

    //call when using scroll to
    private void loadMore() {
        //called when call scroll to
        //add 2 empty post such as facebook new feed, REMEMBER it's TWO empty item
        rvtbAdapter.addThongBao(null);
        rvtbAdapter.notifyItemInserted(rvtbAdapter.getItemCount() - 1);
        rvtbAdapter.addThongBao(null);
        rvtbAdapter.notifyItemInserted(rvtbAdapter.getItemCount() - 1);

        Query qrGetNextItemt = firebase_ref.orderByKey().startAt(String.valueOf(rvtbAdapter.getItemCount() - 2))
                .limitToFirst(RVPostAdapter.LOAD_MORE_DELTA);
        qrGetNextItemt.addListenerForSingleValueEvent(postReceiveListener);
    }

    //scroll man hinh den thong bao co ma hash
    public void scrollTo(final String hash) {
        //set call back cho apdapter
        setScrollToCallBack(new RVPostAdapter.ILoadMoreCallBack() {
            @Override
            public void onLoadMore() {

            }

            //sau khi load them du lieu
            @Override
            public void onLoadMoreFinish() {
                ArrayList<Post> lstTBChung = rvtbAdapter.getLstPost();

                for(Post post : lstTBChung) {
                    if (hash.compareTo(post.getKey()) == 0) {
                        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(rvtbAdapter.getContext()) {
                            @Override
                            protected int getVerticalSnapPreference() {
                                return SNAP_TO_START;
                            }
                        };
                        int position = lstTBChung.indexOf(post);
                        smoothScroller.setTargetPosition(position);
                        //rvtbAdapter.highLightPost(position);

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
        //no need to call loaddmore() here, it will automatic call on the first load
        //loadMore();
    }

    //load du lieu lan dau
    public void loadFirstTime() {
        //add 2 empty post such as facebook new feed, REMEMBER it's TWO empty item
        rvtbAdapter.addThongBao(null);
        rvtbAdapter.notifyItemInserted(rvtbAdapter.getItemCount() - 1);
        rvtbAdapter.addThongBao(null);
        rvtbAdapter.notifyItemInserted(rvtbAdapter.getItemCount() - 1);

        Query qrGetNextItemt = firebase_ref.orderByKey().startAt(String.valueOf(rvtbAdapter.getItemCount() - 2))
                .limitToFirst(RVPostAdapter.LOAD_MORE_DELTA);
        qrGetNextItemt.addListenerForSingleValueEvent(postReceiveListener);
    }
}
