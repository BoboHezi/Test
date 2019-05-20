package com.example.zhanbozhang.test.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.zhanbozhang.test.R;

/**
 * {@link ViewPager} useful for disabled swiping between pages.
 */
public class LockableViewPager extends ViewPager {

    private boolean swipingLocked;

    public LockableViewPager(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        TypedArray a =
                context.obtainStyledAttributes(attributeSet, R.styleable.LockableViewPager, 0, 0);
        int maxHeight = a.getDimensionPixelSize(R.styleable.LockableViewPager_maxHeight, -1);
        Log.i("elifli", "maxHeight: " + maxHeight);
        a.recycle();
    }

    public void setSwipingLocked(boolean swipingLocked) {
        this.swipingLocked = swipingLocked;
    }

    public boolean isSwipingLocked() {
        return swipingLocked;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return !swipingLocked && super.onInterceptTouchEvent(motionEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return !swipingLocked && super.onTouchEvent(motionEvent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightSpecMode == MeasureSpec.AT_MOST) {
            heightSpecSize = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > heightSpecSize) {
                    heightSpecSize = h;
                }
            }
        }
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpecSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
