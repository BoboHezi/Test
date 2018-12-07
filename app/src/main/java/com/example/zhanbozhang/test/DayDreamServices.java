package com.example.zhanbozhang.test;

import android.service.dreams.DreamService;
import android.util.Log;
import android.view.Gravity;
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
