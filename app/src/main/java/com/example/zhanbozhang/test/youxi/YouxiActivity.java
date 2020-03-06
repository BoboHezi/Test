package com.example.zhanbozhang.test.youxi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.example.zhanbozhang.test.R;

import java.util.ArrayList;

public class YouxiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youxi_act);

        recyclerView = findViewById(R.id.videos_rv);

        ArrayList<String> datas = new ArrayList<>();
        datas.add("1jruj");
        datas.add("29eid");
        datas.add("38dur");
        datas.add("4w无e");
        datas.add("5rir7");
        datas.add("6mfju");
        datas.add("7wexc");
        datas.add("84ur7");
        datas.add("9r8zh");
        datas.add("10dk9");
        datas.add("11ruf");
        datas.add("12fuf");
        datas.add("1338e");
        datas.add("14dfh");
        datas.add("15hu3");
        datas.add("16yss");
        datas.add("17hu3");
        YouxiAdapter adapter = new YouxiAdapter(this, datas);
        View header = LayoutInflater.from(this).inflate(R.layout.banner, null);
        adapter.setHeaderView(header);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new FourSpacesItemDecoration(dp2px(this, 8.5f),
                dp2px(this, 10.5f), 2));
    }


    public static int dp2px(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}
