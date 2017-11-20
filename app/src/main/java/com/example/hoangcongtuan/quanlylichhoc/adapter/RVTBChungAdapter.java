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
 * Created by hoangcongtuan on 9/12/17.
 */

public class RVTBChungAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = RVTBChungAdapter.class.getName();

    private ArrayList<ThongBao> lstThongBao;
    private Context mContext;

    LinearLayoutManager linearLayoutManager;

    private ICallBack ICallBack;

    private final static int ITEM_LOADED = 0;
    private final static int ITEM_LOADING = 1;
    public final static int LOAD_MORE_DELTA = 5;
    public final static int RC_FAST_ADD_ALARM = 2;

    public boolean isLoading;
    private int lastVisibleItem;
    public int itemLoadCount;
    public int itemLoaded;
    public boolean allItemLoaded;


    public RVTBChungAdapter(RecyclerView recyclerView, Context context) {

        lstThongBao = new ArrayList<>();
        linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
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
                    //load cac thong bao tiep theo
                    if (ICallBack != null) {
                        if (!allItemLoaded) {
                            //chua load het cac thong bao
                            isLoading = true;
                            //Log.d(TAG, "onScrolled: ");
                            //call back toi
                            ICallBack.onLoadMore();
                        }

                    }
                }
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
            tbHolder.tvTBTieuDe.setText(lstThongBao.get(position).getTittle());
            tbHolder.tvTBThoiGian.setText(lstThongBao.get(position).getStrDate());
            tbHolder.tvThongBaoNoiDung.setText(Html.fromHtml(lstThongBao.get(position).getContent()));
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
        popupMenu.inflate(R.menu.menu_new_feed);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_nhac_toi:
                        Intent addAlarmIntent = new Intent(mContext, AddAlarmActivity.class);
                        addAlarmIntent.putExtra("tieu_de", lstThongBao.get(position).getTittle());
                        addAlarmIntent.putExtra("noi_dung", lstThongBao.get(position).getContent());
                        ((Activity)mContext).startActivityForResult(addAlarmIntent, RC_FAST_ADD_ALARM);
                        break;

                }
                return true;
            }
        });
        popupMenu.show();
    }

    //ham set callBack
    public void setICallBack(ICallBack ICallBack) {
        this.ICallBack = ICallBack;
    }

    @Override
    public int getItemCount() {
        return lstThongBao.size();
    }

    public void setLstThongBao(ArrayList<ThongBao> lstThongBao) {
        this.lstThongBao = lstThongBao;
    }

    public ArrayList<ThongBao> getLstThongBao() {
        return lstThongBao;
    }

    public void removeLast() {
        lstThongBao.remove(lstThongBao.size() - 1);
    }

    public void addThongBao(ThongBao tb) {
        lstThongBao.add(tb);
    }

    //interface dung de call back moi khi load them thong bao
    public interface ICallBack {
        void onLoadMore();
        void onLoadMoreFinish();
        void onFirstLoadFinish();
    }



    //tra ve trang thai cua thong bao, tu do xac dinh dung viewholder nao
    @Override
    public int getItemViewType(int position) {
        return lstThongBao.get(position) == null ? ITEM_LOADING : ITEM_LOADED;
    }
}
