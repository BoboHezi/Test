package com.example.zhanbozhang.test.answer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Touch handler that keeps track of flings for {@link FlingLeftRightMethod}.
 */
@SuppressLint("ClickableViewAccessibility")
public class FlingLeftRightTouchHandler implements View.OnTouchListener {

    /**
     * Callback interface for significant events with this touch handler
     */
    interface OnProgressChangedListener {

        /**
         * Called when the visible answer progress has changed. Implementations should use this for
         * animation, but should not perform accepts or rejects until {@link #onMoveFinish(boolean)} is
         * called.
         *
         * @param progress float representation of the progress with +1f fully accepted, -1f fully
         *                 rejected, and 0 neutral.
         */
        void onProgressChanged(@FloatRange(from = -1f, to = 1f) float progress);

        /**
         * Called when a touch event has started being tracked.
         */
        void onTrackingStart();

        /**
         * Called when touch events stop being tracked.
         */
        void onTrackingStopped();

        /**
         * Called when the progress has fully animated back to neutral. Normal resting animation should
         * resume, possibly with a hint animation first.
         *
         * @param showHint {@code true} iff the hint animation should be run before resuming normal
         *                 animation.
         */
        void onMoveReset(boolean showHint);

        /**
         * Called when the progress has animated fully to accept or reject.
         *
         * @param accept {@code true} if the call has been accepted, {@code false} if it has been
         *               rejected.
         */
        void onMoveFinish(boolean accept);

        /**
         * Determine whether this gesture should use the {@link} to reject accidental
         * touches
         *
         * @param downEvent the MotionEvent corresponding to the start of the gesture
         * @return {@code true} if the {@link FalsingManager} should be used to reject accidental
         * touches for this gesture
         */
        boolean shouldUseFalsing(@NonNull MotionEvent downEvent);
    }

    private static final String TAG = "FlingTouchHandler";

    public static final float POSITIVE_FRACTION = .65f;

    @NonNull
    private final View target;
    @NonNull
    private OnProgressChangedListener listener;
    private int trackingPointer;
    private int absCenterX;
    private float currentProgress;

    private boolean tracking;
    private boolean motionAborted;
    private boolean touchEnabled = true;

    private ValueAnimator startAnimator;
    private ValueAnimator endAnimator;
    private ValueAnimator moveAnimator;

    private VelocityTracker velocityTracker;

    /**
     * Create a new FlingUpDownTouchHandler and attach it to the target. Will call {@link
     * View#setOnTouchListener(View.OnTouchListener)} before returning.
     *
     * @param target         View whose touches are to be listened to
     * @param listener       Callback to listen to major events
     * @param falsingManager FalsingManager to identify false touches
     * @return the instance of FlingUpDownTouchHandler that has been added as a touch listener
     */
    public static FlingLeftRightTouchHandler attach(@NonNull View target, @NonNull OnProgressChangedListener listener) {
        FlingLeftRightTouchHandler handler = new FlingLeftRightTouchHandler(target, listener);
        target.setOnTouchListener(handler);
        return handler;
    }

    private FlingLeftRightTouchHandler(@NonNull View target, @NonNull OnProgressChangedListener listener) {
        this.target = target;
        this.listener = listener;
    }

    /**
     * Returns {@code true} iff a touch is being tracked
     */
    public boolean isTracking() {
        return tracking;
    }

