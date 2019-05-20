package com.example.zhanbozhang.test.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TestDataDao extends SQLiteOpenHelper {

    private static final String DB_NAME = "private_contacts.db";

    private static final int DB_VERSION = 1;

    private static final String TABLE_TEST = "test";

    //创建 students 表的 sql 语句
    private static final String TEST_CREATE_TABLE_SQL = "create table " + TABLE_TEST + "("
            + "id integer primary key autoincrement,"
            + "name varchar(20) not null,"
            + "tel_no varchar(11) not null,"
            + "cls_id integer not null"
            + ");";

    private static TestDataDao dataDao;

    public static final TestDataDao getInstance(Context context) {
        if (dataDao == null) {
            dataDao = new TestDataDao(context);
        }
        return dataDao;
    }

    private TestDataDao(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TEST_CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
