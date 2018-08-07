package com.example.hoangcongtuan.quanlylichhoc.adapter.RVClassAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
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
import com.example.hoangcongtuan.quanlylichhoc.utils.DBLopHPHelper;
import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by hoangcongtuan on 1/30/18.
 */

public class RVClassAdapter extends RecyclerView.Adapter<RVClassAdapter.ViewHolder> implements Observables {
    private final static int ACTION_ADD = 0;
    private final static int ACTION_REMOVE = 1;
    private ArrayList<LopHP> lstLopHP;
    private Context mContext;
    private ArrayList<RepositoryObserver> mObservers;

    private LopHP undo_LopHP;
    private int undo_position;
    private int last_action;

    public RVClassAdapter(Context context, ArrayList<LopHP> arrayList) {
        this.mContext = context;
        this.lstLopHP = arrayList;
        mObservers = new ArrayList<>();

        undo_LopHP = null;
        undo_position = -1;
        last_action = -1;

        notifyObservers();
    }

    @NonNull
    @Override
    public RVClassAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_item_hphan_with_background, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RVClassAdapter.ViewHolder holder, int position) {
        LopHP lopHP = lstLopHP.get(position);
        holder.tvMaHP.setText(lopHP.getMaHP());
        holder.tvTenHP.setText(lopHP.getTenHP());
        holder.tvIndex.setText(position + 1 + "");
        holder.tvTenGV.setText(lopHP.getTenGV());
        holder.tvTkb.setText(lopHP.getTkb());
    }

    public void copyFrom(ArrayList<LopHP> lstLopHP) {
        this.lstLopHP.clear();
        for(LopHP i: lstLopHP) {
            if (DBLopHPHelper.getsInstance().getLopHocPhan(i.getMaHP()) != null)
                this.lstLopHP.add(i);
        }
        //this.lstLopHP.addAll(lstLopHP);
        notifyDataSetChanged();
        notifyObservers();
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

    public int indexOf(String id) {
        //return lstLopHP.indexOf(lopHP);
        for (LopHP i : lstLopHP)
            if (i.getMaHP().compareTo(id) == 0)
                return lstLopHP.indexOf(i);
        return -1;
    }

    public void addItem(LopHP lopHP) {
        //find proper position
        int i;
        for(i = 0; i < lstLopHP.size(); i++) {
            if (lstLopHP.get(i).getTkb().compareTo(lopHP.getTkb()) > 0)
                break;
        }
        lstLopHP.add(i, lopHP);
        notifyItemInserted(i);
        notifyItemRangeChanged(i, lstLopHP.size() - i);

        undo_LopHP = lopHP;
        undo_position = i;
        last_action = ACTION_ADD;

        notifyObservers();
    }

    public void addItemWithoutSort(LopHP lopHP) {
        lstLopHP.add(lopHP);
        notifyItemInserted(lstLopHP.size() - 1);

        notifyObservers();
    }

    private void insertItem(int position, LopHP lopHP) {
        lstLopHP.add(position, lopHP);
        this.notifyItemInserted(position);
        this.notifyItemRangeChanged(position, lstLopHP.size() - position);

        undo_LopHP = lopHP;
        undo_position = position;
        last_action = ACTION_ADD;

        notifyObservers();
    }

    private void removeItem(int position) {
        undo_LopHP = lstLopHP.get(position);
        undo_position = position;

        lstLopHP.remove(position);
        this.notifyItemRemoved(position);
        notifyItemRangeChanged(position, lstLopHP.size() - position);
        last_action = ACTION_REMOVE;

        notifyObservers();
    }

    public void removeItem(String id) throws AppException {
        int index = getIndexOf(id);

        undo_LopHP = lstLopHP.get(index);
        undo_position = index;
        last_action = ACTION_REMOVE;

        lstLopHP.remove(index);
        this.notifyItemRemoved(index);
        notifyItemRangeChanged(index, lstLopHP.size() - index);

        notifyObservers();
    }

    private int getIndexOf(String id) throws AppException {
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

        notifyObservers();
    }

    public ArrayList<LopHP> getAllItem() {
        return lstLopHP;
    }

    public ArrayList<String> getAllId() {
        ArrayList<String> list = new ArrayList<>();
        for(LopHP i: lstLopHP)
            list.add(i.getMaHP());
        return list;
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

    @Override
    public int getItemCount() {
        return lstLopHP.size();
    }

    @Override
    public void registerObserver(RepositoryObserver repositoryObserver) {
        if (!mObservers.contains(repositoryObserver))
            mObservers.add(repositoryObserver);
    }

    @Override
    public void removeObserver(RepositoryObserver repositoryObserver) {
        if (mObservers.contains(repositoryObserver))
            mObservers.remove(repositoryObserver);
    }

    @Override
    public void notifyObservers() {
        for (RepositoryObserver observer: mObservers)
            observer.onDataStateChange(lstLopHP.isEmpty());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaHP, tvTenHP, tvTenGV, tvTkb, tvIndex;
        RelativeLayout viewBackground;
        public ConstraintLayout viewForeground;
        ViewHolder(View itemView) {
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