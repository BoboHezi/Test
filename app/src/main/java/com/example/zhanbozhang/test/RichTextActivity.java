package com.example.zhanbozhang.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

public class RichTextActivity extends Activity {

    private static final String TAG = "RichTextActivity";

    private TextView richTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rich_text_activity);
        richTextView = findViewById(R.id.rich_text);

        new ParseRichTextTask().execute(R.raw.privacy_policy, richTextView);
    }
}
