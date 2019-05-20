package com.example.zhanbozhang.test.utils;

import android.util.Log;

public class Utils {

    static {
        Log.i("elifli", "Utils static");
    }

    static public int number = 39;

    static public int getNumber() {
        return number;
    }

}
