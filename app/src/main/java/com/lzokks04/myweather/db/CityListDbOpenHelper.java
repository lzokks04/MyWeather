package com.lzokks04.myweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Liu on 2016/8/12.
 */
public class CityListDbOpenHelper extends SQLiteOpenHelper{

    public static final String CREATE_SQL = "create table citylistinfo (province_name text," +
            "city_name text," +
            "city_code text)";



    public CityListDbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
