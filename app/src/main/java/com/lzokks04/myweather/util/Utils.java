package com.lzokks04.myweather.util;

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


}
