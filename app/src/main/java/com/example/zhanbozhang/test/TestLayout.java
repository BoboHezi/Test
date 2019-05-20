package com.example.zhanbozhang.test;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestLayout extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_layout);
        final TextView textView = findViewById(R.id.battery_info_p1);
        textView.setFitsSystemWindows(true);

        ViewTreeObserver observer = textView.getViewTreeObserver();
        observer.addOnPreDrawListener(() -> {
            int location[] = new int[2];
            textView.getLocationOnScreen(location);
            Paint paint = textView.getPaint();
            if (paint != null) {
                final Paint.FontMetrics metrics = paint.getFontMetrics();
                Log.i("elifli",
                        "top: " + metrics.top +
                                " ascent: " + metrics.ascent +
                                " descent: " + metrics.descent +
                                " bottom: " + metrics.bottom);
            }
            return true;
        });
    }

}
