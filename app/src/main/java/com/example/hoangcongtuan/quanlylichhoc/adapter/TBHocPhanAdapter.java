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

import static android.content.ContentValues.TAG;

/**
 * Created by hoangcongtuan on 9/7/17.
 * Adaper cho bang tin thong bao lop hoc phan
 */

public class TBHocPhanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ThongBao> lstThongBao;
    private OnLoadMoreListener onLoadMoreListener;
    private Context mContext;
    private final static int ITEM_LOADED = 0;
    private final static int ITEM_LOADING = 1;

    public boolean isLoading = false;
    private int lastVisibleItem;     //item cuoi cung
    public final static int LOAD_MORE_DELTA = 5;    //so luong tin moi lan load
    public int itemLoadCount = 5;       //so ban tin se load
    public int itemLoaded;              //so ban tin da load
    public boolean allItemLoaded;

    //hold noi dung cua moi ban tin
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

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void addThongBao(ThongBao tb) {
        lstThongBao.add(tb);
    }

    public void removeLastThongBao() {
        lstThongBao.remove(lstThongBao.size() - 1);
    }


    public TBHocPhanAdapter(RecyclerView recyclerView, Context context) {
        lstThongBao = new ArrayList<>();
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        this.mContext = context;
        isLoading = false;
        allItemLoaded = false;
        itemLoadCount = LOAD_MORE_DELTA;
        itemLoaded = 0;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                //Log.d(TAG, "onScrolled: last visible item: " + lastVisibleItem);
                if (itemLoadCount == (lastVisibleItem + 1)) {   //da keo xuong item cuoi cua ban tin
                    if (onLoadMoreListener != null) {           //load ban tin tiep theo
                        if (!allItemLoaded) {
                            isLoading = true;
                            Log.d(TAG, "onScrolled: load more");
                            onLoadMoreListener.onLoadMore();
                        }

                    }
                }

            }
        });
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    //neu thong bao chua load no se la null
    @Override
    public int getItemViewType(int position) {
        return lstThongBao.get(position) == null ? ITEM_LOADING : ITEM_LOADED;
    }

    //tao layout cua ban tin ung voi trang thai da load hay chua
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == ITEM_LOADED) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_thong_bao, parent, false);
            return new ThongBaoHolder(itemView);
        }
        else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_loading, parent, false);
            return new ThongBaoHolderLoading(itemView);
        }
    }

    //set cac thuoc tinh cho bang tin
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ThongBaoHolder) {

            final ThongBaoHolder thongBaoHolder = (ThongBaoHolder)holder;

            ThongBao tb = lstThongBao.get(position);
            thongBaoHolder.tvTBThoiGian.setText(tb.getStrDate());
            thongBaoHolder.tvTBTieuDe.setText(tb.getTittle());
            thongBaoHolder.tvThongBaoNoiDung.setText(tb.getContent());
            thongBaoHolder.btnDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupNewFeed(thongBaoHolder.btnDots);
                }
            });

        }
    }

    //show popup menu khi nhan vao bieu tuong 3 cham
    public void showPopupNewFeed(View view) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.inflate(R.menu.menu_new_feed);
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return lstThongBao.size();
    }

    public List<ThongBao> getLstThongBao() {
        return lstThongBao;
    }

    public void setLstThongBao(List<ThongBao> lstThongBao) {
        this.lstThongBao = lstThongBao;
    }

}
