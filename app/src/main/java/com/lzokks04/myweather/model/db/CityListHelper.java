package com.lzokks04.myweather.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzokks04.myweather.model.bean.CityDetailBean;
import com.lzokks04.myweather.model.bean.CityWeatherBean;
import com.lzokks04.myweather.model.bean.CityWeatherConBean;
import com.lzokks04.myweather.model.bean.DailyWeather;
import com.lzokks04.myweather.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu on 2016/8/12.
 */
public class CityListHelper {

    public static final String DB_NAME = "weatherdb";

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
     * 将城市列表数据保存到数据库
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

    public List<CityDetailBean> loadCityData() {
        List<CityDetailBean> beanList = null;
        Cursor cursor = null;
        try {
            beanList = new ArrayList<>();
            cursor = db.query("citylistinfo", null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    CityDetailBean bean = new CityDetailBean();
                    bean.setProvince_name(cursor.getString(cursor.getColumnIndex("province_name")));
                    bean.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
                    bean.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
                    beanList.add(bean);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return beanList;
    }

    /**
     * 保存当前天气信息到数据库
     *
     * @param bean
     */
    public void saveCityWeather(CityWeatherBean bean) {
        if (bean != null) {
            ContentValues values = new ContentValues();
            values.put("city", bean.getHeWeatherdataservice().get(0).getBasic().getCity());
            values.put("temp", bean.getHeWeatherdataservice().get(0).getNow().getTmp() + "°");
            values.put("weather", bean.getHeWeatherdataservice().get(0).getNow().getCond().getTxt());
            values.put("hum", "湿度:" + bean.getHeWeatherdataservice().get(0).getNow().getHum() + "%");
            values.put("wind", bean.getHeWeatherdataservice().get(0).getNow().getWind().getDir()
                    + bean.getHeWeatherdataservice().get(0).getNow().getWind().getSc() + "级");
            values.put("code", bean.getHeWeatherdataservice().get(0).getNow().getCond().getCode());
            values.put("time", Utils.getLastTime(bean.getHeWeatherdataservice().get(0).
                    getBasic().getUpdate().getLoc()) + "更新");
            db.insert("cityweather", null, values);
        }
    }

    /**
     * 保存每日信息到数据库
     *
     * @param bean
     */
    public void saveDailyWeather(CityWeatherBean bean) {
        if (bean != null) {
            for (int i = 0; i < bean.getHeWeatherdataservice().get(0).getDaily_forecast().size(); i++) {
                ContentValues values = new ContentValues();
                values.put("date", Utils.getMonthDay(bean.getHeWeatherdataservice().get(0).
                        getDaily_forecast().get(i).getDate()));
                values.put("lweather", bean.getHeWeatherdataservice().get(0).
                        getDaily_forecast().get(i).getCond().getTxt_d());
                values.put("lcode", bean.getHeWeatherdataservice()
                        .get(0).getDaily_forecast().get(i).getCond().getCode_d());
                values.put("ltemp", bean.getHeWeatherdataservice().get(0).
                        getDaily_forecast().get(i).getTmp().getMax() + "°");
                values.put("ntemp", bean.getHeWeatherdataservice().get(0).
                        getDaily_forecast().get(i).getTmp().getMin() + "°");
                values.put("ncode", bean.getHeWeatherdataservice()
                        .get(0).getDaily_forecast().get(i).getCond().getCode_n());
                values.put("nweather", bean.getHeWeatherdataservice().get(0).
                        getDaily_forecast().get(i).getCond().getTxt_n());
                values.put("prob", bean.getHeWeatherdataservice().get(0).
                        getDaily_forecast().get(i).getPop() + "%");
                db.insert("dailyweather", null, values);
            }
        }
    }

    /**
     * 将城市天气从数据库读取出来
     *
     * @return
     */
    public CityWeatherConBean loadCityWeather() {
        Cursor cursor = null;
        CityWeatherConBean bean = null;
        try {
            cursor = db.query("cityweather", null, null, null, null, null, null);
            bean = new CityWeatherConBean();
            if (cursor.moveToFirst()) {
                do {
                    bean.setCity(cursor.getString(cursor.getColumnIndex("city")));
                    bean.setCode(cursor.getString(cursor.getColumnIndex("code")));
                    bean.setHum(cursor.getString(cursor.getColumnIndex("hum")));
                    bean.setTemp(cursor.getString(cursor.getColumnIndex("temp")));
                    bean.setWind(cursor.getString(cursor.getColumnIndex("wind")));
                    bean.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
                    bean.setTime(cursor.getString(cursor.getColumnIndex("time")));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bean;
    }

    /**
     * 从数据库中获取保存的每日天气信息
     *
     * @return
     */
    public List<DailyWeather> loadDailyWeather() {
        Cursor cursor = null;
        List<DailyWeather> beanList = null;
        try {
            cursor = db.query("dailyweather", null, null, null, null, null, null);
            beanList = new ArrayList<DailyWeather>();
            if (cursor.moveToFirst()) {
                do {
                    DailyWeather bean = new DailyWeather();
                    bean.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    bean.setlWeather(cursor.getString(cursor.getColumnIndex("lweather")));
                    bean.setlCode(cursor.getString(cursor.getColumnIndex("lcode")));
                    bean.setlTemp(cursor.getString(cursor.getColumnIndex("ltemp")));
                    bean.setnTemp(cursor.getString(cursor.getColumnIndex("ntemp")));
                    bean.setnCode(cursor.getString(cursor.getColumnIndex("ncode")));
                    bean.setnWeather(cursor.getString(cursor.getColumnIndex("nweather")));
                    bean.setProb(cursor.getString(cursor.getColumnIndex("prob")));
                    beanList.add(bean);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return beanList;
    }

    /**
     * 清空当前天气表
     */
    public void clearCityWeatherTable() {
        db.execSQL("delete from cityweather");
    }

    /**
     * 清空每日天气表
     */
    public void clearDailyWeatherTable() {
        db.execSQL("delete from dailyweather");
    }
}
