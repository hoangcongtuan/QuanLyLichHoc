package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.example.hoangcongtuan.quanlylichhoc.activity.Alarm.AddAlarmActivity;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBao;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 9/7/17.
 * Adaper cho bang tin thong bao lop hoc phan
 */

public class RVTBHPhanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ThongBao> lstThongBao;
    private Context mContext;
    private final static int ITEM_LOADED = 0;
    private final static int ITEM_LOADING = 1;

    public boolean isLoading;
    private int lastVisibleItem;     //item cuoi cung
    public final static int LOAD_MORE_DELTA = 5;    //so luong tin moi lan load
    public int itemLoadCount;       //so ban tin se load
    public int itemLoaded;              //so ban tin da load
    public boolean allItemLoaded;

    private RVTBChungAdapter.ICallBack iCallBack;

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

            tvThongBaoNoiDung.setClickable(true);
            tvThongBaoNoiDung.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    //ban tin rong (dang load)
    public class ThongBaoHolderLoading extends RecyclerView.ViewHolder {

        public ThongBaoHolderLoading(View itemView) {
            super(itemView);
        }
    }


    public RVTBHPhanAdapter(RecyclerView recyclerView, Context context) {
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
                if (itemLoadCount == (lastVisibleItem + 1)) {   //da keo xuong item cuoi cua ban tin
                    if (iCallBack != null) {           //load ban tin tiep theo
                        if (!allItemLoaded) {
                            isLoading = true;
                            iCallBack.onLoadMore();
                        }

                    }
                }
            }
        });
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ThongBaoHolder) {

            final ThongBaoHolder thongBaoHolder = (ThongBaoHolder)holder;

            ThongBao tb = lstThongBao.get(position);
            thongBaoHolder.tvTBThoiGian.setText(tb.getStrDate());
            thongBaoHolder.tvTBTieuDe.setText(tb.getTittle());
            thongBaoHolder.tvThongBaoNoiDung.setText(Html.fromHtml(tb.getContent()));
            thongBaoHolder.btnDots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupNewFeed(thongBaoHolder.btnDots, position);
                }
            });

        }
    }

    //show popup menu khi nhan vao bieu tuong 3 cham
    public void showPopupNewFeed(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.inflate(R.menu.menu_new_feed);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_nhac_toi:
                        Intent addAlarmIntent = new Intent(mContext, AddAlarmActivity.class);
                        addAlarmIntent.putExtra("tieu_de", lstThongBao.get(position).getTittle());
                        addAlarmIntent.putExtra("noi_dung", Html.fromHtml(lstThongBao.get(position).getContent()).toString());
                        ((Activity)mContext).startActivityForResult(addAlarmIntent, RVTBChungAdapter.RC_FAST_ADD_ALARM);
                        break;

                }
                return true;
            }
        });
        popupMenu.show();
    }


    @Override
    public int getItemCount() {
        return lstThongBao.size();
    }

    //neu thong bao chua load no se la null
    @Override
    public int getItemViewType(int position) {
        return lstThongBao.get(position) == null ? ITEM_LOADING : ITEM_LOADED;
    }

    public ArrayList<ThongBao> getLstThongBao() {
        return lstThongBao;
    }

    public void setLstThongBao(ArrayList<ThongBao> lstThongBao) {
        this.lstThongBao = lstThongBao;
    }


    public void addThongBao(ThongBao tb) {
        lstThongBao.add(tb);
    }

    public void removeLastThongBao() {
        lstThongBao.remove(lstThongBao.size() - 1);
    }

    public void setiCallBack(RVTBChungAdapter.ICallBack iCallBack) {
        this.iCallBack = iCallBack;
    }

}
