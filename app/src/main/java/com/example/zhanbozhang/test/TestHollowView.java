package com.example.zhanbozhang.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.zhanbozhang.test.utils.Utils;

public class TestHollowView extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holow);
        ImageView imageView = new ImageView(this);
        imageView.getDrawable();

        View v = findViewById(R.id.view_container);
        v.postDelayed(() -> {
            Utils.saveBitmap(Utils.viewConversionBitmap(v), "/storage/emulated/0/Pictures/Test/hollow.png");
        }, 5000);
    }
}
