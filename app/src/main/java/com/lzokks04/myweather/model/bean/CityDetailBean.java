package com.lzokks04.myweather.model.bean;

/**转换后的城市列表实体类
 * Created by Liu on 2016/8/16.
 */
public class CityDetailBean {
    private String province_name;
    private String city_name;
    private String city_code;

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }
}
