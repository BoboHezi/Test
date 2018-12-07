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
import android.widget.TextClock;

/**
 * create by eli.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MaskTextClock extends TextClock {

    private final String TAG = this.getClass().getSimpleName();

    private Paint mPaint;

    public MaskTextClock(Context context) {
        this(context, null);
    }

    public MaskTextClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskTextClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaskTextClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(getTextSize());
        mPaint.setTypeface(getTypeface());
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap result = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(result);
        // draw background (totally dark)
        c.drawColor(Color.argb(255, 0, 0, 0));

        float textWidth = mPaint.measureText(getText().toString());
        int left = textWidth > getWidth() ? 0 : (int) ((getWidth() - textWidth) / 2);

        // draw text
        c.drawText(getText().toString(), left, getTextSize(), mPaint);
        Log.i(TAG, "draw text: " + getText());

        // drawing to view
        int leftOffset = (int) ((getMeasuredWidth() - textWidth) / 2);
        if (leftOffset > 0) {
            Bitmap fillBitmap = Bitmap.createBitmap(leftOffset, getHeight(), Bitmap.Config.ARGB_8888);
            Canvas fillCanvas = new Canvas(fillBitmap);
            fillCanvas.drawColor(Color.argb(255, 0, 0, 0));
            canvas.drawBitmap(fillBitmap, 0, 0, mPaint);
        }
        canvas.drawBitmap(result, 0, 0, mPaint);
    }
}
