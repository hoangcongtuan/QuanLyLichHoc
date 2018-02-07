package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.exception.AppException;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 1/30/18.
 */

public class RVTKBieuAdapter extends RecyclerView.Adapter<RVTKBieuAdapter.ViewHolder> {
    private ArrayList<LopHP> lstLopHP;
    private Context mContext;

    public RVTKBieuAdapter(Context context, ArrayList<LopHP> arrayList) {
        this.mContext = context;
        this.lstLopHP = arrayList;
    }

    @Override
    public RVTKBieuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.lop_hp_item, parent, false);
        RVTKBieuAdapter.ViewHolder viewHolder = new RVTKBieuAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RVTKBieuAdapter.ViewHolder holder, int position) {
        LopHP lopHP = lstLopHP.get(position);
        holder.tvMaHP.setText(lopHP.getMaHP());
        holder.tvTenHP.setText(lopHP.getTenHP());
        holder.tvIndex.setText(position + "");
        holder.tvTenGV.setText(lopHP.getTenGV());
        holder.tvTkb.setText(lopHP.getTkb());
    }

    public LopHP getItem(int postion) {
        return lstLopHP.get(postion);
    }

    public LopHP getItem(String id) throws AppException {
        int index = getIndexOf(id);
        return lstLopHP.get(index);
    }


    public void addItem(LopHP lopHP) {
        lstLopHP.add(lopHP);
        this.notifyItemInserted(lstLopHP.size());
        sortItem();
    }

    public void removeItem(int position) {
        lstLopHP.remove(position);
        this.notifyItemRemoved(position);
    }

    public void removeItem(String id) throws AppException {
        int index = getIndexOf(id);
        lstLopHP.remove(index);
        this.notifyItemRemoved(index);
    }

    public int getIndexOf(String id) throws AppException {
        for (LopHP lopHP : lstLopHP) {
            if (lopHP.getMaHP().equals(id))
                return lstLopHP.indexOf(lopHP);
        }
        throw new AppException(
                mContext.getResources().getString(
                        R.string.sorry_error
                )
        );
    }

    public void removeAllItem() {
        lstLopHP.clear();
        this.notifyDataSetChanged();
    }

    public ArrayList<LopHP> getAllItem() {
        return lstLopHP;
    }

    public void restoreItem(LopHP lopHP, int position) {
        lstLopHP.add(position, lopHP);
        this.notifyItemInserted(position);
        sortItem();

    }

    public void sortItem() {
        Utils.sortLHP(lstLopHP);
        this.notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return lstLopHP.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaHP, tvTenHP, tvTenGV, tvTkb, tvIndex;
        public RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;
        public ViewHolder(View itemView) {
            super(itemView);
            viewBackground = itemView.findViewById(R.id.background);
            viewForeground = itemView.findViewById(R.id.foreground);
            tvMaHP = itemView.findViewById(R.id.tvMaHP);
            tvTenHP = itemView.findViewById(R.id.tvTenHP);
            tvTenGV = itemView.findViewById(R.id.tvTenGV);
            tvTkb = itemView.findViewById(R.id.tvTkb);
            tvIndex = itemView.findViewById(R.id.tvIndex);
        }
    }


}
