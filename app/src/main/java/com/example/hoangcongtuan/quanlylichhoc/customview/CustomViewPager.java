package com.example.hoangcongtuan.quanlylichhoc.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by hoangcongtuan on 9/27/17.
 * disable tinh nang slide
 */

public class CustomViewPager extends ViewPager {
    //true -> enable tinh nang slide
    private boolean enable;
    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        enable = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.enable)
            return super.onTouchEvent(ev);
        return false;
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        if (this.enable)
            return super.onInterceptHoverEvent(event);
        return false;
    }

    public void setPagingEnable(boolean enable) {
        this.enable = enable;
    }
}
