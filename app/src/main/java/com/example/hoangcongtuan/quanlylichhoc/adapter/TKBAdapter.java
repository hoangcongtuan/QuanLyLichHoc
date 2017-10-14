package com.example.hoangcongtuan.quanlylichhoc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.hoangcongtuan.quanlylichhoc.R;
import com.example.hoangcongtuan.quanlylichhoc.models.LopHP;

import java.util.ArrayList;

public class TKBAdapter extends ArrayAdapter<LopHP> {
    private ArrayList<LopHP> arrayList;
    public TKBAdapter(Context context, int resource, ArrayList<LopHP> arrayList) {
        super(context, resource, arrayList);
        this.arrayList = arrayList;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.lop_hp_item, parent, false);

            viewHolder.tenHP = convertView.findViewById(R.id.tenHP);
            viewHolder.tenGV = convertView.findViewById(R.id.tenGV);
            viewHolder.maHP  = convertView.findViewById(R.id.maHP);
            viewHolder.tkb   = convertView.findViewById(R.id.tkb);
            viewHolder.btnRemove   = convertView.findViewById(R.id.btnRemove);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LopHP lopHP = getItem(position);
        if (lopHP != null){
            viewHolder.tenHP.setText(lopHP.tenHP);
            viewHolder.tenGV.setText(lopHP.tenGV);
            viewHolder.maHP.setText(lopHP.maHP);
            viewHolder.tkb.setText(lopHP.tkb);
            viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
        return convertView;
    }
    private class ViewHolder {
        TextView maHP, tenHP, tenGV, tkb;
        Button btnRemove;
    }
}
