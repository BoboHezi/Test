package com.example.zhanbozhang.test.youxi;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.zhanbozhang.test.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class YouxiActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private YouxiAdapter adapter;

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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new FourSpacesItemDecoration(dp2px(this, 8.5f),
                dp2px(this, 10.5f), 2));
        adapter = new YouxiAdapter(this, datas);
        View header = LayoutInflater.from(this).inflate(R.layout.banner, null);
        adapter.setHeaderView(header);
        recyclerView.setAdapter(adapter);
    }

    private void saveBitmap(Bitmap bitmap, String path) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            Log.i("", "bitmap: " + bitmap);
            return;
        }
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = path;
        } else {
            Log.e("tag", "saveBitmap failure : sdcard not mounted");
            return;
        }
        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("tag", "saveBitmap: " + e.getMessage());
            return;
        }
    }


    public static int dp2px(Context context, float dpValue) {
        if (context == null) {
            return 0;
        }
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }
}
