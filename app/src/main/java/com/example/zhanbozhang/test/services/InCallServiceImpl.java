package com.example.zhanbozhang.test.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class InCallServiceImpl extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new ServicesBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class ServicesBinder extends Binder {
        public InCallServiceImpl getServices() {
            return InCallServiceImpl.this;
        }
    }
}
