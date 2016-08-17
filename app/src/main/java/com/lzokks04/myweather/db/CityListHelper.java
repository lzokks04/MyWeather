package com.lzokks04.myweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzokks04.myweather.bean.CityDetailBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Liu on 2016/8/12.
 */
public class CityListHelper {
    /**
     * 數據庫名
     */
    public static final String DB_NAME = "Citylist";
    /**
     * 數據庫版本
     */
    public static final int VER = 1;

    private static CityListHelper cityListHelper;

    private SQLiteDatabase db;

    /**
     * 將構造方法私有化
     *
     * @param context
     */
    private CityListHelper(Context context) {
        CityListDbOpenHelper helper = new CityListDbOpenHelper(context, DB_NAME, null, VER);
        db = helper.getWritableDatabase();
    }

    /**
     * 獲取CityListHelper的實例
     *
     * @param context
     * @return
     */
    public synchronized static CityListHelper getInstance(Context context) {
        if (cityListHelper == null) {
            cityListHelper = new CityListHelper(context);
        }
        return cityListHelper;
    }

    /**
     * 将数据保存到数据库
     *
     * @param beanList
     */
    public void saveDataToDB(List<CityDetailBean> beanList) {
        if (beanList != null) {
            for (int i = 0; i < beanList.size(); i++) {
                ContentValues values = new ContentValues();
                values.put("province_name", beanList.get(i).getProvince_name());
                values.put("city_name", beanList.get(i).getCity_name());
                values.put("city_code", beanList.get(i).getCity_code());
                db.insert("citylistinfo", null, values);
            }
        }
    }

    /**
     * 将省份列表提取出来
     * @return
     */
    public List<String> loadProvData() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = db.query("citylistinfo", null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                list.add(cursor.getString(cursor.getColumnIndex("province_name")));
            }while (cursor.moveToNext());
        }
        //去重
        Set<String> set = new HashSet<String>();
        for (int i =0;i<list.size();i++){
            set.add(list.get(i));
        }
        list.clear();
        for (String str :set){
            list.add(str);
        }
        return list;
    }

    /**
     * 传入省份，读取出省份所在的城市列表
     * @param prov
     * @return
     */
    public List<String> loadCityData(String prov){
        List<String> list = new ArrayList<String>();
        Cursor cursor = db.query("citylistinfo", null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                if (prov.equals(cursor.getString(cursor.getColumnIndex("province_name")))){
                    list.add(cursor.getString(cursor.getColumnIndex("city_name")));
                }
            }while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 传入城市，读取出省份所在的城市的id
     * @param city
     * @return
     */
    public String loadCityCode(String city){
        String cityCode = null;
        Cursor cursor = db.query("citylistinfo", null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                if (city.equals(cursor.getString(cursor.getColumnIndex("city_name")))){
                    cityCode = cursor.getString(cursor.getColumnIndex("city_code"));
                }
            }while (cursor.moveToNext());
        }
        return cityCode;
    }
}
