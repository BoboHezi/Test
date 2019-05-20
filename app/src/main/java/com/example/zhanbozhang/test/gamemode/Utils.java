package com.example.zhanbozhang.test.gamemode;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<PackageInfo> getThirdApps(Context context) {
        List<PackageInfo> thirdApp = new ArrayList<>();
        for (PackageInfo info : context.getPackageManager().getInstalledPackages(0)) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                thirdApp.add(info);
            }
        }
        return thirdApp;
    }

    public static List<ActivityManager.RecentTaskInfo> getRecentTask(Context context) {
        return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
                .getRecentTasks(Integer.MAX_VALUE, ActivityManager.RECENT_WITH_EXCLUDED);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static List<ActivityManager.RecentTaskInfo> getRecentTaskButThis(Context context) {
        List<ActivityManager.RecentTaskInfo> recent = new ArrayList<>();
        String thisPackage = context.getPackageName();
        for (ActivityManager.RecentTaskInfo taskInfo : getRecentTask(context)) {
            if (taskInfo.topActivity != null && !thisPackage.equals(taskInfo.topActivity.getPackageName())) {
                recent.add(taskInfo);
            }
        }
        return recent;
    }

}
