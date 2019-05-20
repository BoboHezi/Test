package com.example.zhanbozhang.test.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.zhanbozhang.test.TestAODActivity;

public class SecretCodeReceiver extends BroadcastReceiver {

    public static final String SECRET_CODE_ACTION = "android.provider.Telephony.SECRET_CODE";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case SECRET_CODE_ACTION:
                Uri uri = intent.getData();
                if (uri != null) {
                    String host = uri.getSchemeSpecificPart().substring(2);
                    Log.i("elifli", "host: " + host);
                    context.startActivity(new Intent(context, TestAODActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }
                break;
        }
    }
}
