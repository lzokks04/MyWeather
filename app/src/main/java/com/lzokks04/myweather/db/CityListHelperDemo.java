package com.lzokks04.myweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.lzokks04.myweather.bean.CityDetailBean;
import com.lzokks04.myweather.bean.CityListBean;
import com.lzokks04.myweather.util.API;
import com.lzokks04.myweather.util.NetUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu on 2016/8/16.
 */
public class CityListHelperDemo {
    /**
     * 數據庫名
     */
    public static final String DB_NAME = "Citylist";
    /**
     * 數據庫版本
     */
    public static final int VER = 1;

    private static CityListHelperDemo cityListHelperDemo;

    private SQLiteDatabase db;

    /**
     * 將構造方法私有化
     *
     * @param context
     */
    private CityListHelperDemo(Context context) {
        CityListDbOpenHelper helper = new CityListDbOpenHelper(context, DB_NAME, null, VER);
        db = helper.getWritableDatabase();
    }

    /**
     * 獲取CityListHelper的實例
     *
     * @param context
     * @return
     */
    public synchronized static CityListHelperDemo getInstance(Context context) {
        if (cityListHelperDemo == null) {
            cityListHelperDemo = new CityListHelperDemo(context);
        }
        return cityListHelperDemo;
    }

    /**
     * 將城市列表下載
     */
    public synchronized void cityListDataDown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonData = NetUtil.getJsonData(API.CITYLIST_CHINA_URL + API.USER_ID);
                Gson gson = new Gson();
                CityListBean bean = gson.fromJson(jsonData, CityListBean.class);
                convertData(bean);
            }
        }).start();
    }

    /**
     * 将解析的数据提取，只保留省份，城市，城市id
     * @param bean
     */
    public void convertData(CityListBean bean){
        List<CityDetailBean> beanList = new ArrayList<CityDetailBean>();
        CityDetailBean cityDetailBean = new CityDetailBean();
        for (int i=0;i<bean.getCity_info().size();i++){
            cityDetailBean.setProvince_name(bean.getCity_info().get(i).getProv());
            cityDetailBean.setCity_name(bean.getCity_info().get(i).getCity());
            cityDetailBean.setCity_code(bean.getCity_info().get(i).getId());
            beanList.add(cityDetailBean);
        }
        convertDataSaveDb(beanList);
    }

    /**
     * 将转换的数据保存到数据库
     * @param beanList
     */
    public void convertDataSaveDb(List<CityDetailBean> beanList){
        if (beanList!=null){
            ContentValues values = new ContentValues();
            for (int i=0;i<beanList.size();i++){
                values.put("province_name",beanList.get(i).getProvince_name());
                values.put("city_name",beanList.get(i).getCity_name());
                values.put("city_code",beanList.get(i).getCity_code());
                db.insert("citylistinfo",null,values);
            }
        }
    }

    /**
     * 将数据从数据库提取出来
     * @return
     */
    public List<CityDetailBean> getProvDataFromDb(){
        List<CityDetailBean> beanList = new ArrayList<CityDetailBean>();
        Cursor cursor = db.query("citylistinfo",null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                CityDetailBean bean = new CityDetailBean();
                bean.setProvince_name(cursor.getString(cursor.getColumnIndex("province_name")));
                bean.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
                bean.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
                beanList.add(bean);
            }while (cursor.moveToNext());
        }
        if (cursor!=null){
            cursor.close();
        }
        return beanList;
    }

}