    private void initVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        velocityTracker = VelocityTracker.obtain();
    }

    private void trackMovement(MotionEvent event) {
        if (velocityTracker != null) {
            velocityTracker.addMovement(event);
        }
    }

    private void handleFling() {
        float vel = 0f;
        if (velocityTracker != null) {
            velocityTracker.computeCurrentVelocity(1500);
            vel = velocityTracker.getXVelocity();
        }

        if (currentProgress * vel > 0) {
            final float G = 9.8f;
            int time = (int) Math.abs(vel / G);
            float target = currentProgress + (time / 1000f) * (currentProgress / Math.abs(currentProgress));
            float finalTarget = Math.min(1, Math.max(target, -1));
            double offset = finalTarget / target;
            if (finalTarget != target) {
                time = (int) (time * offset);
            }
            target = finalTarget;

            Log.i("elifli", "velocity: " + vel +
                    "\ttime: " + time +
                    "\tcurrent: " + currentProgress +
                    "\ttarget: " + target);

            fling(time, target);
        } else {
            endAnimator = createProgressAnimator(currentProgress, 0, 300);
            endAnimator.setInterpolator(new AccelerateInterpolator());
            endAnimator.start();
        }
    }

    private void fling(int time, float target) {
        moveAnimator = createProgressAnimator(currentProgress, target, time);
        moveAnimator.setInterpolator(new DecelerateInterpolator());
        moveAnimator.addListener(
                new AnimatorListenerAdapter() {
                    boolean canceled;

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        canceled = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!canceled) {
                            endAnimator = createProgressAnimator(currentProgress, 0, 300);
                            endAnimator.setInterpolator(new AccelerateInterpolator());
                            endAnimator.start();
                        }
                    }
                });
        moveAnimator.start();
    }

    /**
     * Sets whether touch events will continue to be listened to
     *
     * @param touchEnabled whether future touch events will be listened to
     */
    public void setTouchEnabled(boolean touchEnabled) {
        this.touchEnabled = touchEnabled;
    }

    public void detach() {
        setTouchEnabled(false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (/*event.getPointerCount() > 1*/false) {
            if (endAnimator != null && endAnimator.isRunning()) return false;
            if (startAnimator != null && startAnimator.isRunning()) startAnimator.cancel();
            if (moveAnimator != null && moveAnimator.isRunning()) moveAnimator.cancel();
            endAnimator = createProgressAnimator(currentProgress, 0, 300);
            endAnimator.setInterpolator(new AccelerateInterpolator());
            endAnimator.start();
            // stop touch-track
            motionAborted = true;
            onTrackingStopped();
            return false;
        }

        if (!touchEnabled) {
            return false;
        }
        if (motionAborted && (event.getActionMasked() != MotionEvent.ACTION_DOWN)) {
            Log.i(TAG, "aborted Action: " + MotionEvent.actionToString(event.getAction()));
            if (currentProgress != 0 && (endAnimator == null || !endAnimator.isRunning())) {
                endAnimator = createProgressAnimator(currentProgress, 0, 300);
                endAnimator.setInterpolator(new AccelerateInterpolator());
                endAnimator.start();
            }
            return false;
        }

        int pointerIndex = event.findPointerIndex(trackingPointer);
        if (pointerIndex < 0) {
            pointerIndex = 0;
            trackingPointer = event.getPointerId(pointerIndex);
        }
        if (absCenterX <= 0) {
            absCenterX = target.getWidth() / 2;
        }

        final float pointerX = event.getX(pointerIndex);
        final float newProgress = pointerXToProgress(pointerX);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN");
                motionAborted = false;
                if (endAnimator != null && endAnimator.isRunning()) endAnimator.end();
                startAnimator = createProgressAnimator(currentProgress, newProgress, 300);
                startAnimator.start();
                // start touch-track
                onTrackingStarted();
                if (velocityTracker == null) {
                    initVelocityTracker();
                }
                trackMovement(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                final int upPointer = event.getPointerId(event.getActionIndex());
                Log.i(TAG, "ACTION_POINTER_UP: " + (trackingPointer == upPointer));
                if (trackingPointer == upPointer) {
                    // gesture is ongoing, find a new pointer to track
                    int newIndex = event.getPointerId(0) != upPointer ? 0 : 1;
                    trackingPointer = event.getPointerId(newIndex);
                    onTrackingStarted();
                    setCurrentProgress(newProgress);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i(TAG, "ACTION_POINTER_DOWN");
                motionAborted = true;
                onTrackingStopped();
                break;
            case MotionEvent.ACTION_MOVE:
                if (endAnimator != null && endAnimator.isRunning()) endAnimator.end();
                if (startAnimator != null && startAnimator.isRunning()) return true;
                if (moveAnimator != null && moveAnimator.isRunning()) return true;
                // update current progress immediately
                if (Math.abs(currentProgress - newProgress) > 0.2) {
                    moveAnimator = createProgressAnimator(currentProgress, newProgress, 100);
                    moveAnimator.start();
                } else {
                    setCurrentProgress(newProgress);
                }
                trackMovement(event);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, event.getAction() == MotionEvent.ACTION_UP ? "ACTION_UP" : "ACTION_CANCEL");
                if (startAnimator != null && startAnimator.isRunning()) startAnimator.cancel();
                if (moveAnimator != null && moveAnimator.isRunning()) moveAnimator.cancel();
                /*endAnimator = createProgressAnimator(currentProgress, 0, 300);
                endAnimator.start();*/
                if (Math.abs(currentProgress) > POSITIVE_FRACTION) {
                    // answer or reject the call
                    listener.onMoveFinish(currentProgress > 0);
                }
                // stop touch-track
                onTrackingStopped();
                trackMovement(event);
                handleFling();
        }
        return false;
    }

    @FloatRange(from = -1f, to = 1f)
    private float pointerXToProgress(float pointerX) {
        boolean pointerAfterZero = pointerX > absCenterX;
        float nearestThreshold = pointerAfterZero ? absCenterX * 2 : 0;

        float absoluteProgress = (pointerX - absCenterX) / (nearestThreshold - absCenterX);
        return Math.max(-1f, Math.min(absoluteProgress * (pointerAfterZero ? 1 : -1), 1f));
    }

    private ValueAnimator createProgressAnimator(float fromProgress, float targetProgress, long maxDuration) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fromProgress, targetProgress);
        valueAnimator.addUpdateListener((ValueAnimator animation) -> {
            setCurrentProgress((Float) animation.getAnimatedValue());
        });
        valueAnimator.setDuration((long) (Math.abs(targetProgress - fromProgress) * maxDuration));
        return valueAnimator;
    }

    private void onTrackingStarted() {
        tracking = true;
        listener.onTrackingStart();
    }

    private void onTrackingStopped() {
        tracking = false;
        listener.onTrackingStopped();
    }

    private void setCurrentProgress(float progress) {
        currentProgress = progress;
        listener.onProgressChanged(currentProgress);
    }
}
