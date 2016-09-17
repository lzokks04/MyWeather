package com.lzokks04.myweather.model.http;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.lzokks04.myweather.model.bean.CityWeatherBean;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 自定义retrofit转换器，先把json的错误数据去除然后再解析成javabean
 * Created by Liu on 2016/8/28.
 */
public class JsonConverterFactory extends Converter.Factory {

    public static JsonConverterFactory create() {
        return create(new Gson());
    }

    public static JsonConverterFactory create(Gson gson) {
        return new JsonConverterFactory(gson);
    }

    private final Gson gson;

    private JsonConverterFactory(Gson gson) {
        if (gson == null)
            throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    /**
     * 返回响应ResponseBody
     *
     * @param type
     * @param annotations
     * @param retrofit
     * @return
     */
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new JsonRequestBodyConverter<>(gson, adapter);
    }

    /**
     * 自定义响应ResponseBody
     *
     * @param <T>
     */
    public class JsonRequestBodyConverter<T> implements Converter<ResponseBody, T> {

        private Gson mGson;
        private TypeAdapter<T> mAdapter;

        public JsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.mGson = gson;
            this.mAdapter = adapter;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            //清除错误的字段
            String str = deleteErrData(value.string());
            CityWeatherBean bean = gson.fromJson(str, CityWeatherBean.class);
            return (T) bean;
        }

        /**
         * 由于城市天气json中字段含有违规字符，所以去除（空格，点，数字）
         *
         * @param str
         * @return
         */
        public String deleteErrData(String str) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.length(); i++) {
                sb.append(str.charAt(i));
            }
            sb.deleteCharAt(11);
            sb.deleteCharAt(15);
            sb.delete(22, 26);
            return sb.toString();
        }
    }
}
