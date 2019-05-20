package com.example.zhanbozhang.test.answer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.zhanbozhang.test.R;

import org.xmlpull.v1.XmlPullParser;

import java.io.FileInputStream;
import java.io.StringReader;
import java.util.HashMap;

public class FlingLeftRightMethod extends AnswerMethod implements FlingLeftRightTouchHandler.OnProgressChangedListener {

    private static final String TAG = "FlingLeftRightMethod";

    private FlingLeftRightTouchHandler touchHandler;

    private Animator vibrationAnimator;

    private View contactPuckContainer;
    private ImageView contactPuckIcon;
    private View iconAnswer;
    private View iconReject;
    private HintProgress hintProgress;

    // a value for finger swipe progress. -1 or less for "reject"; 1 or more for "accept".
    private float swipeProgress;
    private int mPositiveWidth;
    private int mPuckWidth;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_left_right_method, container, false);
        contactPuckContainer = view.findViewById(R.id.incoming_call_puck_container);
        contactPuckIcon = view.findViewById(R.id.incoming_call_puck_icon);
        iconAnswer = view.findViewById(R.id.incoming_call_answer);
        iconReject = view.findViewById(R.id.incoming_call_reject);
        hintProgress = view.findViewById(R.id.incoming_call_progress);
        hintProgress.startAnimation();
        touchHandler = FlingLeftRightTouchHandler.attach(view.findViewById(R.id.incoming_call_touch_area), this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        View container = getView().findViewById(R.id.incoming_swipe_to_answer_container);
        final Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        container.startAnimation(animation);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startVibrationAnimation();

        new Handler().postDelayed(() -> {
        }, 300);
    }

    @Override
    public void setHintText(@Nullable CharSequence hintText) {
    }

    @Override
    public void setShowIncomingWillDisconnect(boolean incomingWillDisconnect) {

    }

    @Override
    public void onProgressChanged(float progress) {
        swipeProgress = progress;
        if (mPositiveWidth <= 0 || mPuckWidth <= 0) {
            mPositiveWidth = hintProgress.getWidth();
            mPuckWidth = contactPuckContainer.getWidth();
        }
        int positionX = (int) (mPositiveWidth * progress / 2);
        int min = (mPuckWidth - mPositiveWidth) / 2;
        int max = (mPositiveWidth - mPuckWidth) / 2;
        positionX = Math.max(min, Math.min(positionX, max));
        contactPuckContainer.setTranslationX(positionX);
        contactPuckIcon.setRotation(progress * 135);

        if (progress > FlingLeftRightTouchHandler.POSITIVE_FRACTION) {
            iconAnswer.setBackground(getResources().getDrawable(R.drawable.action_answer_bg));
        } else if (progress < -FlingLeftRightTouchHandler.POSITIVE_FRACTION) {
            iconReject.setBackground(getResources().getDrawable(R.drawable.action_reject_bg));
        } else {
            iconAnswer.setBackground(null);
            iconReject.setBackground(null);
        }
    }

    @Override
    public void onTrackingStart() {
        hintProgress.stopAnimation();
        vibrationAnimator.pause();
    }

    @Override
    public void onTrackingStopped() {
        hintProgress.startAnimation();
        startVibrationAnimation();
    }

    @Override
    public void onMoveReset(boolean showHint) {

    }

    @Override
    public void onMoveFinish(boolean accept) {
        Log.i(TAG, "onMoveFinish accept: " + accept);

        if (accept) {
            try {
                //startActivity(new Intent(Intent.ACTION_CALL_BUTTON));
            } catch (Exception e) {
                Log.i(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean shouldUseFalsing(@NonNull MotionEvent downEvent) {
        return false;
    }

    private void updateSwipeTextAndPuckForTouch() {

    }

    private void startVibrationAnimation() {
        AnimatorSet breatheAnimation = new AnimatorSet();
        addVibrationAnimator(breatheAnimation);
    }

    private void addVibrationAnimator(AnimatorSet animatorSet) {
        if (vibrationAnimator != null) {
            vibrationAnimator.end();
        }

        // Note that we animate the value between 0 and 1, but internally VibrateInterpolator will
        // translate it into actually X translation value.
        vibrationAnimator =
                ObjectAnimator.ofFloat(
                        contactPuckContainer, View.TRANSLATION_X, 0 /* begin value */, 1 /* end value */);
        vibrationAnimator.setDuration(1_833);
        vibrationAnimator.setInterpolator(new VibrateInterpolator(getContext()));

        animatorSet.play(vibrationAnimator).after(300);
    }

    /**
     * Custom interpolator class for puck vibration.
     */
    private static class VibrateInterpolator implements Interpolator {

        private static final long RAMP_UP_BEGIN_MS = 583;
        private static final long RAMP_UP_DURATION_MS = 167;
        private static final long RAMP_UP_END_MS = RAMP_UP_BEGIN_MS + RAMP_UP_DURATION_MS;
        private static final long RAMP_DOWN_BEGIN_MS = 1_583;
        private static final long RAMP_DOWN_DURATION_MS = 250;
        private static final long RAMP_DOWN_END_MS = RAMP_DOWN_BEGIN_MS + RAMP_DOWN_DURATION_MS;
        private static final long RAMP_TOTAL_TIME_MS = RAMP_DOWN_END_MS;
        private final float ampMax;
        private final float freqMax = 80;
        private Interpolator sliderInterpolator = new FastOutSlowInInterpolator();

        VibrateInterpolator(Context context) {
            ampMax = 2;
        }

        @Override
        public float getInterpolation(float t) {
            float slider = 0;
            float time = t * RAMP_TOTAL_TIME_MS;

            // Calculate the slider value based on RAMP_UP and RAMP_DOWN times. Between RAMP_UP and
            // RAMP_DOWN, the slider remains the maximum value of 1.
            if (time > RAMP_UP_BEGIN_MS && time < RAMP_UP_END_MS) {
                // Ramp up.
                slider =
                        sliderInterpolator.getInterpolation(
                                (time - RAMP_UP_BEGIN_MS) / (float) RAMP_UP_DURATION_MS);
            } else if ((time >= RAMP_UP_END_MS) && time <= RAMP_DOWN_BEGIN_MS) {
                // Vibrate at maximum
                slider = 1;
            } else if (time > RAMP_DOWN_BEGIN_MS && time < RAMP_DOWN_END_MS) {
                // Ramp down.
                slider =
                        1
                                - sliderInterpolator.getInterpolation(
                                (time - RAMP_DOWN_BEGIN_MS) / (float) RAMP_DOWN_DURATION_MS);
            }

            float ampNormalized = ampMax * slider;
            float freqNormalized = freqMax * slider;

            return (float) (ampNormalized * Math.sin(time * freqNormalized));
        }
    }
}
