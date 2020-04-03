package com.example.zhanbozhang.test.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * create by eli.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MaskTextView extends TextView {

    private final String TAG = this.getClass().getSimpleName();

    private Paint mPaint;

    private Paint mOriginPaint;

    private int mBaseColor = Color.BLACK;

    public MaskTextView(Context context) {
        this(context, null);
    }

    public MaskTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaskTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(getTextSize());
        mPaint.setTypeface(getTypeface());

        mOriginPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        } else {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap result = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(result);
        // draw background (totally dark)
        c.drawColor(mBaseColor);
        // draw text
        c.drawText(getText().toString(), 0, getTextSize(), mPaint);
        Log.i(TAG, "draw text: " + getText());

        //drawing to view
        float textWidth = mPaint.measureText(getText().toString());
        int leftOffset = (int) ((getMeasuredWidth() - textWidth) / 2);
        if (leftOffset > 0) {
            Bitmap fillBitmap = Bitmap.createBitmap(leftOffset, getHeight(), Bitmap.Config.ARGB_8888);
            Canvas fillCanvas = new Canvas(fillBitmap);
            fillCanvas.drawColor(mBaseColor);
            canvas.drawBitmap(fillBitmap, 0, 0, mPaint);
        }
        canvas.drawBitmap(result, leftOffset, 0, mPaint);
    }
}
