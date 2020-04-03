// IMyAidlInterface.aidl
package com.example.zhanbozhang.test.aidl;

import com.example.zhanbozhang.test.aidl.User;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    String getName();

    User getUser();
}
