package com.lzokks04.myweather.util;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.bean.CityDetailBean;
import com.lzokks04.myweather.bean.CityListBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Liu on 2016/8/27.
 */
public class Utils {

    /**
     * 由于城市天气json中字段含有违规字符，所以去除（空格，点，数字）
     *
     * @param str
     * @return
     */
    public static String deleteErrData(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            sb.append(str.charAt(i));
        }
        sb.deleteCharAt(11);
        sb.deleteCharAt(15);
        sb.delete(22, 26);
        return sb.toString();
    }

    /**
     * 获取今天是星期几
     *
     * @return
     */
    public static String getDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
        return null;
    }

    /**
     * 将类似2006-12-18的转换为12月18日的字符串
     *
     * @param str
     * @return
     */
    public static String getMonthDay(String str) {
        StringBuffer sb = new StringBuffer();
        if (str.charAt(5) == '1') {
            sb.append(str.charAt(5));
            sb.append(str.charAt(6) + "-" + str.charAt(8) + str.charAt(9));
        } else {
            sb.append("0" + str.charAt(6) + "-" + str.charAt(8) + str.charAt(9));
        }
        return sb.toString();
    }

    /**
     * 返回省份名
     *
     * @param list
     * @return
     */
    public static List<String> getProvStringGroup(List<CityDetailBean> list) {
        Set<String> set = new HashSet<>();
        List<String> tempList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            set.add(list.get(i).getProvince_name());
        }
        for (String str : set) {
            tempList.add(str);
        }
        return tempList;
    }

    /**
     * 传入省份，获取所在城市列表
     *
     * @param prov
     * @return
     */
    public static List<String> getCityStringGroup(List<CityDetailBean> list, String prov) {
        List<String> tempList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            if (prov.equals(list.get(i).getProvince_name())) {
                tempList.add(list.get(i).getCity_name());
            }
        }
        return tempList;
    }

    /**
     * 传入城市，获得城市代码
     *
     * @param list
     * @param city
     * @return
     */
    public static String getCityCode(List<CityDetailBean> list, String city) {
        String cityCode = null;
        for (int i = 0; i < list.size(); i++) {
            if (city.equals(list.get(i).getCity_name())) {
                cityCode = list.get(i).getCity_code();
            }
        }
        return cityCode;
    }

    /**
     * 将CityListBean转换成List<CityDetailBean>
     *
     * @param bean
     * @return
     */
    public static List<CityDetailBean> getCityDetailBean(CityListBean bean) {
        List<CityDetailBean> beanList = new ArrayList<CityDetailBean>();
        for (int i = 0; i < bean.getCity_info().size(); i++) {
            CityDetailBean cityBean = new CityDetailBean();
            cityBean.setCity_name(bean.getCity_info().get(i).getCity());
            cityBean.setProvince_name(bean.getCity_info().get(i).getProv());
            cityBean.setCity_code(bean.getCity_info().get(i).getId());
            beanList.add(cityBean);
        }
        return beanList;
    }

    /**
     * 根据天气代码返回天气图片的int
     * @param code
     * @return
     */
    public static int getWeatherIcon(String code){
        switch (code){
            case "100":
                return R.drawable.biz_plugin_weather_qing;
            case "101":
                return R.drawable.biz_plugin_weather_duoyun;
            case "102":
                return R.drawable.biz_plugin_weather_duoyun;
            case "103":
                return R.drawable.biz_plugin_weather_yin;
            case "104":
                return R.drawable.biz_plugin_weather_yin;
            case "200":
                return R.drawable.biz_plugin_weather_wu;
            case "201":
                return R.drawable.biz_plugin_weather_wu;
            case "202":
                return R.drawable.biz_plugin_weather_wu;
            case "203":
                return R.drawable.biz_plugin_weather_wu;
            case "204":
                return R.drawable.biz_plugin_weather_wu;
            case "205":
                return R.drawable.biz_plugin_weather_wu;
            case "206":
                return R.drawable.biz_plugin_weather_wu;
            case "207":
                return R.drawable.biz_plugin_weather_wu;
            case "208":
                return R.drawable.biz_plugin_weather_wu;
            case "209":
                return R.drawable.biz_plugin_weather_wu;
            case "210":
                return R.drawable.biz_plugin_weather_wu;
            case "211":
                return R.drawable.biz_plugin_weather_wu;
            case "212":
                return R.drawable.biz_plugin_weather_wu;
            case "213":
                return R.drawable.biz_plugin_weather_wu;
            case "300":
                return R.drawable.biz_plugin_weather_zhenyu;
            case "301":
                return R.drawable.biz_plugin_weather_zhenyu;
            case "302":
                return R.drawable.biz_plugin_weather_leizhenyu;
            case "303":
                return R.drawable.biz_plugin_weather_leizhenyu;
            case "304":
                return R.drawable.biz_plugin_weather_leizhenyubingbao;
            case "305":
                return R.drawable.biz_plugin_weather_xiaoyu;
            case "306":
                return R.drawable.biz_plugin_weather_zhongyu;
            case "307":
                return R.drawable.biz_plugin_weather_dayu;
            case "308":
                return R.drawable.biz_plugin_weather_dayu;
            case "309":
                return R.drawable.biz_plugin_weather_xiaoyu;
            case "310":
                return R.drawable.biz_plugin_weather_baoyu;
            case "311":
                return R.drawable.biz_plugin_weather_dabaoyu;
            case "312":
                return R.drawable.biz_plugin_weather_tedabaoyu;
            case "313":
                return R.drawable.biz_plugin_weather_dayu;
            case "400":
                return R.drawable.biz_plugin_weather_xiaoxue;
            case "401":
                return R.drawable.biz_plugin_weather_zhongxue;
            case "402":
                return R.drawable.biz_plugin_weather_daxue;
            case "403":
                return R.drawable.biz_plugin_weather_baoxue;
            case "404":
                return R.drawable.biz_plugin_weather_yujiaxue;
            case "405":
                return R.drawable.biz_plugin_weather_yujiaxue;
            case "406":
                return R.drawable.biz_plugin_weather_yujiaxue;
            case "407":
                return R.drawable.biz_plugin_weather_yujiaxue;
            case "500":
                return R.drawable.biz_plugin_weather_wu;
            case "501":
                return R.drawable.biz_plugin_weather_wu;
            case "502":
                return R.drawable.biz_plugin_weather_wu;
            case "503":
                return R.drawable.biz_plugin_weather_wu;
            case "504":
                return R.drawable.biz_plugin_weather_wu;
            case "505":
                return R.drawable.biz_plugin_weather_wu;
            case "506":
                return R.drawable.biz_plugin_weather_wu;
            case "507":
                return R.drawable.biz_plugin_weather_shachenbao;
            case "508":
                return R.drawable.biz_plugin_weather_shachenbao;
            case "900":
                return R.drawable.biz_plugin_weather_duoyun;
            case "901":
                return R.drawable.biz_plugin_weather_duoyun;
            case "999":
                return R.drawable.ic_image_loadfail;
        }
        return 0;
    }

    /**
     * 去掉最后更新时间的日期
     * @param str
     * @return
     */
    public static String getLastTime(String str){
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<str.length();i++){
            sb.append(str.charAt(i));
        }
        sb.delete(0, 11);
        return sb.toString();
    }

}
