package com.example.zhanbozhang.test.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * create by eli.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class BatteryView extends View {

    private final String TAG = this.getClass().getSimpleName();

    private Paint mPaint;

    private Context mContext;

    // text size for battery percent
    private int mTextSize = 42;

    private int mViewHeight;
    private int mViewWidth;

    // battery info
    private int percent = 0;
    private String mBatteryPercent;

    public BatteryView(Context context) {
        this(context, null);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BatteryView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mContext.registerReceiver(mBatteryReceiver, filter);

        BatteryManager bm = (BatteryManager) mContext.getSystemService(Context.BATTERY_SERVICE);
        percent = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        mBatteryPercent = percent + "%";

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // get window mode and size
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        // when width/height mode be wrap_content, redefine size
        if (widthSpecMode == MeasureSpec.AT_MOST) {
            int textWidth = (int) mPaint.measureText("100%");
            widthSpecSize = (int) (textWidth * 1.1 + mPaint.measureText("9%"));
        }
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            heightSpecSize = mTextSize;
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);

        mViewWidth = getMeasuredWidth();
        mViewHeight = getMeasuredHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);

        // draw background (totally dark)
        mPaint.setStyle(Paint.Style.FILL);
        c.drawColor(Color.argb(255, 0, 0, 0));

        // draw text
        Rect textRect = new Rect();
        mPaint.getTextBounds(mBatteryPercent, 0, mBatteryPercent.length(), textRect);
        c.drawText(mBatteryPercent, 0, (mViewHeight + textRect.height()) / 2, mPaint);

        // draw battery head
        int textWidth = (int) mPaint.measureText(mBatteryPercent);
        Rect headRect = new Rect((int) (textWidth * 1.1), mViewHeight / 3, (int) (textWidth * 1.15), mViewHeight * 2 / 3);
        c.drawRect(headRect, mPaint);

        // draw battery body
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(3);
        Rect bodyRect = new Rect();
        int bodyWidth = (int) mPaint.measureText("9%");
        bodyRect.left = (int) (textWidth * 1.15);
        bodyRect.top = (mViewHeight - textRect.height()) / 2;
        bodyRect.right = bodyRect.left + bodyWidth;
        bodyRect.bottom = (mViewHeight + textRect.height()) / 2;
        c.drawRect(bodyRect, mPaint);
        c.drawLine(bodyRect.right, bodyRect.top, (float) (bodyRect.right * 1.02), bodyRect.top, mPaint);
        c.drawLine(bodyRect.right, bodyRect.bottom, (float) (bodyRect.right * 1.02), bodyRect.bottom, mPaint);

        // draw battery content
        mPaint.setStyle(Paint.Style.FILL);
        Rect fillRect = new Rect(bodyRect);
        int almost = percent + ((5 - percent % 5) == 5 ? 0 : (5 - percent % 5));
        fillRect.left = bodyRect.right - (bodyWidth * almost / 100);
        c.drawRect(fillRect, mPaint);

        // drawing to view
        int leftOffset = (int) ((mViewWidth - bodyRect.right * 1.02) / 2);
        if (leftOffset > 0) {
            Bitmap fillBitmap = Bitmap.createBitmap(leftOffset, getHeight(), Bitmap.Config.ARGB_8888);
            Canvas fillCanvas = new Canvas(fillBitmap);
            fillCanvas.drawColor(Color.argb(255, 0, 0, 0));
            canvas.drawBitmap(fillBitmap, 0, 0, mPaint);
        }
        canvas.drawBitmap(bitmap, leftOffset, 0, mPaint);
    }

    BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);
            int scale = intent.getIntExtra("scale", 0);

            if (level > 0 && scale > 0 && level <= scale) {
                percent = level * 100 / scale;
                mBatteryPercent = percent + "%";
                postInvalidate();
            }
        }
    };

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext.unregisterReceiver(mBatteryReceiver);
    }
}
