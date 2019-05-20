package com.example.zhanbozhang.test;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;

@TargetApi(Build.VERSION_CODES.O)
@RequiresApi(api = Build.VERSION_CODES.N)
public class MessageActivity extends Activity {

    private static final String TAG = "MessageActivity";

    public static final String TEST_HEADSUP_NOTIFY = "action.eli.testHeadsUP";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_message);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.y = 8;
        getWindow().setAttributes(lp);

        ImageView imageView = findViewById(R.id.drag_image);
        imageView.setTag("drag_image");

        imageView.setOnLongClickListener(v -> {
            ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(findViewById(R.id.shadow_view));
            v.startDragAndDrop(dragData, shadow, null, 0);
            return true;
        });

        imageView.setOnDragListener((v, event) -> {
            Log.i(TAG, "drag: " + event.getX() + ", " + event.getY());
            return false;
        });

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        WiredHeadsetCallback headsetCallback = new WiredHeadsetCallback();
        mAudioManager.registerAudioDeviceCallback(headsetCallback, null);

        IntentFilter filter = new IntentFilter();
        filter.addAction(TEST_HEADSUP_NOTIFY);
        registerReceiver(headsupReceiver, filter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDestroy() {
        //mAudioManager.unregisterAudioDeviceCallback(mHeadsetCallback);
        super.onDestroy();

    }

    public void createHeadsupNotify(View view) {

        String channelID = "1";
        String channelName = "MessageActivity";
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentTitle("title")
                .setContentText("content")
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.qq))
                .setSmallIcon(R.drawable.wechat)
                .setWhen(System.currentTimeMillis())
                .setChannelId(channelID)
                .setAutoCancel(true)
                .setStyle(new Notification.DecoratedCustomViewStyle())
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        new Intent(""), PendingIntent.FLAG_ONE_SHOT));

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification_content);
        contentView.setOnClickPendingIntent(R.id.action_positive,
                PendingIntent.getActivity(this, 0,
                        new Intent(this, TestAODActivity.class), PendingIntent.FLAG_CANCEL_CURRENT));
        builder.setCustomContentView(contentView);

        manager.notify(11, builder.build());
    }

    BroadcastReceiver headsupReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            createHeadsupNotify(null);
        }
    };

    private AudioManager mAudioManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private class WiredHeadsetCallback extends AudioDeviceCallback {
        @Override
        public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
            super.onAudioDevicesAdded(addedDevices);
            updateHeadsetStatus();
        }

        @Override
        public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
            super.onAudioDevicesRemoved(removedDevices);
            updateHeadsetStatus();
        }

        private void updateHeadsetStatus() {
            final boolean isPluggedIn = isWiredHeadsetPluggedIn();
            Log.i(TAG, "ACTION_HEADSET_PLUG event, plugged in: " + isPluggedIn);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isWiredHeadsetPluggedIn() {
        AudioDeviceInfo[] devices = mAudioManager.getDevices(AudioManager.GET_DEVICES_ALL);
        boolean isPluggedIn = false;

        for (AudioDeviceInfo device : devices) {
            switch (device.getType()) {
                case AudioDeviceInfo.TYPE_WIRED_HEADPHONES:
                case AudioDeviceInfo.TYPE_WIRED_HEADSET:
                case AudioDeviceInfo.TYPE_USB_HEADSET:
                case AudioDeviceInfo.TYPE_USB_DEVICE:
                    isPluggedIn = true;
            }
            if (isPluggedIn) {
                break;
            }
        }
        return isPluggedIn;
    }
}
