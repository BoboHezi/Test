package com.example.zhanbozhang.test.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.zhanbozhang.test.utils.Utils;

import java.util.List;


public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";

    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //判断广播消息
        if (action.equals(SMS_RECEIVED_ACTION)) {
            Bundle bundle = intent.getExtras();
            //如果不为空
            if (bundle != null) {
                //将pdus里面的内容转化成Object[]数组
                Object pdusData[] = (Object[]) bundle.get("pdus");
                //解析短信
                SmsMessage[] msg = new SmsMessage[pdusData.length];
                for (int i = 0; i < msg.length; i++) {
                    byte pdus[] = (byte[]) pdusData[i];
                    msg[i] = SmsMessage.createFromPdu(pdus);
                }
                StringBuffer content = new StringBuffer();//获取短信内容
                StringBuffer phoneNumber = new StringBuffer();//获取地址
                //分析短信具体参数
                for (SmsMessage temp : msg) {
                    content.append(temp.getMessageBody());
                    phoneNumber.append(temp.getOriginatingAddress());
                }

                Log.i(TAG, "from: " + phoneNumber.toString() + ", message: " + content.toString());

                if ("18816507830".equals(phoneNumber.toString())) {
                    abortBroadcast();
                    markMessageRead(context, phoneNumber.toString(), content.toString());

                    ContentResolver resolver = context.getContentResolver();
                    if (null != resolver) {
                        int delete = resolver.delete(Uri.parse("content://sms/"), "address=? or address = ?",
                                new String[]{phoneNumber.toString(), "+86" + phoneNumber});
                        Log.i(TAG, "delete: " + delete);
                    }
                }
            }

            Location location = Utils.getLocation(context);
            Log.i(TAG, "location: " + location);
        }
    }

    private void markMessageRead(Context context, String number, String body) {
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        try{
            while (cursor.moveToNext()) {
                if ((cursor.getString(cursor.getColumnIndex("address")).equals(number)) && (cursor.getInt(cursor.getColumnIndex("read")) == 0)) {
                    if (cursor.getString(cursor.getColumnIndex("body")).startsWith(body)) {
                        String SmsMessageId = cursor.getString(cursor.getColumnIndex("_id"));
                        ContentValues values = new ContentValues();
                        values.put("read", 1);
                        int row = context.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + SmsMessageId, null);
                        Log.i(TAG, "row: " + row);
                        return;
                    }
                }
            }
        } catch(Exception e) {
            Log.e("Mark Read", "Error in Read: "+e.toString());
        }
    }
}
