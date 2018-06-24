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
import com.example.hoangcongtuan.quanlylichhoc.exception.AppException;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;

import java.util.ArrayList;

/**
 * Created by hoangcongtuan on 1/30/18.
 */

public class RVHPhanAdapter extends RecyclerView.Adapter<RVHPhanAdapter.ViewHolder> {
    private final static int ACTION_ADD = 0;
    private final static int ACTION_REMOVE = 1;
    private ArrayList<LopHP> lstLopHP;
    private Context mContext;

    private LopHP undo_LopHP;
    private int undo_position;
    private int last_action;


    public RVHPhanAdapter(Context context, ArrayList<LopHP> arrayList) {
        this.mContext = context;
        this.lstLopHP = arrayList;

        undo_LopHP = null;
        undo_position = -1;
        last_action = -1;
    }

    @Override
    public RVHPhanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_hphan_with_background, parent, false);
        RVHPhanAdapter.ViewHolder viewHolder = new RVHPhanAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RVHPhanAdapter.ViewHolder holder, int position) {
        LopHP lopHP = lstLopHP.get(position);
        holder.tvMaHP.setText(lopHP.getMaHP());
        holder.tvTenHP.setText(lopHP.getTenHP());
        holder.tvIndex.setText(position + 1 + "");
        holder.tvTenGV.setText(lopHP.getTenGV());
        holder.tvTkb.setText(lopHP.getTkb());
    }

    public LopHP getItem(int postion) {
        return lstLopHP.get(postion);
    }

    public void updateItem(int position, LopHP lopHP) {
        lstLopHP.get(position).update(lopHP.maHP, lopHP.tenHP, lopHP.tenGV, lopHP.tkb);
        this.notifyItemChanged(position);
    }

    public LopHP getItem(String id) throws AppException {
        int index = getIndexOf(id);
        return lstLopHP.get(index);
    }

    public int indexOf(LopHP lopHP) {
        return lstLopHP.indexOf(lopHP);
    }

    public void addItem(LopHP lopHP) {
        //find proper position
        int i;
        for(i = 0; i < lstLopHP.size(); i++) {
            if (lstLopHP.get(i).getTkb().compareTo(lopHP.getTkb()) > 0)
                break;
        }

        lstLopHP.add(i, lopHP);

        //lstLopHP.add(lopHP);
        this.notifyItemInserted(i);
        notifyItemRangeChanged(i, lstLopHP.size() - 1);

        undo_LopHP = lopHP;
        undo_position = i;
        last_action = ACTION_ADD;
        //sortItem();
    }

    public void addItemWithoutSort(LopHP lopHP) {
        lstLopHP.add(lopHP);
        notifyItemInserted(lstLopHP.size() - 1);
        //sortItem();
    }

    public void insertItem(int position, LopHP lopHP) {
        lstLopHP.add(position, lopHP);
        this.notifyItemInserted(position);
        this.notifyItemRangeChanged(position, lstLopHP.size() - 1);

        undo_LopHP = lopHP;
        undo_position = position;
        last_action = ACTION_ADD;
    }

    public void removeItem(int position) {
        undo_LopHP = lstLopHP.get(position);
        undo_position = position;

        lstLopHP.remove(position);
        this.notifyItemRemoved(position);
        notifyItemRangeChanged(position, lstLopHP.size() - 1);
        last_action = ACTION_REMOVE;
    }

    public void removeItem(String id) throws AppException {
        int index = getIndexOf(id);

        undo_LopHP = lstLopHP.get(index);
        undo_position = index;
        last_action = ACTION_REMOVE;

        lstLopHP.remove(index);
        this.notifyItemRemoved(index);
        notifyItemRangeChanged(index, lstLopHP.size() - 1);
        //this.notifyDataSetChanged();
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

    public void undo() {
        switch (last_action) {
            case ACTION_ADD:
                removeItem(undo_position);
                break;
            case ACTION_REMOVE:
                insertItem(undo_position, undo_LopHP);
                break;
        }

        last_action = -1;
    }

    public void sortItem() {
        Utils.getsInstance(mContext).sortLHP(lstLopHP);
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
