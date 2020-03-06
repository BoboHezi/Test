package com.example.zhanbozhang.test.youxi;

import android.graphics.Rect;
import android.view.View;

public class FourSpacesItemDecoration extends android.support.v7.widget.RecyclerView.ItemDecoration {
    private int horizontalSpace;
    private int verticalSpace;
    private int mSpan;

    public FourSpacesItemDecoration(int horizontal, int vertical, int span) {
        horizontalSpace = horizontal;
        verticalSpace = vertical;
        mSpan = span;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, android.support.v7.widget.RecyclerView parent, android.support.v7.widget.RecyclerView.State state) {
        outRect.top = verticalSpace / 2;
        outRect.bottom = verticalSpace / 2;
        int position = parent.getChildLayoutPosition(view);
        if (position % mSpan == 0) {
            //第一列
            outRect.left = 0;
            outRect.right = horizontalSpace / 2;
        } else if ((position + 1) % mSpan == 0) {
            //最后一列
            outRect.left = horizontalSpace / 2;
            outRect.right = 0;
        } else {
            //其他列
            outRect.left = horizontalSpace / 2;
            outRect.right = horizontalSpace / 2;
        }
    }
}