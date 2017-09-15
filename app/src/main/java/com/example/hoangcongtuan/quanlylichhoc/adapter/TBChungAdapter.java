package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoangcongtuan on 9/12/17.
 */

public class TBChungAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static String TAG = TBChungAdapter.class.getName();

    private List<ThongBao> lstThongBao;
    private Context mContext;

    private OnLoadMoreListener onLoadMoreListener;

    private final static int ITEM_LOADED = 0;
    private final static int ITEM_LOADING = 1;
    public final static int LOAD_MORE_DELTA = 5;

    public boolean isLoading = false;
    private int lastVisibleItem;
    public int itemLoadCount = 5;
    public int itemLoaded;
    public boolean allItemLoaded;


    public TBChungAdapter(RecyclerView recyclerView, Context context) {
        lstThongBao = new ArrayList<>();
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        this.mContext = context;
        isLoading = false;
        itemLoadCount = LOAD_MORE_DELTA;
        itemLoaded = 0;
        allItemLoaded = false;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (itemLoadCount == (lastVisibleItem + 1)) {
                    if (onLoadMoreListener != null) {
                        if (!allItemLoaded) {
                            isLoading = true;
                            Log.d(TAG, "onScrolled: ");
                            onLoadMoreListener.onLoadMore();
                        }

                    }
                }
            }
        });
    }

    public void removeLast() {
        lstThongBao.remove(lstThongBao.size() - 1);
    }

    public void addThongBao(ThongBao tb) {
        lstThongBao.add(tb);
    }

    public class ThongBaoHolder extends RecyclerView.ViewHolder {

        private TextView tvTBThoiGian, tvTBTieuDe, tvThongBaoNoiDung;
        private ImageView btnDots;
        public ThongBaoHolder(View itemView) {
            super(itemView);
            tvTBThoiGian = (TextView)itemView.findViewById(R.id.tvTBThoiGian);
            tvTBTieuDe = (TextView)itemView.findViewById(R.id.tvTBTieude);
            tvThongBaoNoiDung = (TextView)itemView.findViewById(R.id.tvTBNoiDung);
            btnDots = (ImageView)itemView.findViewById(R.id.btnDots);
        }
    }

    //ban tin rong (dang load)
    public class ThongBaoHolderLoading extends RecyclerView.ViewHolder {

        public ThongBaoHolderLoading(View itemView) {
            super(itemView);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    @Override
    public int getItemViewType(int position) {
        return lstThongBao.get(position) == null ? ITEM_LOADING : ITEM_LOADED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_LOADING:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_loading, parent, false);
                return new ThongBaoHolderLoading(itemView);
            case ITEM_LOADED:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_thong_bao, parent, false);
                return new ThongBaoHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ThongBaoHolder) {
            final ThongBaoHolder tbHolder = (ThongBaoHolder) holder;
            tbHolder.tvTBTieuDe.setText(lstThongBao.get(position).getTittle());
            tbHolder.tvTBThoiGian.setText(lstThongBao.get(position).getStrDate());
            tbHolder.tvThongBaoNoiDung.setText(lstThongBao.get(position).getContent());
            tbHolder.btnDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupNewFeed(tbHolder.btnDots);
                }
            });
        }
    }

    public void showPopupNewFeed(View view) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.inflate(R.menu.menu_new_feed);
        popupMenu.show();
    }

    public void setOnLoadMoreListentner(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return lstThongBao.size();
    }

    public void setLstThongBao(List<ThongBao> lstThongBao) {
        this.lstThongBao = lstThongBao;
    }
}
