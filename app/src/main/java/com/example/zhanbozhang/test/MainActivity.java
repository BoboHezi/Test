package com.example.zhanbozhang.test;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.zhanbozhang.test.acceleration.AccelerationSensorListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "elifli";

    private SensorManager sensorManager;
    private Sensor sensorStepCounter;
    private Sensor sensorStepDetector;
    private TriggerEventListener detectorTriggerListener = new TriggerEventListener() {
        @Override
        public void onTrigger(TriggerEvent event) {
            sensorManager.requestTriggerSensor(this, sensorStepDetector);
            if (event.values[0] > 0) {
                Log.i(TAG, "Detected step");
                stepCountSinceAppLauncher++;
            }
        }
    };

    private SensorEventListener stepEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.i(TAG, "Sensor: " + event.sensor.getName() + "\tValue: " + event.values[0]);
            int type = event.sensor.getType();
            if (type == Sensor.TYPE_STEP_DETECTOR) {
                stepCountSinceAppLauncher++;
            } else if (type == Sensor.TYPE_STEP_COUNTER) {
                stepCountFormSystem = (long) event.values[0];
            }

            StringBuilder sb =
                    new StringBuilder("Step Number Since App Launched: ")
                            .append(stepCountSinceAppLauncher)
                            .append("\nStep Number Since System booted: ")
                            .append(stepCountFormSystem);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private long stepCountSinceAppLauncher = 0;
    private long stepCountFormSystem = 0;

    AccelerationSensorListener sensorListener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorListener = AccelerationSensorListener.getInstances(this);
        sensorListener.addMotionListener(listener);
        sensorListener.startMotionListener();

        findViewById(R.id.btn_bt).setOnClickListener((view) -> {
            EditText ed = findViewById(R.id.ed_bt);
            String value = ed.getText().toString();
            if (!TextUtils.isEmpty(value)) {
                Settings.System.putString(getContentResolver(), "bluetooth_device_name", value);
            }
        });

        findViewById(R.id.btn_wifi).setOnClickListener((view) -> {
            EditText ed = findViewById(R.id.ed_wifi);
            String value = ed.getText().toString();
            if (!TextUtils.isEmpty(value)) {
                Settings.System.putString(getContentResolver(), "wifi_device_name", value);
            }
        });

        new Thread(this::queryRecordFiles).start();

        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo("com.tencent.tmgp.pubgmhdce", 0);
            if (ai.category == ApplicationInfo.CATEGORY_GAME) {
                Log.i(TAG, "this is a game");
            } else {
                Log.i(TAG, "this is not a game");
            }
        } catch (Exception e) {
            Log.i(TAG, "exception: " + e.toString());
        }

        /*Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);*/
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void queryRecordFiles() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MediaStore.Audio.Media.DATA);
        stringBuilder.append(" LIKE '%");
        stringBuilder.append("/");
        stringBuilder.append("Recording");
        stringBuilder.append("/");
        stringBuilder.append("%'");
        stringBuilder.append(" OR ");
        stringBuilder.append(MediaStore.Audio.Media.DATA);
        stringBuilder.append(" LIKE '%");
        stringBuilder.append("/");
        stringBuilder.append("PhoneRecord");
        stringBuilder.append("/");
        stringBuilder.append("%'");

        String selection = stringBuilder.toString();
        queryFromMediaDB(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, selection, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
    }

    private synchronized void queryFromMediaDB(Uri uri, String selection, String sort) {
        Cursor cursor = null;
        try {
            if (selection == null) {
                cursor = getContentResolver().query(uri, null, null, null, sort);
            } else {
                cursor = getContentResolver().query(uri, null, selection, null, sort);
            }
            if (cursor != null) {
                Log.i(TAG, "queryFromMediaDB: " + cursor.getCount());
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
                    String size = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE));
                    long sizeL = 0L;
                    if (!TextUtils.isEmpty(size)) {
                        sizeL = Long.parseLong(size);
                    }

                    Log.i(TAG, "data: " + data + "\tsize: " + size + "\tsizeL: " + sizeL);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String btName = Settings.System.getString(getContentResolver(), "bluetooth_device_name");
        String wifiName = Settings.System.getString(getContentResolver(), "wifi_device_name");

        ((EditText) findViewById(R.id.ed_bt)).setText(btName);
        ((EditText) findViewById(R.id.ed_wifi)).setText(wifiName);

        Drawable drawable = getResources().getDrawable(R.drawable.incall_answer_bottom);
    }

    class TestBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final PendingResult result = goAsync();
            AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    result.finish();
                    return null;
                }
            };
            asyncTask.execute();
        }
    }

    AccelerationSensorListener.OnPhoneMotionListener listener = new AccelerationSensorListener.OnPhoneMotionListener() {
        @Override
        public void onAccelerationChanged(int type, float x, float y, float z) {

        }

        @Override
        public void onPhoneUplifted() {

        }

        @Override
        public void onPhoneLieDown() {

        }

        @Override
        public void onPhoneUnstable() {
            Log.i(TAG, "The phone is unstable now");
            runOnUiThread(() -> {

            });
        }

        @Override
        public void onPhoneStable() {
            Log.i(TAG, "The phone is stable now");
            runOnUiThread(() -> {

            });
        }

        @Override
        public void onOtherInfo(String text) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorListener.stopMotionListener();
    }

    Runnable AsyncReadCallLogs = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            int slotCount = telephonyManager.getPhoneCount();
            int simCount = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoCount();
            String line1Number = telephonyManager.getLine1Number();
            Log.i(TAG, "slot count: " + slotCount + "\tSIM count: " + simCount + "\tline1Number: " + line1Number);

            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd:hh:mm");

            String[] projection = new String[]{
                    CallLog.Calls.CACHED_NAME,
                    CallLog.Calls.NUMBER,
                    CallLog.Calls.TYPE,
                    CallLog.Calls.DATE,
                    CallLog.Calls.DURATION,
                    CallLog.Calls.PHONE_ACCOUNT_COMPONENT_NAME,
                    CallLog.Calls.PHONE_ACCOUNT_ID,
                    "phone_account_address"/*CallLog.Calls.PHONE_ACCOUNT_ADDRESS*/
            };
            String where = "phone_account_address = ?";
            String[] selectionArgs = new String[]{
                    "+8613095451128"
            };

            Cursor cursor = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    projection, where, selectionArgs, CallLog.Calls.DEFAULT_SORT_ORDER);

            Log.i(TAG, "cursor uri: " + CallLog.Calls.CONTENT_URI.toString());
            if (cursor != null && cursor.getCount() > 0) {
                for (cursor.moveToFirst(); cursor.isLast(); cursor.moveToNext()) {
                    String name = cursor.getString(0);
                    String number = cursor.getString(1);
                    String type;

                    switch (Integer.parseInt(cursor.getString(2))) {
                        case CallLog.Calls.INCOMING_TYPE:
                            type = "In Coming";
                            break;

                        case CallLog.Calls.OUTGOING_TYPE:
                            type = "Outgoing";
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            type = "Missed Call";
                            break;

                        default:
                            type = "Unknown";
                            break;
                    }

                    String callTime = sdf.format(new Date(Long.parseLong(cursor.getString(3))));
                    int callDuration = Integer.parseInt(cursor.getString(4));
                    String componentName = cursor.getString(5);
                    String accountID = cursor.getString(6);
                    String viaNumber = cursor.getString(7);

                    Log.i(TAG, "name: " + name + "\tviaNumber: " + viaNumber);

                    /*if (cursor.isLast()) {
                        break;
                    }*/
                }
            }
        }
    };

    public void startActivity(View view) {
        try {
            /*Intent intent = new Intent(Settings.ACTION_SHOW_REGULATORY_INFO);
            startActivity(intent);*/
            String action = "com.android.screen.recording";
            Intent intent = new Intent(action);
            intent.putExtra("wherefrom", "ScreenRecordActivity");
            sendBroadcast(intent);
        } catch (Exception e) {
            Log.i(TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        String netWork1 = getString(R.string.test_names, 1, "CMCC");
        String netWork2 = getString(R.string.test_names, 2, "CU");
        menu.findItem(R.id.menu1).setTitle(netWork1);
        menu.findItem(R.id.menu2).setTitle(netWork2);
        return super.onPrepareOptionsMenu(menu);
    }
}
