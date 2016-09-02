package com.lzokks04.myweather.util.retrofit;

import com.lzokks04.myweather.bean.CityListBean;
import com.lzokks04.myweather.bean.CityWeatherBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**声明一个方法来定义请求的相关内容
 * Created by Liu on 2016/9/3.
 */
public interface WeatherService {
    @GET("x3/citylist")
    Observable<CityListBean> getCityList(@Query("search") String cityType, @Query("key") String apiKey);

    @GET("x3/weather")
    Observable<CityWeatherBean> getWeather(@Query("cityid") String cityCode, @Query("key") String apiKey);
}
