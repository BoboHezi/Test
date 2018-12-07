package com.example.zhanbozhang.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ArrayRes;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by zhiheng.huang on 2018/4/16
 */
public class FingerprintAnimation {

    private boolean mIsRepeat;

    private AnimationListener mAnimationListener;

    private ImageView mImageView;

    private int[] mFrameRess;

    /**
     * 每帧动画的播放间隔数组
     */
    private int[] mDurations;

    /**
     * 每帧动画的播放间隔
     */
    private int mDuration;

    /**
     * 下一遍动画播放的延迟时间
     */
    private int mDelay;

    private int mLastFrame;

    private boolean mNext;

    private boolean mPause;

    private int mCurrentSelect;

    private int mCurrentFrame;

    private int mRepeatCount;

    private static final int SELECTED_A = 1;

    private static final int SELECTED_B = 2;

    private static final int SELECTED_C = 3;

    private static final int SELECTED_D = 4;

    private FrameThread frameThread;


    /**
     * @param iv       播放动画的控件
     * @param frameRes 播放的图片数组
     * @param duration 每帧动画的播放间隔(毫秒)
     * @param isRepeat 是否循环播放
     */
    public FingerprintAnimation(ImageView iv, int[] frameRes, int duration, boolean isRepeat) {
        this.mImageView = iv;
        this.mFrameRess = frameRes;
        this.mDuration = duration;
        this.mLastFrame = frameRes.length - 1;
        this.mIsRepeat = isRepeat;
//        play(0);
        frameThread = new FrameThread();
    }

    /**
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param isRepeat  是否循环播放
     */
    public FingerprintAnimation(ImageView iv, int[] frameRess, int[] durations, boolean isRepeat) {
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDurations = durations;
        this.mLastFrame = frameRess.length - 1;
        this.mIsRepeat = isRepeat;
//        playByDurations(0);
        frameThread = new FrameThread();
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param duration  每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    public FingerprintAnimation(ImageView iv, int[] frameRess, int duration, int delay) {
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDuration = duration;
        this.mDelay = delay;
        this.mLastFrame = frameRess.length - 1;
//        playAndDelay(0);
        frameThread = new FrameThread();
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    public FingerprintAnimation(ImageView iv, int[] frameRess, int[] durations, int delay) {
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDurations = durations;
        this.mDelay = delay;
        this.mLastFrame = frameRess.length - 1;
//        playByDurationsAndDelay(0);
        frameThread = new FrameThread();
    }

    private void playByDurationsAndDelay(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {   // 暂停和播放需求
                    mCurrentSelect = SELECTED_A;
                    mCurrentFrame = i;
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setBackgroundResource(mFrameRess[i]);
                if (i == mLastFrame) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationRepeat();
                    }
                    mNext = true;
                    playByDurationsAndDelay(0);
                } else {
                    playByDurationsAndDelay(i + 1);
                }
            }
        }, mNext && mDelay > 0 ? mDelay : mDurations[i]);

    }

    private void playAndDelay(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_B;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                mNext = false;
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setBackgroundResource(mFrameRess[i]);
                if (i == mLastFrame) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationRepeat();
                    }
                    mNext = true;
                    playAndDelay(0);
                } else {
                    playAndDelay(i + 1);
                }
            }
        }, mNext && mDelay > 0 ? mDelay : mDuration);

    }

    private void playByDurations(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_C;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setBackgroundResource(mFrameRess[i]);
                if (i == mLastFrame) {
                    if (mIsRepeat) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationRepeat();
                        }
                        playByDurations(0);
                    } else {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationEnd();
                        }
                    }
                } else {

                    playByDurations(i + 1);
                }
            }
        }, mDurations[i]);

    }

    private void play(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_D;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                // add by zhangzhanbo on 20181015 start
                if (i > mFrameRess.length - 1) {
                    play(0);
                    return;
                }
                // add by zhangzhanbo on 20181015 end
                mImageView.setVisibility(View.VISIBLE);
                mImageView.setBackgroundResource(mFrameRess[i]);
                if (i == mLastFrame) {

                    if (mIsRepeat) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationRepeat();
                        }
                        play(0);
                    } else {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationEnd();
                        }
                    }

                } else {

                    play(i + 1);
                }
            }
        }, mDuration);
    }

    class FrameThread extends Thread {

        private boolean stopFlag = false;

        private boolean runningFlag = false;

        public void stop(boolean stopFlag) {
            this.stopFlag = stopFlag;
        }

        public boolean isStop() {
            return stopFlag;
        }

        public boolean isRunning() {
            return runningFlag;
        }

        @Override
        public void run() {
            runningFlag = true;
            int currentFrame = 0;
            while (true) {
                try {
                    Thread.sleep(mDuration);
                    mImageView.setImageResource(mFrameRess[currentFrame]);
                    currentFrame ++;
                } catch (Exception e) {
                } finally {
                    if (stopFlag || (currentFrame >= mFrameRess.length && !mIsRepeat)) {
                        runningFlag = false;
                        break;
                    } else if (currentFrame >= mFrameRess.length) {
                        currentFrame = 0;
                        continue;
                    }
                }
            }
        }
    }

    public static interface AnimationListener {

        /**
         * <p>Notifies the start of the animation.</p>
         */
        void onAnimationStart();

        /**
         * <p>Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         */
        void onAnimationEnd();

        /**
         * <p>Notifies the repetition of the animation.</p>
         */
        void onAnimationRepeat();
    }

    /**
     * <p>Binds an animation listener to this animation. The animation listener
     * is notified of animation events such as the end of the animation or the
     * repetition of the animation.</p>
     *
     * @param listener the animation listener to be notified
     */
    public void setAnimationListener(AnimationListener listener) {
        this.mAnimationListener = listener;
    }

    public void release() {
        pauseAnimation();
        mImageView.setVisibility(View.INVISIBLE);
    }

    public void stopAnimation() {
        if (frameThread != null && frameThread.isRunning()) {
            frameThread.stop(true);
        }
        frameThread = null;
        mImageView.setVisibility(View.INVISIBLE);
    }

    public void pauseAnimation() {
        if (frameThread != null && frameThread.isRunning()) {
            try {
                frameThread.wait();
            } catch (Exception e) {
            }
        }
    }

    public void startAnimation() {
        if (frameThread == null) {
            frameThread = new FrameThread();
        }
        if (!frameThread.isRunning()) {
            try {
                frameThread.start();
            } catch (Exception e) {
                Log.i("elifli", "Error: " + e.getMessage());
            }
        }
        mImageView.setVisibility(View.VISIBLE);
    }

    public boolean isPause() {
        return this.mPause;
    }

    public void restartAnimation() {
        if (frameThread != null) {
            frameThread.stop(true);
            frameThread = null;
        }
        startAnimation();
    }

    public static int[] getRes(Context context, @ArrayRes int id) {
        TypedArray typedArray = context.getResources().obtainTypedArray(id);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }

    // add by zhangzhanbo on 20180910 start
    public void setmFrameRess(int[] images) {
        mFrameRess = images;
        this.mLastFrame = mFrameRess.length - 1;
    }

    public void setmDuration(int duration) {
        this.mDuration = duration;
    }
    // add by zhangzhanbo on 20180910 end
}
