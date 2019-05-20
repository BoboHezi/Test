package com.example.zhanbozhang.test.answer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * animation view, hint the action for answer or reject
 *
 * @author Eli Chang
 */
public class HintProgress extends View {

    private Paint mPaint;

    private final static int POINTS_COUNT = 3;

    // alpha animate
    private ValueAnimator mAnimator = ValueAnimator.ofInt(0, 510);
    private Point[] mPoints = new Point[POINTS_COUNT];
    private int[] mAlphas = new int[POINTS_COUNT];

    private int mViewWidth;
    private int mViewHeight;

    public HintProgress(Context context) {
        this(context, null);
    }

    public HintProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HintProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public HintProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);

        mAnimator.setDuration(1500);
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addUpdateListener((ValueAnimator animator) -> {
            int value = (int) animator.getAnimatedValue();

            for (int i = mAlphas.length - 1; i >= 0; i--) {
                mAlphas[i] = clamp(value, 0, 255);
                postInvalidate();
                value -= 50;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();
        int start1 = mViewWidth / 5;
        int start2 = mViewWidth * 4 / 15;
        int start3 = mViewWidth / 3;

        mPoints[0] = new Point(start1, mViewHeight / 2);
        mPoints[1] = new Point(start2, mViewHeight / 2);
        mPoints[2] = new Point(start3, mViewHeight / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // draw points
        for (int i = 0; i < mPoints.length; i++) {
            mPaint.setAlpha(mAlphas[i]);
            Point point = mPoints[i];
            canvas.drawCircle(point.x, point.y, i + 3, mPaint);
            canvas.drawCircle(mViewWidth - point.x, point.y, i + 3, mPaint);
        }
    }

    public void startAnimation() {
        if (mAnimator.isRunning()) {
            mAnimator.end();
        }
        mAnimator.start();
    }

    public void stopAnimation() {
        mAnimator.end();
        mAlphas = new int[POINTS_COUNT];
        postInvalidate();
    }

    public int clamp(int value, int min, int max) {
        if (value > max) {
            return max - value;
        }
        return Math.max(min, Math.min(value, max));
    }
}
