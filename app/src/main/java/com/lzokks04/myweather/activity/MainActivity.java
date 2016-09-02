package com.lzokks04.myweather.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.adapter.DaliyWeatherAdapter;
import com.lzokks04.myweather.bean.CityWeatherBean;
import com.lzokks04.myweather.bean.CityWeatherConBean;
import com.lzokks04.myweather.bean.DailyWeather;
import com.lzokks04.myweather.db.CityListHelper;
import com.lzokks04.myweather.util.API;
import com.lzokks04.myweather.util.ActivityCollector;
import com.lzokks04.myweather.util.Utils;
import com.lzokks04.myweather.util.retrofit.JsonConverterFactory;
import com.lzokks04.myweather.util.retrofit.WeatherService;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.sephiroth.android.library.picasso.Picasso;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tv_lasttime)
    TextView tvLastTime;//最后更新
    @BindView(R.id.tv_city)
    TextView tvCity;//城市
    @BindView(R.id.tv_dayofweek)
    TextView tvDayOfWeek;//今天星期几
    @BindView(R.id.iv_image)
    ImageView ivImage;//天气图像
    @BindView(R.id.tv_temp)
    TextView tvTemp;//温度
    @BindView(R.id.tv_weather)
    TextView tvWeather;//天气状态
    @BindView(R.id.tv_wind)
    TextView tvWind;//风力
    @BindView(R.id.tv_hum)
    TextView tvHum;//相对湿度
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private DaliyWeatherAdapter adapter;
    private CityListHelper helper;//数据库帮助类
    private String code;//城市代码
    private ProgressDialog progress;

    private long exitTime = 0;//最后推出时间

    @Override
    protected void initialization() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvDayOfWeek.setText(Utils.getDayOfWeek());
        helper = CityListHelper.getInstance(this);
        initToolBar();//初始化Toolbar
        initDialog();//初始化progressDialog
        isFirstBoot();//判断app是否第一次启动
        getCityMessage();//获取citycode并从网络获取天气信息
    }

    /**
     * 初始化ProgressDialog
     */
    private void initDialog() {
        progress = new ProgressDialog(this);
        progress.setCancelable(false);// 设置是否可以通过点击Back键取消
        progress.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progress.setTitle("获取中");
    }

    /**
     * 初始化Toolbar
     */
    private void initToolBar() {
        mToolbar.setTitle("天气");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_18dp);
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.refresh:
                        getCityWeather(API.WEATHER, code, API.USER_ID);
                        adapter.notifyDataSetChanged();
                        break;
                    case R.id.about:
                        Intent i = new Intent(MainActivity.this, AboutActivity.class);
                        startActivity(i);
                        break;
                    case R.id.exit:
                        ActivityCollector.finishAll();
                        break;
                }
                return true;
            }

        });
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
     *
     * @param bean
     */
    private void initRecyclerView(CityWeatherBean bean, List<DailyWeather> beanList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        if (bean != null && beanList == null) {
            mRecyclerView.setAdapter(adapter = new DaliyWeatherAdapter(this, bean, null));
        } else if (bean == null && beanList != null) {
            mRecyclerView.setAdapter(adapter = new DaliyWeatherAdapter(this, null, beanList));
        }

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
    private void getCityMessage() {
        Intent intent = getIntent();
        String cityCode = intent.getStringExtra("citycode");
        code = cityCode;
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        //从选择界面跳转过来
        if (cityCode != null) {
            pref.edit().putString("citycode", cityCode).commit();
            getCityWeather(API.WEATHER, cityCode, API.USER_ID);
            //不是从选择界面跳转过来
        } else {
            //如果SharedPreferences里有城市代号+
            if (pref.getString("citycode", null) != null) {
                cityCode = pref.getString("citycode", null);
                code = cityCode;
                //如果过去1小时候进入app则自动更新天气
                long lastTime = pref.getLong("lasttime", 0);
                if (lastTime != 0 && System.currentTimeMillis() - lastTime >= 3600000) {
                    getCityWeather(API.WEATHER, cityCode, API.USER_ID);
                }
                //如果数据库中没有数据的话
                if (helper.loadDailyWeather().size() == 0) {
                    getCityWeather(API.WEATHER, cityCode, API.USER_ID);
                } else if (helper.loadDailyWeather().size() > 0) {
                    CityWeatherConBean bean = helper.loadCityWeather();
                    List<DailyWeather> beanList = helper.loadDailyWeather();
                    setText(null, bean);//设置文字
                    initRecyclerView(null, beanList);//初始化RecyclerView
                }
                //如果SharedPreferences里没有城市代号
            } else {
                Intent i = new Intent(MainActivity.this, SelectActivity.class);
                startActivity(i);
                finish();
            }
        }
    }

    /**
     * retrofit+rxjava获取json并解析
     *
     * @param baseURL
     * @param cityCode
     * @param apiKey
     */
    private void getCityWeather(String baseURL, String cityCode, String apiKey) {
        progress.show();
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
                        hideProgress();
                        saveLastTime();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        Log.e("mjj", e.getMessage());
                        Toast.makeText(MainActivity.this, "操作失败！请检查网络设置!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(CityWeatherBean cityWeatherBean) {
                        initRecyclerView(cityWeatherBean, null);//初始化RecyclerView
                        setText(cityWeatherBean, null);//设置UI文本
                        saveInfoToDb(cityWeatherBean);//保存到数据库
                    }
                });
    }

    /**
     * 保存更新后的时间到SharedPreferences
     */
    private void saveLastTime() {
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        pref.edit().putLong("lasttime", System.currentTimeMillis()).commit();
    }

    /**
     * 隐藏progressbar
     */
    private void hideProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    /**
     * 将信息保存到数据库
     *
     * @param bean
     */
    private void saveInfoToDb(final CityWeatherBean bean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //存入前先把数据库清空
                helper.clearCityWeatherTable();
                helper.clearDailyWeatherTable();
                //开始存储
                helper.saveCityWeather(bean);
                helper.saveDailyWeather(bean);
            }
        }).start();
    }

    /**
     * 设置UI文字，其中一个可以为空
     *
     * @param cityWeatherBean 此处为网络获取的,为空的话则调用第二个
     * @param bean            此处为数据库获取的，
     */
    private void setText(CityWeatherBean cityWeatherBean, CityWeatherConBean bean) {
        if (cityWeatherBean != null && bean == null) {
            tvCity.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getBasic().getCity());
            tvTemp.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getTmp() + "°");
            tvWeather.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getCond().getTxt());
            tvHum.setText("湿度:" + cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getHum() + "%");
            tvWind.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getWind().getDir()
                    + cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getWind().getSc() + "级");
            tvLastTime.setText(Utils.getLastTime(cityWeatherBean.getHeWeatherdataservice()
                    .get(0).getBasic().getUpdate().getLoc()) + "更新");
            Picasso.with(MainActivity.this).load(Utils.getWeatherIcon(cityWeatherBean.
                    getHeWeatherdataservice().get(0).getNow().getCond().getCode())).into(ivImage);
        } else if (cityWeatherBean == null && bean != null) {
            tvCity.setText(bean.getCity());
            tvTemp.setText(bean.getTemp());
            tvWeather.setText(bean.getWeather());
            tvHum.setText(bean.getHum());
            tvWind.setText(bean.getWind());
            tvLastTime.setText(bean.getTime());
            Picasso.with(MainActivity.this).load(Utils.getWeatherIcon(bean.getCode())).into(ivImage);
        }
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
     * 获取toolbar右侧菜单
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 回到app后自动更新天气
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        getCityWeather(API.WEATHER, code, API.USER_ID);
    }
}