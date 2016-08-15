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
     * 將城市列表下載
     */
    public void CityListDataDownSaveDb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String jsonData = NetUtil.getJsonData(API.CITYLIST_CHINA_URL + API.USER_ID);
                Gson gson = new Gson();
                CityListBean bean = gson.fromJson(jsonData, CityListBean.class);
                saveCityList(bean.getCity_info());
            }
        }).start();
    }

    /**
     * 將解析出來的城市數據存到數據庫
     *
     * @param
     */
    public void saveCityList(List<CityListBean.CityInfoBean> cityListBean) {
        if (cityListBean != null) {
            ContentValues values = new ContentValues();
            for (int i = 0; i < cityListBean.size(); i++) {
                values.put("city", cityListBean.get(i).getCity());
                values.put("cnty", cityListBean.get(i).getCnty());
                values.put("id", cityListBean.get(i).getId());
                values.put("lat", cityListBean.get(i).getLat());
                values.put("lon", cityListBean.get(i).getLon());
                values.put("prov", cityListBean.get(i).getProv());
                db.insert("Citylist", null, values);
            }
        }
    }

    /**
     * 將已有的城市數據讀取出來
     *
     * @param
     * @return List<CityListBean.CityInfoBean>
     */
    public List<CityListBean.CityInfoBean> loadCityList() {
        List<CityListBean.CityInfoBean> list = new ArrayList<CityListBean.CityInfoBean>();
        Cursor cursor = db.query("Citylist", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CityListBean.CityInfoBean cityListBean = new CityListBean.CityInfoBean();
                cityListBean.setCity(cursor.getString(cursor.getColumnIndex("city")));
                cityListBean.setCnty(cursor.getString(cursor.getColumnIndex("cnty")));
                cityListBean.setId(cursor.getString(cursor.getColumnIndex("id")));
                cityListBean.setLat(cursor.getString(cursor.getColumnIndex("lat")));
                cityListBean.setLon(cursor.getString(cursor.getColumnIndex("lon")));
                cityListBean.setProv(cursor.getString(cursor.getColumnIndex("prov")));
                list.add(cityListBean);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public List<CityDetailBean> convertToCityDetail(List<CityListBean.CityInfoBean> cityInfoBeenList){
        List<CityDetailBean> beanList = new ArrayList<CityDetailBean>();
        Cursor cursor = db.query("Citylist", null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            do {
                CityDetailBean cityDetailBean = new CityDetailBean();
                for (int i=0;i<cityInfoBeenList.size();i++){
                    if (!cityInfoBeenList.contains(cityInfoBeenList.get(i).getProv())){
                        cityDetailBean.setProvince_name(cursor.getString(cursor.getColumnIndex("prov")));
                    }
                }
                cityDetailBean.setCity_name(cursor.getColumnName(cursor.getColumnIndex("city")));
                cityDetailBean.setCity_code(cursor.getColumnName(cursor.getColumnIndex("id")));
                beanList.add(cityDetailBean);
            }while (cursor.moveToNext());
        }
        if (cursor!=null){
            cursor.close();
        }
        return beanList;
    }
}
