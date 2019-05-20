package com.example.zhanbozhang.test.answer;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.BlockedNumberContract;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.zhanbozhang.test.MessageActivity;
import com.example.zhanbozhang.test.R;
import com.example.zhanbozhang.test.receiver.MediaButtonReceiver;

import java.security.Provider;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AnswerActivity extends AppCompatActivity implements AnswerMethodHolder {

    private static final String TAG = "AnswerActivity";

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mProximityWakeLock;
    private AudioManager audioManager;
    private ComponentName component;
    private MediaButtonReceiver mediaButtonReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_activity);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment answerFragment = new FlingLeftRightMethod();
        transaction.replace(R.id.answer_method_container, answerFragment);
        transaction.commit();

        //new Handler().postDelayed(this::sendLockScreenNotify, 5000);

        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (mPowerManager.isWakeLockLevelSupported(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK)) {
            mProximityWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
        }

        startLockoutTimer();
        listProviders();

        new Handler().postDelayed(() -> {
            stopLockoutTimer();
        }, 1000 * 6);
    }

    private void listProviders() {
        PackageManager pm = getPackageManager();

        for (PackageInfo packageInfo : pm.getInstalledPackages(0)) {
            if (packageInfo == null || packageInfo.providers == null)
                continue;

            final ProviderInfo[] pis = packageInfo.providers;

            for (ProviderInfo pi : pis) {
                if (pi.authority != null && pi.authority.contains("com.android.contacts")) {
                    Log.i("elifli", pi.toString());
                }
            }

            /*ProviderInfo providerInfo = pm.resolveContentProvider(packageInfo.packageName, 0);
            if (providerInfo != null && providerInfo.authority != null && providerInfo.authority.contains("com.android.contacts")) {
                Log.i("elifli", providerInfo.name);
            }*/
        }
    }

    private Timer mTimer;

    private void startLockoutTimer() {
        mTimer = new Timer();
        final long startTime = new Date().getTime();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                long offset = System.currentTimeMillis() - startTime;
                Log.i(TAG, "offset: " + offset / 1000);
            }
        }, new Date(startTime + 1000), 1000);
    }

    private void stopLockoutTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void turnOnProximitySensor() {
        if (mProximityWakeLock != null) {
            if (!mProximityWakeLock.isHeld()) {
                mProximityWakeLock.acquire();
            }
        }
    }

    private void turnOffProximitySensor(boolean screenOnImmediately) {
        if (mProximityWakeLock != null) {
            if (mProximityWakeLock.isHeld()) {
                int flags = (screenOnImmediately ? 0 : PowerManager.RELEASE_FLAG_WAIT_FOR_NO_PROXIMITY);
                mProximityWakeLock.release(flags);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //turnOffProximitySensor(true);
    }

    private void sendNotify() {
        Log.i("elifli", "sendNotifyDelayed: send notify");
        Notification.Builder builder = new Notification.Builder(this);
        builder.setOngoing(true);
        builder.setOnlyAlertOnce(true);
        builder.setPriority(Notification.PRIORITY_HIGH);

        builder.setContentText("This is a test msg");
        builder.setContentTitle("Hello");
        builder.setSmallIcon(R.drawable.wechat);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.wechat));
        builder.setChannelId("channel_test_1");

        Notification notification = builder.build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(2934, notification);
    }

    private void sendLockScreenNotify() {
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (/*km.isKeyguardLocked()*/true) {
            Intent alarmIntent = new Intent(this, MessageActivity.class);
            alarmIntent.putExtra("show_incall", false);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(alarmIntent);
        }
    }

    @Override
    public void onAnswerProgressUpdate(float answerProgress) {

    }

    @Override
    public void answerFromMethod() {

    }

    @Override
    public void rejectFromMethod() {

    }

    @Override
    public void resetAnswerProgress() {

    }

    @Override
    public boolean isVideoCall() {
        return false;
    }

    @Override
    public boolean isVideoUpgradeRequest() {
        return false;
    }
}
