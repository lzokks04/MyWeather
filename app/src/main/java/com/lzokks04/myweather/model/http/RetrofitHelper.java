package com.lzokks04.myweather.model.http;

import com.lzokks04.myweather.model.bean.CityListBean;
import com.lzokks04.myweather.model.bean.CityWeatherBean;
import com.lzokks04.myweather.util.Constants;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Created by Liu on 2016/9/17.
 */
public class RetrofitHelper {

    private static WeatherService weatherList = null;
    private static WeatherService weatherInfo = null;

    public RetrofitHelper() {
        weatherList = getWeatherList();
        weatherInfo = getWeatherInfo();
    }

    private static WeatherService getWeatherList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.WEATHER)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(WeatherService.class);
    }

    private static WeatherService getWeatherInfo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.WEATHER)
                .addConverterFactory(JsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        WeatherService service = retrofit.create(WeatherService.class);
        return service;
    }

    public Observable<CityListBean> fetchCityList(String type, String key) {
        return weatherList.getCityList(type, key);
    }

    public Observable<CityWeatherBean> fetchCityWeather(String cityCode, String key) {
        return weatherInfo.getWeather(cityCode, key);
    }
}
