package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.ThongBao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hoangcongtuan on 9/7/17.
 */

public class TBHocPhanAdapter extends RecyclerView.Adapter<TBHocPhanAdapter.ThongBaoHolder> {

    private List<ThongBao> lstThongBao;

    public class ThongBaoHolder extends RecyclerView.ViewHolder {

        private TextView tvTBThoiGian, tvTBTieuDe, tvThongBaoNoiDung;
        public ThongBaoHolder(View itemView) {
            super(itemView);
            tvTBThoiGian = (TextView)itemView.findViewById(R.id.tvTBThoiGian);
            tvTBTieuDe = (TextView)itemView.findViewById(R.id.tvTBTieude);
            tvThongBaoNoiDung = (TextView)itemView.findViewById(R.id.tvTBNoiDung);
        }
    }

    public void addThongBao(ThongBao tb) {
        lstThongBao.add(tb);
    }

    public TBHocPhanAdapter() {
        lstThongBao = new ArrayList<>();
    }
    @Override
    public ThongBaoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_thong_bao, parent, false);
        return new ThongBaoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ThongBaoHolder holder, int position) {
        ThongBao tb = lstThongBao.get(position);
        holder.tvTBThoiGian.setText(tb.getStrDate());
        holder.tvTBTieuDe.setText(tb.getTittle());
        holder.tvThongBaoNoiDung.setText(tb.getContent());
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
