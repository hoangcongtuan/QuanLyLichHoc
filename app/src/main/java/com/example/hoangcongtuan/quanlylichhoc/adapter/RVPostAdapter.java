package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.activity.alarm.AddAlarmActivity;
import com.example.hoangcongtuan.quanlylichhoc.models.Post;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 9/12/17.
 */

public class RVPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = RVPostAdapter.class.getName();
    private ArrayList<Post> lstPost;
    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
    }

    private LinearLayoutManager linearLayoutManager;

    //load more callback
    private ILoadMoreCallBack loadMoreCallBack;

    private RecyclerView recyclerView;

    //type of item
    private final static int ITEM_LOADED = 0;
    private final static int ITEM_LOADING = 1;

    //amount item load more
    public final static int LOAD_MORE_DELTA = 5;

    //returen code when create fast alarm
    public final static int RC_FAST_ADD_ALARM = 2;

    //true if post is loading more item
    public boolean isLoading;

    //thresh sold
    public int visibleThreshold = 1;

    //true if all new feed is loaded
    public boolean allItemLoaded;


    public RVPostAdapter(final RecyclerView recyclerView, Context context) {
        lstPost = new ArrayList<>();
        linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        this.mContext = context;

        this.recyclerView = recyclerView;
        isLoading = false;
        allItemLoaded = false;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d(TAG, "onScrolled: ");
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                //itemLoaded = linearLayoutManager.getItemCount();
                if (linearLayoutManager.getItemCount() <= (lastVisibleItem + visibleThreshold)
                        && !isLoading && !allItemLoaded) {
                    //load cac thong bao tiep theo
                    if (loadMoreCallBack != null) {
                            //chua load het cac thong bao
                            isLoading = true;
                            //call back toi ham load more ben fragment
                            loadMoreCallBack.onLoadMore();
                    }
                }
            }
        });

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    //hold view cho moi item da load trong recycle view
    public class ThongBaoHolder extends RecyclerView.ViewHolder {

        private TextView tvTBThoiGian, tvTBTieuDe, tvThongBaoNoiDung;
        private ImageView btnDots;
        public ThongBaoHolder(View itemView) {
            super(itemView);
            tvTBThoiGian = (TextView)itemView.findViewById(R.id.tvTBThoiGian);
            tvTBTieuDe = (TextView)itemView.findViewById(R.id.tvTBTieude);
            tvThongBaoNoiDung = (TextView)itemView.findViewById(R.id.tvTBNoiDung);
            btnDots = (ImageView)itemView.findViewById(R.id.btnDots);

            tvThongBaoNoiDung.setClickable(true);
            tvThongBaoNoiDung.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    //holder view cho item dang load trong recycle view
    public class ThongBaoHolderLoading extends RecyclerView.ViewHolder {

        public ThongBaoHolderLoading(View itemView) {
            super(itemView);
        }
    }


    //luc tao item cho recycleview, phu thuoc vao viewType
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_LOADING:
                //item dang load
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_loading, parent, false);
                return new ThongBaoHolderLoading(itemView);
            case ITEM_LOADED:
                //item da load
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_thong_bao, parent, false);
                return new ThongBaoHolder(itemView);
        }
        return null;
    }

    //hien thi du lieu len item
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ThongBaoHolder) {
            //neu la holder cua thong bao da load xong
            final ThongBaoHolder tbHolder = (ThongBaoHolder) holder;
            tbHolder.tvTBTieuDe.setText(lstPost.get(position).getTittle());
            tbHolder.tvTBThoiGian.setText(lstPost.get(position).getStrDate());
            tbHolder.tvThongBaoNoiDung.setText(Html.fromHtml(lstPost.get(position).getContent()));
            tbHolder.btnDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupNewFeed(tbHolder.btnDots, position);
                }
            });
        }
    }

    //menu xuat hien khi nhan button ... tren bang tin
    public void showPopupNewFeed(final View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.inflate(R.menu.menu_post);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_nhac_toi:
                        Intent addAlarmIntent = new Intent(mContext, AddAlarmActivity.class);
                        addAlarmIntent.putExtra("tieu_de", lstPost.get(position).getTittle());
                        addAlarmIntent.putExtra("noi_dung", Html.fromHtml(lstPost.get(position).getContent()).toString());
                        ((Activity)mContext).startActivityForResult(addAlarmIntent, RC_FAST_ADD_ALARM);
                        break;

                }
                return true;
            }
        });
        popupMenu.show();
    }

    //ham set callBack
    public void setLoadMoreCallBack(ILoadMoreCallBack loadMoreCallBack) {
        this.loadMoreCallBack = loadMoreCallBack;
    }

    @Override
    public int getItemCount() {
        return lstPost.size();
    }

    public void setLstPost(ArrayList<Post> lstPost) {
        this.lstPost = lstPost;
    }

    public ArrayList<Post> getLstPost() {
        return lstPost;
    }

    public void removeLast() {
        lstPost.remove(lstPost.size() - 1);
    }

    public void addThongBao(Post tb) {
        lstPost.add(tb);
    }

    public void removeAll() {
        lstPost.clear();
        isLoading = false;
        allItemLoaded = false;
        notifyDataSetChanged();
    }

    //interface dung de call back moi khi load them thong bao
    public interface ILoadMoreCallBack {
        void onLoadMore();
        void onLoadMoreFinish();
    }



    //tra ve trang thai cua thong bao, tu do xac dinh dung viewholder nao
    @Override
    public int getItemViewType(int position) {
        return lstPost.get(position) == null ? ITEM_LOADING : ITEM_LOADED;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
