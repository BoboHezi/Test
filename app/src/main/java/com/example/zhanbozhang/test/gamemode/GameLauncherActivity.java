package com.example.zhanbozhang.test.gamemode;

import android.app.ActivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.zhanbozhang.test.R;

import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.M)
public class GameLauncherActivity extends AppCompatActivity {

    private static final String TAG = "GameLauncherActivity";

    private long mBackPressedTime = 0;

    private RecyclerView recyclerGrid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_launcher_activity);

        recyclerGrid = findViewById(R.id.test_grid_recycle);
        recyclerGrid.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        recyclerGrid.setHasFixedSize(true);
    }

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - mBackPressedTime > 1000) {
            Toast.makeText(this, "Press again to exit.", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
        mBackPressedTime = now;
    }

    public void clearRecent(View view) {
        List<ActivityManager.RecentTaskInfo> recent = Utils.getRecentTaskButThis(this);
        Log.i(TAG, "size: " + recent.size());
        for (ActivityManager.RecentTaskInfo info : recent) {
            StringBuilder sb = new StringBuilder();
            sb.append("Task: ");
            sb.append(" id: ").append(info.id);
            sb.append(" persistentId: ").append(info.persistentId);
            sb.append(" origActivity: ").append(info.origActivity);
            sb.append(" topActivity: ").append(info.topActivity);
            sb.append(" numActivities: ").append(info.numActivities);
            Log.i(TAG, sb.toString());
        }

        for (ActivityManager.RecentTaskInfo info : recent) {
            try {
                //ActivityManager.getService().removeTask(info.persistentId);
            } catch (Exception e) {
                Log.i(TAG, "onCreate: " + e.getMessage());
            }
        }
    }
}
