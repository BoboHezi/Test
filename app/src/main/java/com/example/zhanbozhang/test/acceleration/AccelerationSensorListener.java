package com.example.zhanbozhang.test.acceleration;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanbo.zhang on 2018/9/14.
 */

public class AccelerationSensorListener implements SensorEventListener {

    private static final String TAG = "elifli";

    private static AccelerationSensorListener instances;
    private Context mContext;
    private SensorManager mSensorManager;
    private Sensor mGravitySensor;
    private Sensor mLinearAcceleration;
    private Stack mAccelerationDataStack;

    private boolean isPhoneStable = true;

    private static final double PHONE_STABLE_ACCE = 0.47;
    private static final double PHONE_UNSTABLE_ACCE = 3.5;

    private static List<OnPhoneMotionListener> mMotionListeners = new ArrayList<>();

    private AnalyzeThread analyzeThread;

    public static AccelerationSensorListener getInstances(Context context) {
        return getInstances(context, null);
    }

    public static AccelerationSensorListener getInstances(Context context, OnPhoneMotionListener gravityChangedListener) {
        if (instances == null) {
            instances = new AccelerationSensorListener(context, gravityChangedListener);
        }
        mMotionListeners.add(0, gravityChangedListener);
        return instances;
    }

    private AccelerationSensorListener(Context context, OnPhoneMotionListener gravityChangedListener) {
        mContext = context;
        if (gravityChangedListener != null) {
            mMotionListeners.add(gravityChangedListener);
        }
        if (mContext != null) {
            mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
            mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }
        mAccelerationDataStack = new Stack<>(70);
        analyzeThread = new AnalyzeThread();
        Log.i(TAG, "AccelerationSensorListener created");
    }

    public void addMotionListener(OnPhoneMotionListener listener) {
        if (listener != null) {
            mMotionListeners.add(listener);
        }
    }

    public void startMotionListener() {
        if (mSensorManager != null) {
            Log.i(TAG, "startMotionListener");
            mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_UI);
            if (!analyzeThread.isInterrupted()) {
                analyzeThread.start();
            }
        }
    }

    public void stopMotionListener() {
        if (mSensorManager != null) {
            Log.i(TAG, "stopMotionListener");
            mSensorManager.unregisterListener(this, mLinearAcceleration);
            analyzeThread.interrupt();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            //Log.i(TAG, "X: " + nf.format(x) + "\tY: " + nf.format(y) + "\tZ: " + nf.format(z));
        } else if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            //Log.i(TAG, "X: " + nf.format(x) + "\tY: " + nf.format(y) + "\tZ: " + nf.format(z));

            synchronized (mAccelerationDataStack) {
                mAccelerationDataStack.addElement(new AccelerateData(x, y, z));
            }
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            //Log.i(TAG, "X: " + nf.format(x) + "\tY: " + nf.format(y) + "\tZ: " + nf.format(z));
        }
    }

    public interface OnPhoneMotionListener {
        void onAccelerationChanged(int type, float x, float y, float z);

        void onPhoneUplifted();

        void onPhoneLieDown();

        void onPhoneUnstable();

        void onPhoneStable();

        void onOtherInfo(String text);
    }

    class AnalyzeThread extends Thread {

        private boolean interrupted = false;

        @Override
        public boolean isInterrupted() {
            return interrupted;
        }

        @Override
        public void interrupt() {
            super.interrupt();
            interrupted = true;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);

                    if (mAccelerationDataStack == null) {
                        continue;
                    }

                    synchronized (mAccelerationDataStack) {
                        int size = mAccelerationDataStack.currentSize();
                        if (isPhoneStable) {
                            if (size >= 10) {
                                float sum = 0;
                                for (int i = 1; i <= 10; i++) {
                                    AccelerateData data = (AccelerateData) mAccelerationDataStack.getData(size - i);
                                    sum += Math.abs(data.AX) + Math.abs(data.AY) + Math.abs(data.AZ);
                                }

                                if ((sum / 10) > PHONE_UNSTABLE_ACCE) {
                                    isPhoneStable = false;
                                    if (mMotionListeners != null && mMotionListeners.size() > 0) {
                                        for (OnPhoneMotionListener listener : mMotionListeners) {
                                            listener.onPhoneUnstable();
                                        }
                                    }
                                }
                            }
                        } else {
                            double[] xDates = new double[size];
                            double[] yDates = new double[size];
                            double[] zDates = new double[size];

                            for (int i = 0; i < size; i++) {
                                AccelerateData data = (AccelerateData) mAccelerationDataStack.getData(i);
                                xDates[i] = data.AX;
                                yDates[i] = data.AY;
                                zDates[i] = data.AZ;
                            }

                            double aveX = average(true, xDates);
                            double aveY = average(true, yDates);
                            double aveZ = average(true, zDates);

                            double aveSUM = aveX + aveY + aveZ;

                            if (aveSUM < PHONE_STABLE_ACCE) {
                                isPhoneStable = true;
                                if (mMotionListeners != null && mMotionListeners.size() > 0) {
                                    for (OnPhoneMotionListener listener : mMotionListeners) {
                                        listener.onPhoneStable();
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "error: " + e.getMessage());
                    if (isInterrupted()) {
                        return;
                    }
                }
            }
        }
    }

    private double disperse(double[] dates) {
        double average = average(false, dates);

        double total1 = 0;
        for (int i = 0; i < dates.length; i++) {
            double diff = Math.abs(average - dates[i]);
            total1 += diff * diff;
        }
        return Math.sqrt(total1 / dates.length);
    }

    private double average(boolean isABS, double[] dates) {
        double average;
        double total = 0;

        for (int i = 0; i < dates.length; i++) {
            total += isABS ? Math.abs(dates[i]) : dates[i];
        }
        average = total / dates.length;

        return average;
    }

    class AccelerateData {

        public float AX;
        public float AY;
        public float AZ;

        public AccelerateData(float AX, float AY, float AZ) {
            this.AX = AX;
            this.AY = AY;
            this.AZ = AZ;
        }
    }

    class Stack<T> {

        private List<T> array;

        private int size;

        private int currentNumber = 0;

        public Stack(int size) {
            this.size = size;
            if (size >= 0) {
                array = new ArrayList(size);
            }
            array.toArray();
        }

        public void addElement(T t) {
            if (currentNumber < size) {
                array.add(currentNumber, t);
                currentNumber++;
            } else {
                popFirst();
                array.set(currentNumber - 1, t);
            }
        }

        public T getFirst() {
            return array.get(0);
        }

        public T getLast() {
            return array.get(currentNumber - 1);
        }

        public T getData(int position) {
            if (position < 0 || position > currentNumber) {
                return null;
            }

            return array.get(position);
        }

        public int currentSize() {
            return currentNumber;
        }

        private void popFirst() {
            if (size > 1 && currentNumber == size && array != null) {
                for (int i = 1; i < array.size(); i++) {
                    array.set(i - 1, array.get(i));
                }
            }
        }
    }
}
