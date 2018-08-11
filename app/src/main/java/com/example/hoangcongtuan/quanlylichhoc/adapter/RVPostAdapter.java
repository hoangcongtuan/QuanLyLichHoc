package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
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
    private final static int ITEM_LOADED = 0;
    private final static int ITEM_LOADING = 1;
    public final static int RC_FAST_ADD_ALARM = 2;
    public final static int LOAD_MORE_DELTA = 5;
    private final static int VISIBLE_THRESHOLD = 1;

    private ArrayList<Post> lstPost;
    private Context mContext;
    private LinearLayoutManager linearLayoutManager;
    private ILoadMoreCallBack loadMoreCallBack;
    private RecyclerView recyclerView;
    public boolean isLoading;
    public boolean allItemLoaded;
    public boolean isFirstTimeLoaded = false;

    public RVPostAdapter(final RecyclerView recyclerView, Context context) {
        this.lstPost = new ArrayList<>();
        this.linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        this.mContext = context;
        this.recyclerView = recyclerView;
        this.isLoading = false;
        this.allItemLoaded = false;

        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (linearLayoutManager.getItemCount() <= (lastVisibleItem + VISIBLE_THRESHOLD)
                        && !isLoading && !allItemLoaded && isFirstTimeLoaded) {
                    if (loadMoreCallBack != null) {
                            isLoading = true;
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

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTBThoiGian, tvTBTieuDe, tvThongBaoNoiDung;
        private ImageView btnDots;

        PostViewHolder(View itemView) {
            super(itemView);

            tvTBThoiGian = itemView.findViewById(R.id.tvTBThoiGian);
            tvTBTieuDe = itemView.findViewById(R.id.tvTBTieude);
            tvThongBaoNoiDung = itemView.findViewById(R.id.tvTBNoiDung);
            btnDots = itemView.findViewById(R.id.btnDots);

            tvThongBaoNoiDung.setClickable(true);
            tvThongBaoNoiDung.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public class PostLoadingViewHolder extends RecyclerView.ViewHolder {
        PostLoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_LOADING:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_loading, parent, false);
                return new PostLoadingViewHolder(itemView);

            case ITEM_LOADED:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_thong_bao, parent, false);
                return new PostViewHolder(itemView);
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_loading, parent, false);
                return new PostLoadingViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PostViewHolder) {
            final PostViewHolder tbHolder = (PostViewHolder) holder;
            tbHolder.tvTBTieuDe.setText(lstPost.get(position).getTittle());
            tbHolder.tvTBThoiGian.setText(lstPost.get(position).getStrDate());
            tbHolder.tvThongBaoNoiDung.setText(Html.fromHtml(lstPost.get(position).getContent()));
            tbHolder.btnDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupNewFeed(tbHolder.btnDots, holder.getAdapterPosition());
                }
            });
        }
    }

    private void showPopupNewFeed(final View view, final int position) {
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

    public Context getContext() {
        return mContext;
    }

    public void removeAll() {
        lstPost.clear();
        isLoading = false;
        allItemLoaded = false;
        notifyDataSetChanged();
    }

    public void removeItem(int index) {
        lstPost.remove(index);
        this.notifyItemRemoved(index);
    }

    //interface dung de call back moi khi load them thong bao
    public interface ILoadMoreCallBack {
        void onLoadMore();
        void onLoadMoreFinish();
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return linearLayoutManager;
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
