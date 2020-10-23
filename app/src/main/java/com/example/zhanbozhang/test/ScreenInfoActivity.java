package com.example.zhanbozhang.test;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.zhanbozhang.test.utils.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RequiresApi(api = 28)
public class ScreenInfoActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "elifli";

    public static final int WIFICIPHER_NOPASS = 1;
    public static final int WIFICIPHER_WEP = 2;
    public static final int WIFICIPHER_WPA = 3;
    public static final int WIFICIPHER_PSK = 4;
    public static final int WIFICIPHER_EAP = 5;

    private TextView infoText;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Fragment fragment;

    private Sensor proximitySensor;
    private PowerManager.WakeLock proximityWakeLock;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        //getWindow().setNavigationBarColor(0x880E637C);

        setContentView(R.layout.activity_screen_info);
        infoText = findViewById(R.id.screen_info);

        /*WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        // navigation area
        View view = findViewById(R.id.navigation_area);
        view.getLayoutParams().height = getNavigationBarHeight(this);

        int realHeight = getRealScreenSize(display).y;
        int usableHeight = getAppUsableScreenSize(display).y;

        Log.i(TAG, "realHeight: " + realHeight + "\tusableHeight: " + usableHeight + "\tdiff: " + (realHeight - usableHeight));

        // full area
        View fullArea = findViewById(R.id.full_area);
        fullArea.getLayoutParams().height = realHeight;

        // app area
        View usableArea = findViewById(R.id.usable_area);
        usableArea.getLayoutParams().height = usableHeight;*/

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.frag_in, R.anim.frag_out);
        fragment = new ThatFragment();

        infoText.setOnClickListener(v -> {
            Fragment fragment = fragmentManager.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = this.fragment;
                transaction.add(R.id.incall_dialpad_container, fragment, TAG);
                transaction.commit();
            } else {
                /*if (fragment.isHidden()) {
                    transaction.show(fragment);
                    transaction.commit();
                } else {
                    transaction.hide(fragment);
                    transaction.commit();
                }*/
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        registerReceiver(localeReceiver, filter);

        new Handler().postDelayed(() -> {
            getCutoutInfo();
        }, 3000);

        proximitySensor = getSystemService(SensorManager.class).getDefaultSensor(Sensor.TYPE_PROXIMITY);
        getSystemService(SensorManager.class).registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        proximityWakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
    }

    private void getCutoutInfo() {
        final DisplayInfo mInfo = new DisplayInfo();
        getDisplay().getDisplayInfo(mInfo);
        DisplayCutout cutout = mInfo.displayCutout;

        if (cutout != null) {
            Log.d(TAG, "BoundingRects:\t" + cutout.getBoundingRects());
            Log.d(TAG, "SafeInsetBottom:\t" + cutout.getSafeInsetBottom());
            Log.d(TAG, "SafeInsetLeft:\t" + cutout.getSafeInsetLeft());
            Log.d(TAG, "SafeInsetRight:\t" + cutout.getSafeInsetRight());
            Log.d(TAG, "SafeInsetTop:\t" + cutout.getSafeInsetTop());
        }
    }

    BroadcastReceiver localeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Locale changed");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localeReceiver);
    }

    private void listWifiConfig() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configurations = wifiManager.getConfiguredNetworks();

        for (WifiConfiguration config : configurations) {
            StringBuilder sb = new StringBuilder("SSID: ");
            sb.append(config.SSID);
            sb.append("\tBSSID: ");
            sb.append(config.BSSID);
            sb.append("\tSecure: ");
            sb.append(getType(config));
            sb.append("\tLocked: ");
            sb.append(isLocked(config));
            Log.i(TAG, sb.toString());
        }
    }

    private int getType(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return WIFICIPHER_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return WIFICIPHER_EAP;
        }
        return (config.wepKeys[0] != null) ? WIFICIPHER_WEP : WIFICIPHER_NOPASS;
    }

    private boolean isLocked(WifiConfiguration config) {
        return getType(config) != WIFICIPHER_NOPASS;
    }

    private void handlerLocales() {

        Locale locale = Locale.forLanguageTag("fr-MA");
        String mId = locale.toLanguageTag();

        Set<String> ls = new HashSet<>();
        for (String l : getResources().getStringArray(R.array.supported_locales)) {
            ls.add(l);
        }

        if (ls.contains(mId)) {
            Log.i(TAG, "contains " + mId);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG, "onSensorChanged: " + event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class TestLocale {
        String mId = null;
        String mValue = null;

        public TestLocale(String id, String value) {
            mId = id;
            mValue = value;
        }

        @Override
        public String toString() {
            return mId + " : " + mValue;
        }
    }

    private class MyQueryHandler extends AsyncQueryHandler {

        public MyQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "get mag: " + msg.what);
            super.handleMessage(msg);

            FingerViewHandler fingerViewHandler = new FingerViewHandler();
            fingerViewHandler.removeMessages(FingerViewHandler.HANDLER_FINGER_VIEW_GONE);
            fingerViewHandler.sendEmptyMessageDelayed(FingerViewHandler.HANDLER_FINGER_VIEW_VISIBLE, 100);
        }
    }

    private class FingerViewHandler extends Handler {

        final static int HANDLER_FINGER_VIEW_VISIBLE = 0;

        final static int HANDLER_FINGER_VIEW_GONE = 0;

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_FINGER_VIEW_VISIBLE) {

            } else if (msg.what == HANDLER_FINGER_VIEW_GONE) {

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        float density = metric.density;
        int densityDpi = metric.densityDpi;

        StringBuilder sb = new StringBuilder();
        sb.append("Screen info:");
        sb.append("\nwidth: " + width);
        sb.append("\nheight: " + height);
        sb.append("\ndensity: " + density);
        sb.append("\ndensityDpi: " + densityDpi);
        sb.append("\ndp-width: " + (width / density));
        sb.append("\ndp-height: " + (height / density));
        sb.append("\npoint: " + Utils.getRealScreenSize());

        if (infoText != null) {
            infoText.setText(sb);
        }

        //Log.i(TAG, sb.toString());
    }

    public int getNavigationBarHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point appUsableSize = getAppUsableScreenSize(display);
        Point realScreenSize = getRealScreenSize(display);

        // Navigation bar on the right.
        if (appUsableSize.x < realScreenSize.x) {
            Log.i(TAG, "appUsableSize.y: " + appUsableSize.y);
            return appUsableSize.y;
        }

        // Navigation bar at the bottom.
        if (appUsableSize.y < realScreenSize.y) {
            Log.i(TAG, "realScreenSize.y - appUsableSize.y: " + (realScreenSize.y - appUsableSize.y));
            return realScreenSize.y - appUsableSize.y;
        }

        // Navigation bar is not present.
        return 0;
    }

    private Point getAppUsableScreenSize(Display display) {
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    private Point getRealScreenSize(Display display) {
        Point size = new Point();
        display.getRealSize(size);
        return size;
    }
}
