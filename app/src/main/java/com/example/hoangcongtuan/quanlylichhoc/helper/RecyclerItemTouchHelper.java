package com.example.hoangcongtuan.quanlylichhoc.helper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.hoangcongtuan.quanlylichhoc.adapter.RVTKBieuAdapter;
import com.example.hoangcongtuan.quanlylichhoc.adapter.ReminderAdapter;
import com.example.hoangcongtuan.quanlylichhoc.models.Reminder;

/**
 * Created by hoangcongtuan on 1/6/18.
 */

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener listener;

    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * {@link #getSwipeDirs(RecyclerView, ViewHolder)}
     * and / or {@link #getDragDirs(RecyclerView, ViewHolder)}.
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     */
    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder != null) {
            if (viewHolder instanceof ReminderAdapter.ViewHolder) {
                View foregroundView = ((ReminderAdapter.ViewHolder)viewHolder).viewForeground;
                getDefaultUIUtil().onSelected(foregroundView);
            } else if (viewHolder instanceof RVTKBieuAdapter.ViewHolder) {
                View foregroundView = ((RVTKBieuAdapter.ViewHolder)viewHolder).viewForeground;
                getDefaultUIUtil().onSelected(foregroundView);
            }

        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder
            , float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (viewHolder instanceof ReminderAdapter.ViewHolder) {
            View foregroundView = ((ReminderAdapter.ViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        } else if (viewHolder instanceof RVTKBieuAdapter.ViewHolder) {
            View foregroundView = ((RVTKBieuAdapter.ViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //super.clearView(recyclerView, viewHolder);
        if (viewHolder instanceof ReminderAdapter.ViewHolder) {
            View foregroundView = ((ReminderAdapter.ViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().clearView(foregroundView);
        } else if (viewHolder instanceof RVTKBieuAdapter.ViewHolder) {
            View foregroundView = ((RVTKBieuAdapter.ViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().clearView(foregroundView);
        }

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (viewHolder instanceof ReminderAdapter.ViewHolder) {
            View foregroundView = ((ReminderAdapter.ViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        } else if (viewHolder instanceof RVTKBieuAdapter.ViewHolder) {
            View foregroundView = ((RVTKBieuAdapter.ViewHolder)viewHolder).viewForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }
}
