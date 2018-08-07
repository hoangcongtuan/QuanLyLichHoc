package com.example.hoangcongtuan.quanlylichhoc.activity.about;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoangcongtuan.quanlylichhoc.R;

import java.util.List;

/**
 * Created by florentchampigny on 24/04/15.
 */
public class TestRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> contents;

    TestRecyclerViewAdapter(List<Object> contents) {
        this.contents = contents;
    }


    @Override
    public int getItemCount() {
        return contents.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card_about, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }
}