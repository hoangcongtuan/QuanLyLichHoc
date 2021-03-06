package com.example.hoangcongtuan.quanlylichhoc.helper;

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

public class LoadSearchPostResultHelper {
    private final static String TAG = LoadSearchPostResultHelper.class.getName();
    private RVPostAdapter rvPostAdapter;
    private DatabaseReference firebase_ref;
    private ValueEventListener postEvenListener;
    private RVPostAdapter.ILoadMoreCallBack fragmentCallBack;
    private SearchPostCallBack callBack;
    private ArrayList<String> key_result;
    private ArrayList<Post>  retrievePosts = new ArrayList<>();
    private int post_loaded_count;  //count so luong item da load moi lan


    public LoadSearchPostResultHelper(RVPostAdapter adapter,
                                      DatabaseReference firebase_ref,
                                      RVPostAdapter.ILoadMoreCallBack fragmentCallBack,
                                      SearchPostCallBack callBack,
                                      ArrayList key_result) {
        this.rvPostAdapter = adapter;
        this.firebase_ref = firebase_ref;
        this.fragmentCallBack = fragmentCallBack;
        this.key_result = key_result;
        this.callBack = callBack;
        init();
    }

    public void init() {
        //call back khi tai thong bao ve
        postEvenListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PostObj tbObj;
                Iterable<DataSnapshot> posts = dataSnapshot.getChildren();

                for (DataSnapshot snapshot :
                        posts) {
                    tbObj = snapshot.getValue(PostObj.class);
                    retrievePosts.add(new Post(tbObj.day, tbObj.event, tbObj.content, tbObj.key));
                }
                post_loaded_count++;
                //load next item
                if (post_loaded_count == 5) {
                    rvPostAdapter.isLoading = false;
                    //remove empty post
                    rvPostAdapter.removeLast();
                    rvPostAdapter.notifyItemRemoved(rvPostAdapter.getItemCount());
                    rvPostAdapter.removeLast();
                    rvPostAdapter.notifyItemRemoved(rvPostAdapter.getItemCount());
                    //add post on retrievePost to recycleview
                    for(Post post: retrievePosts) {
                        rvPostAdapter.addThongBao(post);
                    }
                    rvPostAdapter.notifyItemRangeInserted(rvPostAdapter.getItemCount() - post_loaded_count,
                            rvPostAdapter.getItemCount() - 1);
                    //loadmore finish
                    fragmentCallBack.onLoadMoreFinish();
                }
                else if (rvPostAdapter.getItemCount() - 2 + post_loaded_count == key_result.size()) {
                    rvPostAdapter.isLoading = false;

                    //remove empty post
                    rvPostAdapter.removeLast();
                    rvPostAdapter.notifyItemRemoved(rvPostAdapter.getItemCount());
                    rvPostAdapter.removeLast();
                    rvPostAdapter.notifyItemRemoved(rvPostAdapter.getItemCount());

                    //add post on retrievePost to recycleview
                    for(Post post: retrievePosts) {
                        rvPostAdapter.addThongBao(post);
                    }

                    rvPostAdapter.notifyItemRangeInserted(rvPostAdapter.getItemCount() - post_loaded_count,
                            rvPostAdapter.getItemCount() - 1);

                    //already load all search result post
                    rvPostAdapter.allItemLoaded = true;
                    fragmentCallBack.onLoadMoreFinish();
                }
                else {
                    //continue load item
                    Query qrGetNextItemt = firebase_ref.orderByChild("key").equalTo(key_result.get(
                            rvPostAdapter.getItemCount() + post_loaded_count - 2)
                    );
                    qrGetNextItemt.addListenerForSingleValueEvent(postEvenListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //set call back cho adapter
        rvPostAdapter.setLoadMoreCallBack(new RVPostAdapter.ILoadMoreCallBack() {
            @Override
            public void onLoadMore() {
                rvPostAdapter.isLoading = true;
                if (rvPostAdapter.getItemCount() == key_result.size()) {
                    rvPostAdapter.isLoading = false;
                    rvPostAdapter.allItemLoaded = true;
                    return;
                }
                rvPostAdapter.addThongBao(null);
                rvPostAdapter.addThongBao(null);
                rvPostAdapter.getRecyclerView().post(new Runnable() {
                    @Override
                    public void run() {
                        //add empty new feed such as facebook new feed
                        rvPostAdapter.notifyItemInserted(rvPostAdapter.getItemCount() - 2);
                        rvPostAdapter.notifyItemInserted(rvPostAdapter.getItemCount() - 1);
                    }
                });

                //query to get next new feed from ..
                post_loaded_count = 0;
                retrievePosts.clear();
                Query qrGetNextItemt = firebase_ref.orderByChild("key").equalTo(key_result.get(rvPostAdapter.getItemCount() - 2));
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
        rvPostAdapter.isLoading = true;
        if (key_result.size() == 0) {
            rvPostAdapter.isLoading = false;
            //remove empty post
            rvPostAdapter.removeLast();
            rvPostAdapter.notifyItemRemoved(rvPostAdapter.getItemCount());
            rvPostAdapter.removeLast();
            rvPostAdapter.notifyItemRemoved(rvPostAdapter.getItemCount());
            rvPostAdapter.allItemLoaded = true;
            callBack.onNoResult();
            return;
        }
        post_loaded_count = 0;
        retrievePosts.clear();
        //getitemcount - 2 (sub null item)
        Query qrGetNextItemt = firebase_ref.orderByChild("key").equalTo(key_result.get(rvPostAdapter.getItemCount() - 2));
        qrGetNextItemt.addListenerForSingleValueEvent(postEvenListener);
        fragmentCallBack.onLoadMore();
        rvPostAdapter.isFirstTimeLoaded = true;
    }

    public interface SearchPostCallBack {
        void onNoResult();
    }

}
