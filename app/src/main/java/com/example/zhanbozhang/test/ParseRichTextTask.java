package com.example.zhanbozhang.test;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ParseRichTextTask extends AsyncTask {

    private static final String TAG = "ParseRichTextTask";

    private TextView mTextView;

    private Context mContext;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        if (objects == null || objects.length < 2) {
            return null;
        }
        int id = (int) objects[0];
        mTextView = (TextView) objects[1];
        mContext = mTextView.getContext();

        InputStream inputStream = mContext.getResources().openRawResource(id);
        String source = getString(inputStream);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        try {
            JSONArray jsonArray = new JSONArray(source);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                SpannableString spannable = parseSpannable(object);
                builder.append(spannable);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Exception: " + e);
        }
        return builder;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        mTextView.setTextIsSelectable(true);
        mTextView.setText((SpannableStringBuilder) o);
        mTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private SpannableString parseSpannable(JSONObject object) {

        final String content = (String) getObject(object, "content");

        Object scale = getObject(object, "font-scale");
        final double fontScale = scale == null ? -1 : (double) scale;

        Object face = getObject(object, "type-face");
        final int typeFace = face == null ? -1 : (int) face;

        Object margin = getObject(object, "leading-margin");
        final int leadingMargin = margin == null ? -1 : (int) margin;

        final String url = (String) getObject(object, "url");
        final String mail = (String) getObject(object, "mail");

        SpannableString spannable = new SpannableString(content);

        if (fontScale > 0) {
            spannable.setSpan(new RelativeSizeSpan((float) fontScale),
                    0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (typeFace > 0) {
            spannable.setSpan(new StyleSpan(typeFace),
                    0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (leadingMargin >= 0) {
            spannable.setSpan(new LeadingMarginSpan.Standard(0, leadingMargin),
                    0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (url != null && url.length() > 0) {
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        Log.i(TAG, "startActivity Exception: " + e);
                    }
                }
            }, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (mail != null && mail.length() > 0) {
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        mContext.startActivity(intent);
                    } catch (Exception e) {
                        Log.i(TAG, "startActivity Exception: " + e);
                    }
                }
            }, 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannable;
    }

    private Object getObject(JSONObject object, String key) {
        try {
            if (object != null && object.has(key)) {
                return object.get(key);
            }
        } catch (Exception e) {
            Log.i(TAG, "getObject Exception: " + e);
        }
        return null;
    }

    private String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            Log.i(TAG, "IOException: " + e);
        }
        return sb.toString();
    }
}
