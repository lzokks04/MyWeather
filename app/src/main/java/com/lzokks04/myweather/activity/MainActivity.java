package com.lzokks04.myweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.adapter.DaliyWeatherAdapter;
import com.lzokks04.myweather.bean.CityWeatherBean;
import com.lzokks04.myweather.util.API;
import com.lzokks04.myweather.util.ActivityCollector;
import com.lzokks04.myweather.util.DividerGridItemDecoration;
import com.lzokks04.myweather.util.JsonConverterFactory;
import com.lzokks04.myweather.util.Utils;

import it.sephiroth.android.library.picasso.Picasso;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ImageView ivImage;//天气图像
    private TextView tvTemp;//温度
    private TextView tvWeather;//天气状态
    private TextView tvHum;//相对湿度
    private TextView tvWind;//风力
    private TextView tvDayOfWeek;//今天星期几
    private TextView tvCity;//城市
    private RecyclerView mRecyclerView;
    private DaliyWeatherAdapter adapter;


    private long exitTime = 0;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        tvCity = (TextView) findViewById(R.id.tv_city);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        tvTemp = (TextView) findViewById(R.id.tv_temp);
        tvWeather = (TextView) findViewById(R.id.tv_weather);
        tvHum = (TextView) findViewById(R.id.tv_hum);
        tvWind = (TextView) findViewById(R.id.tv_wind);
        tvDayOfWeek = (TextView) findViewById(R.id.tv_dayofweek);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    @Override
    protected void initData() {
        initToolBar();//初始化Toolbar
        isFirstBoot();//判断app是否第一次启动
        getCityCode();//获取citycode并从网络获取天气信息(本地保存待加入)
    }

    /**
     * 初始化Toolbar
     */
    private void initToolBar() {
        mToolbar.setTitle("天气");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_18dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 初始化RecyclerView
     * @param bean
     */
    private void initRecyclerView(CityWeatherBean bean) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        mRecyclerView.setAdapter(adapter = new DaliyWeatherAdapter(this, bean));
    }

    /**
     * 判断app是否第一次启动
     */
    private void isFirstBoot() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        boolean isFirst = pref.getBoolean("first", true);
        if (isFirst == true) {
            pref.edit().putBoolean("first", false).commit();
            Intent intent = new Intent(MainActivity.this, SelectActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 获取citycode并从网络获取天气信息
     */
    private void getCityCode() {
        Intent intent = getIntent();
        String cityCode = intent.getStringExtra("citycode");
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        if (cityCode != null) {
            pref.edit().putString("citycode", cityCode).commit();
            getCityWeather(API.WEATHER, cityCode, API.USER_ID);
        } else {
            if (pref.getString("citycode", null) != null) {
                cityCode = pref.getString("citycode", null);
                getCityWeather(API.WEATHER, cityCode, API.USER_ID);
            }
        }
    }

    @Override
    public void initListener() {

    }

    /**
     * retrofit+rxjava获取json并解析
     *
     * @param baseURL
     * @param cityCode
     * @param apiKey
     */
    private void getCityWeather(String baseURL, String cityCode, String apiKey) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                //自定义的JsonConverterFactory,去除json原始数据的非法数据
                .addConverterFactory(JsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        WeatherService service = retrofit.create(WeatherService.class);

        service.getWeather(cityCode, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CityWeatherBean>() {
                    @Override
                    public void onCompleted() {
//                        Toast.makeText(MainActivity.this, "操作完成！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("mjj", e.getMessage());
                        Toast.makeText(MainActivity.this, "操作失败！请检查网络设置!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(CityWeatherBean cityWeatherBean) {
                        initRecyclerView(cityWeatherBean);//初始化RecyclerView
                        setText(cityWeatherBean);
                    }
                });
    }

    /**
     * 设置文字
     *
     * @param cityWeatherBean
     */
    private void setText(CityWeatherBean cityWeatherBean) {
        tvCity.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getBasic().getCity());
        tvDayOfWeek.setText(Utils.getDayOfWeek());
        tvTemp.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getTmp()+"°");
        tvWeather.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getCond().getTxt());
        tvHum.setText("湿度:" + cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getHum() + "%");
        tvWind.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getWind().getDir()
                + cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getWind().getSc() + "级");

        Picasso.with(MainActivity.this).load(API.WEATHER_ICON +
                cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getCond().getCode()
                + API.ICON_SUFFIX).into(ivImage);
    }

    /**
     * 再按一次退出的实现
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                ActivityCollector.finishAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * retrofit拦截器
     */
    private interface WeatherService {
        @GET("x3/weather")
        Observable<CityWeatherBean> getWeather(@Query("cityid") String cityCode, @Query("key") String apiKey);
    }
}


