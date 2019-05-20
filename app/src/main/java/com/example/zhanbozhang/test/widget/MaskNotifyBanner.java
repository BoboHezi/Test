package com.example.zhanbozhang.test.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.example.zhanbozhang.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * create by eli.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MaskNotifyBanner extends View {

    private final String TAG = this.getClass().getSimpleName();

    private Paint mPaint;

    private List<Integer> imageArray = new ArrayList<>();

    private List<Bitmap> images = new ArrayList<>();

    public MaskNotifyBanner(Context context) {
        this(context, null);
    }

    public MaskNotifyBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskNotifyBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MaskNotifyBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        for (int id : imageArray) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            if (bitmap != null) {
                images.add(bitmap);
            }
        }
    }

    public void setImageArray(List<Integer> array) {
        imageArray = array;
        images.clear();
        for (int id : imageArray) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            if (bitmap != null) {
                images.add(bitmap);
            }
        }
        requestLayout();
        postInvalidate();
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
            widthSpecSize = 0;
            for (int i = 0; i < images.size(); i++) {
                Bitmap bitmap = images.get(i);
                widthSpecSize += (i < images.size() - 1) ? bitmap.getWidth() * 1.3 : bitmap.getWidth();
            }
        }
        if (heightSpecMode == MeasureSpec.AT_MOST) {
            Bitmap bitmap = (images.size() > 0) ? images.get(0) : null;
            heightSpecSize = bitmap != null ? bitmap.getHeight() : 0;
        }
        setMeasuredDimension(widthSpecSize, heightSpecSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);

        // draw background (totally dark)
        mPaint.setStyle(Paint.Style.FILL);
        c.drawColor(Color.argb(255, 0, 0, 0));

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        int left = 0;
        for (int i = 0; i < images.size(); i++) {
            Bitmap bm = images.get(i);
            c.drawBitmap(bm, left, 0, mPaint);
            left += (i < images.size() - 1) ? bm.getWidth() * 1.3 : bm.getWidth();
        }

        // drawing to view
        int leftOffset = (getMeasuredWidth() - left) / 2;
        if (leftOffset > 0) {
            Bitmap fillBitmap = Bitmap.createBitmap(leftOffset, getHeight(), Bitmap.Config.ARGB_8888);
            Canvas fillCanvas = new Canvas(fillBitmap);
            fillCanvas.drawColor(Color.argb(255, 0, 0, 0));
            canvas.drawBitmap(fillBitmap, 0, 0, mPaint);
        }
        canvas.drawBitmap(bitmap, leftOffset, 0, mPaint);
    }
}
