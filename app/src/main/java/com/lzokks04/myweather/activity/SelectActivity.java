package com.lzokks04.myweather.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.bean.CityDetailBean;
import com.lzokks04.myweather.bean.CityListBean;
import com.lzokks04.myweather.db.CityListHelper;
import com.lzokks04.myweather.util.API;
import com.lzokks04.myweather.util.Utils;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 选择城市列表activity
 * Created by Liu on 2016/8/13.
 */
public class SelectActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final int LEVEL_PROV = 1;
    public static final int LEVEL_CITY = 2;
    public static final int GET_NET = 3;
    public static final int GET_DB = 4;

    private ListView mListView;
    private Toolbar mToolbar;
    private CityListHelper helper;
    private ArrayAdapter<String> adapter;
    private List<CityDetailBean> cityDetailBeanList;

    private int currentLevel;
    private int currentGetLevel;
    private List<String> provList;
    private List<String> cityList;
    private String cityCode;
    private String selectProv;


    @Override
    public void initView() {
        setContentView(R.layout.activity_select);
        mListView = (ListView) findViewById(R.id.lv_select);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void initData() {
        helper = CityListHelper.getInstance(this);
        //设置listview
        initListView();
        //设置toolbar
        setToolbar();
    }


    @Override
    public void initListener() {
        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        itemClick(i);
    }

    private void itemClick(int i) {
        //如果在省份列表
        if (currentLevel == LEVEL_PROV) {
            //如果是从网络获取
            if (currentGetLevel == GET_NET) {
                cityList = Utils.getCityStringGroup(cityDetailBeanList, provList.get(i));
                selectProv = provList.get(i);
                //如果在数据库获取
            } else if (currentGetLevel == GET_DB) {
                cityList = helper.loadCityData(provList.get(i));
                selectProv = provList.get(i);
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cityList);
            mListView.setAdapter(adapter);
            mToolbar.setTitle(selectProv);
            currentLevel = LEVEL_CITY;
            //如果是在城市列表
        } else if (currentLevel == LEVEL_CITY) {
            //如果是网络获取
            if (currentGetLevel == GET_NET) {
                cityCode = Utils.getCityCode(cityDetailBeanList, cityList.get(i));
                //如果是城市获取
            } else if (currentGetLevel == GET_DB) {
                cityCode = helper.loadCityCode(cityList.get(i));
            }
            Intent intent = new Intent(SelectActivity.this, MainActivity.class);
            intent.putExtra("citycode", cityCode);
            startActivity(intent);
        }
    }

    /**
     * 初始化Toolbar
     */
    private void setToolbar() {
        mToolbar.setTitle("省份");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backSelect();
            }
        });
    }

    /**
     * 初始化ListView所需要的adapter,list
     * 如果数据库有数据则读取数据库数据
     * 反之从网络获得且保存到数据库
     */
    private void initListView() {
        if (helper.loadProvData().size() > 0) {
            provList = helper.loadProvData();
            adapter = new ArrayAdapter<String>(SelectActivity.this, android.R.layout.simple_list_item_1,
                    provList);
            mListView.setAdapter(adapter);
            currentLevel = LEVEL_PROV;
            currentGetLevel = GET_DB;
        } else {
            getCityList(API.WEATHER,"allchina", API.USER_ID);
            currentGetLevel = GET_NET;
        }
    }

    /**
     * 通过rxjava+retrofit获取json并解析
     * @param baseUrl
     * @param cityType
     * @param apiKey
     */
    private void getCityList(String baseUrl,String cityType,String apiKey){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        CityListService service = retrofit.create(CityListService.class);

        service.getCityList(cityType,apiKey)
                .map(new Func1<CityListBean, List<CityDetailBean>>() {
                    @Override
                    public List<CityDetailBean> call(CityListBean bean) {
                        return Utils.getCityDetailBean(bean);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<CityDetailBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("mjj", e.getMessage());
                        Toast.makeText(SelectActivity.this, "操作失败！请检查网络设置!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(final List<CityDetailBean> beanList) {
                        provList = Utils.getProvStringGroup(beanList);
                        adapter = new ArrayAdapter<String>(SelectActivity.this, android.R.layout.simple_list_item_1,
                                provList);
                        mListView.setAdapter(adapter);
                        currentLevel = LEVEL_PROV;
                        cityDetailBeanList = beanList;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                helper.saveDataToDB(beanList);
                            }
                        }).start();
                    }
                });
    }

    /**
     * 判断返回键的操作
     */
    @Override
    public void onBackPressed() {
        backSelect();
    }

    /**
     * 判断返回键的操作
     * 如果在city表的话直接返回到province表
     * 如果在province表的话就退出
     */
    private void backSelect() {
        if (currentLevel == LEVEL_CITY) {
            currentLevel = LEVEL_PROV;
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, provList);
            mListView.setAdapter(adapter);
            mToolbar.setTitle("省份");
            adapter.notifyDataSetChanged();
        } else if (currentLevel == LEVEL_PROV) {
            Intent intent = new Intent(SelectActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * retrofit拦截器
     */
    private interface CityListService{
        @GET("x3/citylist")
        Observable<CityListBean>getCityList(@Query("search")String cityType,@Query("key")String apiKey);
    }
}
