package com.example.zhanbozhang.test.services;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AutoRebootService extends Service {

    private static final String TAG = "AutoRebootService_tag";

    public static final String ACTION_THREE_CLOCK_REBOOT = "eli.Test.ACTION_REBOOT_ON_THREE";
    public static final String ACTION_ELI_TEST_IDLE = "eli.Test.ACTION_TEST_IDLE";
    public static final String ACTION_ELI_TEST_PACKAGE = "eli.Test.ACTION_TEST_PACKAGE";

    @Override
    public void onCreate() {
        super.onCreate();
        createAlarm(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_THREE_CLOCK_REBOOT);
        filter.addAction(ACTION_ELI_TEST_IDLE);
        filter.addAction(ACTION_ELI_TEST_PACKAGE);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        registerReceiver(rebootReceiver, filter);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        unregisterReceiver(rebootReceiver);
        super.onDestroy();
    }

    private void createAlarm(Context context) {
        Log.i(TAG, "createAlarm: ");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ACTION_THREE_CLOCK_REBOOT);
        PendingIntent pi = PendingIntent.getBroadcast(context,
                0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 3);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 3);
        calendar.set(Calendar.MILLISECOND, 0);
        long selectTime = calendar.getTimeInMillis();
        long systemTime = System.currentTimeMillis();
        if (systemTime > selectTime) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");
        String selectStr = sdf.format(new Date(calendar.getTimeInMillis()));
        Log.i(TAG, "selectStr 3 clock : " + selectStr);
        alarmManager.cancel(pi);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
    }

    BroadcastReceiver rebootReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_THREE_CLOCK_REBOOT)) {
                Log.i(TAG, "I`m gonna reboot");
                /*try {
                    Runtime.getRuntime().exec("am start -a android.intent.action.REBOOT");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            } else if (intent.getAction().equals(ACTION_ELI_TEST_IDLE)) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                boolean isPhoneIdle = tm != null && tm.getCallState() == TelephonyManager.CALL_STATE_IDLE;

                KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
                boolean isKerguardEnabled = km != null && km.isKeyguardLocked();

                AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                boolean isMusicActive = am != null && am.isMusicActive();

                Log.i(TAG, "isPhoneIdle: " + isPhoneIdle +
                        " isKerguardEnabled: " + isKerguardEnabled +
                        " isMusicActive: " + isMusicActive);
            } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) {

            } else if (intent.getAction().equals(ACTION_ELI_TEST_PACKAGE)) {
                Intent packageIntent = new Intent(Intent.ACTION_PACKAGE_CHANGED,
                        Uri.fromParts("package", "com.example.zhanbozhang.test", null));
                packageIntent.setFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
                packageIntent.putExtra(/*Intent.EXTRA_USER_HANDLE*/"android.intent.extra.user_handle", 12);
                packageIntent.putExtra(Intent.EXTRA_CHANGED_COMPONENT_NAME_LIST, new String[0]);
                packageIntent.setPackage("com.android.settings");
                context.sendBroadcast(packageIntent);

                Uri uri = null;
                uri.getEncodedSchemeSpecificPart();
                uri.getSchemeSpecificPart();

                Log.i(TAG, "onReceive: send ACTION_PACKAGE_CHANGED broadcast");
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class ServiceBinder extends Binder {
        public AutoRebootService getService() {
            return AutoRebootService.this;
        }
    }
}
