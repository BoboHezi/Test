package com.example.zhanbozhang.test.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class MediaButtonReceiver extends BroadcastReceiver {

    private static final String TAG = "MediaButtonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "onReceive: " + action);

        if (Intent.ACTION_MEDIA_BUTTON.equals(action)) {
            KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

            int keyCode = event.getKeyCode();
            int keyAction = event.getAction();
            long eventTime = event.getEventTime();

            Log.i(TAG, "keyCode: " + KeyEvent.keyCodeToString(keyCode) +
                    "\tkeyAction: " + keyAction + "\teventTime: " + eventTime);

        }
    }
}
