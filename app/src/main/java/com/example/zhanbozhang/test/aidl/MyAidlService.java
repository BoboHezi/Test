package com.example.zhanbozhang.test.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.system.Os;
import android.util.Log;

public class MyAidlService extends Service {

    private static final String TAG = "MyAidlService";

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind: ");
        return new MyBinder();
    }

    class MyBinder extends IMyAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            Log.i(TAG, "anInt: " + anInt + ", " + aLong + ", " + aLong + ", " + aBoolean + ", " + aBoolean + ", " + anInt);
        }

        @Override
        public String getName() throws RemoteException {
            Log.i(TAG, "getName: ");
            return "This is Eli from service";
        }

        @Override
        public User getUser() throws RemoteException {
            Log.i(TAG, "server pid: " + Os.getpid() + ", ppid: " + Os.getppid());
            /*if (true) {
                throw new RuntimeException("Failed to decrypt blob");
            }*/
            return new User("Eli-Chang");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "onStart: ");
        super.onStart(intent, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }
}
