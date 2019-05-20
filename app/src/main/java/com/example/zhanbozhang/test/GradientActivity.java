package com.example.zhanbozhang.test;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class GradientActivity extends AppCompatActivity {

    private static final String TAG = "GradientActivity";

    private TextView usbTextView;
    private BatteryBroadcastReceiver batteryReceiver;
    private ActivityManager mActivityManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gradient_layout);

        usbTextView = findViewById(R.id.usb_state);
        batteryReceiver = new BatteryBroadcastReceiver(this);
        batteryReceiver.register();

        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        findViewById(R.id.clear_recent).setOnClickListener(v -> {
            List<ActivityManager.RecentTaskInfo> recent =
                    mActivityManager.getRecentTasks(Integer.MAX_VALUE, ActivityManager.RECENT_WITH_EXCLUDED);
            if (recent != null && recent.size() > 0) {
                if (getApplication().getPackageName().equals(recent.get(0).topActivity.getPackageName())) {
                    recent.remove(0);
                }
            }
            Log.i(TAG, "size: " + recent.size());
            for (ActivityManager.RecentTaskInfo info : recent) {
                StringBuilder sb = new StringBuilder();
                sb.append("Task: ");
                sb.append(" id: ").append(info.id);
                sb.append(" persistentId: ").append(info.persistentId);
                sb.append(" origActivity: ").append(info.origActivity);
                sb.append(" topActivity: ").append(info.topActivity);
                sb.append(" numActivities: ").append(info.numActivities);
                Log.i(TAG, sb.toString());
            }

            for (ActivityManager.RecentTaskInfo info : recent) {
                try {
                    //ActivityManager.getService().removeTask(info.persistentId);
                } catch (Exception e) {
                    Log.i(TAG, "onCreate: " + e.getMessage());
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();

        List<PackageInfo> installedPackages = new ArrayList<>();
        for (PackageInfo info : getPackageManager().getInstalledPackages(0)) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                installedPackages.add(info);
                Log.i(TAG, "installed app: " + info.packageName);
            }
        }

        Drawable drawable = getDrawable(R.drawable.incall_answer_bottom);
        Log.i("elifli", "size: " + drawable.getIntrinsicWidth() + "x" + drawable.getIntrinsicHeight());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        batteryReceiver.unRegister();
    }

    class BatteryBroadcastReceiver extends BroadcastReceiver {

        private Context mContext;
        private String mBatteryLevel;
        private String mBatteryStatus;

        public BatteryBroadcastReceiver(Context context) {
            mContext = context;
        }

        public void register() {
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            intentFilter.addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);

            final Intent intent = mContext.registerReceiver(this, intentFilter);
            updateBatteryStatus(intent, true);
        }

        public void unRegister() {
            mContext.unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            updateBatteryStatus(intent, false);
        }

        private void updateBatteryStatus(Intent intent, boolean forceUpdate) {
            if (intent != null) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    final String batteryLevel = getBatteryLevel(intent);
                    final String batteryStatus = getBatteryStatus(intent);
                    if (forceUpdate) {
                    } else if (!batteryLevel.equals(mBatteryLevel)) {
                    } else if (!batteryStatus.equals(mBatteryStatus)) {
                    }
                    mBatteryLevel = batteryLevel;
                    mBatteryStatus = batteryStatus;

                    final boolean pluggedIn = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0;
                    final int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);

                    final boolean charged = status == BatteryManager.BATTERY_STATUS_FULL;
                    final boolean charging = charged || status == BatteryManager.BATTERY_STATUS_CHARGING;

                    if (pluggedIn && charging) {
                        mBatteryStatus = "Charging";
                    } else {
                        mBatteryStatus = "Not Charging";
                    }

                    String msg = "Status: " + mBatteryStatus + "\nLevel: " + mBatteryLevel;
                    Log.i("elifli", "Test Status: " + mBatteryStatus);
                    usbTextView.setText(msg);
                } else if (PowerManager.ACTION_POWER_SAVE_MODE_CHANGED.equals(intent.getAction())) {
                }
            }
        }

        public String getBatteryLevel(Intent batteryChangedIntent) {
            int level = batteryChangedIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = batteryChangedIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            float percentage = (float) level / scale;
            return NumberFormat.getPercentInstance().format(percentage);
        }

        public String getBatteryStatus(Intent batteryChangedIntent) {
            int status = batteryChangedIntent.getIntExtra(BatteryManager.EXTRA_STATUS,
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            String statusString;
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                statusString = "Charging";
            } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                statusString = "Not Charging";
            } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                statusString = "Plugged in, can\\'t charge right now";
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                statusString = "Full";
            } else {
                statusString = "Unknown";
            }
            return statusString;
        }
    }
}
