package com.example.hoangcongtuan.quanlylichhoc.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.hoangcongtuan.quanlylichhoc.utils.Utils;

/*
* This class is a ScrollListener for RecyclerView that allows to show/hide
* views when list is scrolled. It assumes that you have added a header
* to your list. @see pl.michalz.hideonscrollexample.adapter.partone.RecyclerAdapter
* */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {

    private static final float HIDE_THRESHOLD = 10;
    private static final float SHOW_THRESHOLD = 70;
    private static final String TAG = HidingScrollListener.class.getName();

    private static int mToolbarOffset = 0;
    private static boolean mControlsVisible = true;
    private static int mToolbarHeight;
    private int mTotalScrolledDistance = 0;

    public HidingScrollListener(Context context) {
        mToolbarHeight = Utils.getToolbarHeight(context);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        if(newState == RecyclerView.SCROLL_STATE_IDLE) {
//            //Log.d(TAG, "onScrollStateChanged: ScrollY = " + recyclerView.ca);
//            //fix bug on the fly
//            if (!mControlsVisible && mTotalScrolledDistance < mToolbarHeight) {
//                recyclerView.scrollBy(0, mToolbarHeight - mTotalScrolledDistance);
//                if (mTotalScrolledDistance < 0) {
//                    mTotalScrolledDistance = 0;
//                }
//                mTotalScrolledDistance += (mToolbarHeight - mTotalScrolledDistance);
//
//
//
//            }

            if(mTotalScrolledDistance < mToolbarHeight) {
                setVisible();
            } else {
                if (mControlsVisible) {
                    if (mToolbarOffset > HIDE_THRESHOLD) {
                        setInvisible();
                    } else {
                        setVisible();
                    }
                } else {
                    if ((mToolbarHeight - mToolbarOffset) > SHOW_THRESHOLD) {
                        setVisible();
                    } else {
                        setInvisible();
                    }
                }
            }
        }

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        clipToolbarOffset();
        onMoved(mToolbarOffset);

        if((mToolbarOffset <mToolbarHeight && dy>0) || (mToolbarOffset >0 && dy<0)) {
            mToolbarOffset += dy;
        }
        if (mTotalScrolledDistance < 0) {
            mTotalScrolledDistance = 0;
        } else {
            mTotalScrolledDistance += dy;
        }

        //Log.d(TAG, "onScrolled: total scroll = " + mTotalScrolledDistance);
    }

    private void clipToolbarOffset() {
        if(mToolbarOffset > mToolbarHeight) {
            mToolbarOffset = mToolbarHeight;
        } else if(mToolbarOffset < 0) {
            mToolbarOffset = 0;
        }
    }

    public void showToolbar() {
        onShow();
        mToolbarOffset = 0;
    }

    private void setVisible() {
        if(mToolbarOffset > 0) {
            onShow();
            mToolbarOffset = 0;
        }
        mControlsVisible = true;
    }

    private void setInvisible() {
        if(mToolbarOffset < mToolbarHeight) {
            onHide();
            mToolbarOffset = mToolbarHeight;
        }
        mControlsVisible = false;
    }

    public abstract void onMoved(int distance);
    public abstract void onShow();
    public abstract void onHide();
}
