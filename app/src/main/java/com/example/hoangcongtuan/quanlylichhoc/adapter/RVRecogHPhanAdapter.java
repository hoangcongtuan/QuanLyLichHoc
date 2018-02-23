package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;

import java.util.ArrayList;

public class RVRecogHPhanAdapter extends RecyclerView.Adapter<RVRecogHPhanAdapter.ViewHolder> {

    private Context context;
    private ArrayList<LopHP> lstLopHP;

    public RVRecogHPhanAdapter(Context context, ArrayList<LopHP> arrayList) {
        this.context = context;
        this.lstLopHP = arrayList;
    }

//    @Override
//    public View getView(final int position, View convertView, final ViewGroup parent) {
//        ViewHolder viewHolder;
//        if (convertView == null) {
//            viewHolder = new ViewHolder();
//            LayoutInflater inflater = LayoutInflater.from(getContext());
//            convertView = inflater.inflate(R.layout.item_hphan_with_background, parent, false);
//
//            viewHolder.tenHP = convertView.findViewById(R.id.tvTenHP);
//            viewHolder.tenGV = convertView.findViewById(R.id.tvTenGV);
//            viewHolder.maHP = convertView.findViewById(R.id.tvMaHP);
//            viewHolder.tkb = convertView.findViewById(R.id.tvTkb);
//            viewHolder.tvIndex = convertView.findViewById(R.id.tvIndex);
//
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//        LopHP lopHP = getItem(position);
//        if (lopHP != null) {
//            viewHolder.tenHP.setText(lopHP.tenHP);
//            viewHolder.tenGV.setText(lopHP.tenGV);
//            viewHolder.maHP.setText(lopHP.maHP);
//            viewHolder.tkb.setText(lopHP.tkb);
//            viewHolder.tvIndex.setText((position + 1) + "");
//        }
//        return convertView;
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_hphan_with_background, parent, false);
        RVRecogHPhanAdapter.ViewHolder viewHolder = new RVRecogHPhanAdapter.ViewHolder(view);
        return viewHolder;
    }

    public void removeAllItem() {
        lstLopHP.clear();
        this.notifyDataSetChanged();
    }

    public void addItem(LopHP lopHP) {
        lstLopHP.add(lopHP);
        this.notifyItemInserted(lstLopHP.size() - 1);
    }

    @Override
    public int getItemCount() {
        return lstLopHP.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LopHP lopHP = lstLopHP.get(position);
        holder.tvMaHP.setText(lopHP.getMaHP());
        holder.tvTenHP.setText(lopHP.getTenHP());
        holder.tvTkb.setText(lopHP.getTkb());
        holder.tvTenGV.setText(lopHP.getTenGV());
        holder.tvIndex.setText(position + "");
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
