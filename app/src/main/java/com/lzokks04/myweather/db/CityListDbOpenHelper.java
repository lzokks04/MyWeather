package com.lzokks04.myweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Liu on 2016/8/12.
 */
public class CityListDbOpenHelper extends SQLiteOpenHelper{

    public static final String CREATE_CITYLIST = "create table Citylist (city varchar(20)," +
            "cnty varchar(10)," +
            " id varchar(15)," +
            " lat varchar(10)," +
            " lon varchar(10)," +
            " prov varchar(20))";

    public static final String CREATE_PROVINCE = "create table citylistinfo (id integer primary key autoincrement," +
            " province_name text" +
            " city_name text" +
            " city_code text)";



    public CityListDbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CITYLIST);
        sqLiteDatabase.execSQL(CREATE_PROVINCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
