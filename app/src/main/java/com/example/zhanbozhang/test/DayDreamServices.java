package com.example.zhanbozhang.test;

import android.service.dreams.DreamService;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class DayDreamServices extends DreamService {

    private static final String TAG = "eli_day_dream";

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onAttachedToWindow() {
        Log.i(TAG, "onAttachedToWindow");
        super.onAttachedToWindow();
        setInteractive(true);
        setFullscreen(true);
        setContentView(R.layout.day_dream_layout);

        findViewById(R.id.dream_btn).setOnClickListener(v -> {
            Log.i(TAG, "clicked");
        });

        getWindow().getDecorView().setSystemUiVisibility(0
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onDreamingStarted() {
        Log.i(TAG, "onDreamingStarted");
        super.onDreamingStarted();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.DISPLAY_CLIP_VERTICAL;
        getWindow().setAttributes(lp);
    }

    @Override
    public void onDreamingStopped() {
        Log.i(TAG, "onDreamingStopped");
        super.onDreamingStopped();
    }

    @Override
    public void onDetachedFromWindow() {
        Log.i(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }
}
