package com.example.zhanbozhang.test;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.provider.CallLog;
import android.support.annotation.InterpolatorRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.example.zhanbozhang.test.widget.MaskNotifyBanner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Eli
 */
public class TestAODActivity extends AppCompatActivity {

    private static final String TAG = "TestAODActivity";

    private MaskNotifyBanner notifyBanner;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aod_layout_7);

        notifyBanner = findViewById(R.id.notify_banner);

        TextView dateText = findViewById(R.id.date_view);
        //updateDate(dateText);

        findViewById(R.id.view_container).setOnClickListener(v -> {
            ScaleAnimation scale =
                    new ScaleAnimation(1, 0, 1, 0,
                            ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
            scale.setDuration(1000);
            scale.setFillAfter(true);
            //scale.setInterpolator(new AnticipateInterpolator());
            scale.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            v.startAnimation(scale);
        });

        new Handler().postDelayed(() -> {
            List<Integer> icons = new ArrayList<>();

            icons.add(R.drawable.notification_call);
            icons.add(R.drawable.notification_qq);
            icons.add(R.drawable.notification_wechat);

            notifyBanner.setImageArray(icons);
        }, 2000);

        /*new Thread(() -> {
            Uri smsUri = Uri.parse("content://sms/inbox");
            Cursor smsCursor = getContentResolver().query(smsUri, null, "type = 1 and read = 0", null, null);
            smsCursor.moveToFirst();
            Log.i(TAG, "sms count: " + smsCursor.getCount());

            String where = CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE + " AND " + CallLog.Calls.IS_READ + "=" + 0;
            @SuppressLint("MissingPermission")
            Cursor callCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, where, null, null);
            callCursor.moveToFirst();
            Log.i(TAG, "call count: " + callCursor.getCount());

        }).start();*/
    }

    private void updateDate(TextView dateView) {
        String dateFormat = "MMMd\'日\' EEE";
        String dateString = new SimpleDateFormat(dateFormat).format(new Date());
        dateView.setText(dateString);
    }

}
