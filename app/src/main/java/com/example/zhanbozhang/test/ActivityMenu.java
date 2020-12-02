package com.example.zhanbozhang.test;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhanbozhang.test.model.Summary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityMenu extends AppCompatActivity {

    private RecyclerView activitiesRv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menus);

        activitiesRv = findViewById(R.id.activities_rv);

        PackageManager pm = getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("elifli", "NameNotFoundException: " + e);
        }
        if (pi != null) {
            ActivityInfo[] activities = new ActivityInfo[pi.activities.length - 1];
            int index = 0;
            for (ActivityInfo ai : pi.activities) {
                if (!ai.getComponentName().equals(getComponentName())) {
                    activities[index] = ai;
                    index ++;
                }
            }
            activitiesRv.setLayoutManager(new LinearLayoutManager(this));
            activitiesRv.setAdapter(new ActivitiesAdapter(this, activities, pm));
            activitiesRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }

        getRam();
    }

    private int getRam() {
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getMemoryInfo(memInfo);
        String ram = Formatter.formatFileSize(this, memInfo.totalMem);
        float v = Float.valueOf(ram.substring(0, ram.indexOf(" GB")));
        Log.i("elifli", "getRam: " + Math.round(v));
        return (int) v;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onResume() {
        super.onResume();

        List<Summary> originList = new ArrayList<>();
        originList.add(new Summary("CS", 84, "uryhf"));
        originList.add(new Summary("CS", 84, "dhyehr"));
        originList.add(new Summary("de", 3, "zhdye"));
        originList.add(new Summary("de", 1, "djfur"));
        originList.add(new Summary("CS", 3, "dufjr"));
        originList.add(new Summary("cs", 4, "atqrw"));
        originList.add(new Summary("Wa", 54, "djfur"));
        originList.add(new Summary("Wb", 12, "23eifjg"));
        originList.add(new Summary("DE", 43, "dhjuejf"));
        originList.add(new Summary("wA", 12, "fjujf"));
        originList.add(new Summary("wA", 14, "fjujf"));
        originList.add(new Summary("w3", 3, "zhdye"));
        originList.add(new Summary("4r", 1, "djfur"));
        originList.add(new Summary("CS", 84, "urxhf"));
        originList.add(new Summary("CS", 84, "uaxhf"));
        originList.add(new Summary("ty", 4, "没有"));
        originList.add(new Summary("ty", 4, "到了"));
        originList.add(new Summary("2w", 3, "dufjr"));
        originList.add(new Summary("ty", 4, "不会"));
        originList.add(new Summary("ty", 4, "xes"));

        Log.i("elifli", "origin list: ");
        originList.forEach(summary -> Log.i("elifli", "" + summary));

        originList.stream().filter(Summary.PREDICATE);

        Collections.sort(originList, Summary.COMPARATOR);

        Log.i("elifli", "sorted list: ");
        originList.forEach(summary -> Log.i("elifli", "" + summary));
    }

    class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ActivityHolder> {

        private ActivityInfo[] activities;

        private Context context;

        private PackageManager pm;

        public ActivitiesAdapter(Context context, ActivityInfo[] activities, PackageManager pm) {
            this.activities = activities;
            this.context = context;
            this.pm = pm;
        }

        @NonNull
        @Override
        public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ActivityHolder(LayoutInflater.from(context).inflate(R.layout.activity_item, parent, false));
        }

        @Override
        public int getItemCount() {
            return activities != null ? activities.length : 0;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
            ActivityInfo activityInfo = activities[position];
            holder.icon.setImageDrawable(activityInfo.loadIcon(pm));
            holder.label.setText(activityInfo.loadLabel(pm));
            holder.className.setText(activityInfo.getComponentName().flattenToShortString());

            holder.itemView.setOnClickListener(v -> startActivity(new Intent().setComponent(activityInfo.getComponentName())));
        }

        class ActivityHolder extends RecyclerView.ViewHolder {

            ImageView icon;
            TextView label;
            TextView className;

            public ActivityHolder(View itemView) {
                super(itemView);

                icon = itemView.findViewById(R.id.activity_icon);
                label = itemView.findViewById(R.id.activity_label);
                className = itemView.findViewById(R.id.activity_class);
            }
        }
    }
}
