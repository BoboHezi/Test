package com.example.zhanbozhang.test.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.zhanbozhang.test.R;

/**
 * create by eli.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GradientLayout extends LinearLayout {

    private final String TAG = this.getClass().getSimpleName();

    private Paint mPaint;

    // whether draw gradient colors
    private boolean mShowGradient = true;

    // color res for gradient color array
    private int mColorArrayRes;

    // angle of gradient(implement in the future)
    private int mAngle;

    // color array for gradient
    private int[] mColorArray;

    public GradientLayout(Context context) {
        this(context, null);
    }

    public GradientLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GradientLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GradientLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GradientLayout, 0, 0);
        mShowGradient = ta.getBoolean(R.styleable.GradientLayout_showGradient, true);
        mColorArrayRes = ta.getResourceId(R.styleable.GradientLayout_colors, R.array.gradient_colors);
        mAngle = ta.getInteger(R.styleable.GradientLayout_angle, 45);
        ta.recycle();

        if (mShowGradient) {
            setBackgroundColor(0x000000);
            mColorArray = getResources().getIntArray(mColorArrayRes);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mShowGradient) {
            // draw gradient color
            int viewWidth = getMeasuredWidth();
            int viewHeight = getMeasuredHeight();

            float[] positions = new float[4];
            calculatePosition(positions, viewWidth, viewHeight, mAngle);

            LinearGradient gradient =
                    new LinearGradient(positions[0], positions[1], positions[2], positions[3],
                            mColorArray, null, Shader.TileMode.CLAMP);
            mPaint.setShader(gradient);

            canvas.drawRect(0, 0, viewWidth, viewHeight, mPaint);
        }
        super.onDraw(canvas);
    }

    private void calculatePosition(float[] positions, int width, int height, int angle) {
        int radius = Math.max(width, height) / 2;
        int circleX = width / 2;
        int circleY = height / 2;
        float radian = (float) (angle * Math.PI / 180);

        float p_H = (float) (radius * Math.sin(radian));
        float p_W = (float) (radius * Math.cos(radian));
        positions[0] = circleX + p_W;
        positions[1] = circleY - p_H;
        positions[2] = circleX - p_W;
        positions[3] = circleY + p_H;
    }
}
