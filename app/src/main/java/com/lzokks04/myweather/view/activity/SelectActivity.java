package com.lzokks04.myweather.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.model.bean.CityDetailBean;
import com.lzokks04.myweather.model.bean.CityListBean;
import com.lzokks04.myweather.model.db.CityListHelper;
import com.lzokks04.myweather.model.http.RetrofitHelper;
import com.lzokks04.myweather.util.Constants;
import com.lzokks04.myweather.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnItemClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 选择城市列表activity
 * Created by Liu on 2016/8/13.
 */
public class SelectActivity extends BaseActivity {

    public static final int LEVEL_PROV = 1;//省份列表状态
    public static final int LEVEL_CITY = 2;//城市列表状态

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.lv_select)
    ListView lvSelect;

    private RetrofitHelper netHelper;//网络帮助类
    private CityListHelper dbHelper;//数据库帮助类
    private ProgressDialog progress;
    private ArrayAdapter<String> adapter;
    private List<CityDetailBean> cityDetailBeanList;
    private int currentLevel;//选择的列表状态（省份/城市）
    private String cityCode;//城市代码
    private String selectProv;//选择的省份，用于显示toolbar
    private List<String> mList;//数据载体

    @Override
    protected int getLayout() {
        return R.layout.activity_select;
    }

    @Override
    protected void initialization() {
        dbHelper = CityListHelper.getInstance(this);
        netHelper = new RetrofitHelper();

        setToolBar(toolbar, "省份");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lvSelect.setAdapter(adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1,
                mList = new ArrayList<String>()));

        //初始化ProgressDialog
        initProgressDialog();
        //设置listview
        getData();
    }

    //初始化ProgressDialog
    private void initProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.setTitle("获取中");
    }

    /**
     * 根据条件获取数据
     */
    private void getData() {
        if (dbHelper.loadCityData().size() > 0) {
            cityDetailBeanList = dbHelper.loadCityData();
            mList = Utils.getProvStringGroup(cityDetailBeanList);
            adapter.clear();
            adapter.addAll(mList);
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROV;
        } else {
            getCityList("allchina", Constants.USER_ID);
        }
    }

    /**
     * 通过rxjava+retrofit获取json并解析
     *
     * @param cityType
     * @param apiKey
     */
    private void getCityList(String cityType, String apiKey) {
        progress.show();
        netHelper.fetchCityList(cityType, apiKey)
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
                        hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgress();
                        Log.e("mjj", e.getMessage());
                        Toast.makeText(SelectActivity.this, "操作失败！请检查网络设置!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(final List<CityDetailBean> beanList) {
                        adapter.clear();
                        adapter.addAll(Utils.getProvStringGroup(beanList));
                        adapter.notifyDataSetChanged();
                        cityDetailBeanList = beanList;
                        currentLevel = LEVEL_PROV;
                        //开线程，将城市列表保存到数据库
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                dbHelper.saveDataToDB(beanList);
                            }
                        }).start();
                    }
                });
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
     * 判断返回键的操作
     * 如果在city表的话直接返回到province表
     * 如果在province表的话就退出
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_CITY) {
            toolbar.setTitle("省份");
            mList = Utils.getProvStringGroup(cityDetailBeanList);
            adapter.clear();
            adapter.addAll(mList);
            adapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROV;
        } else if (currentLevel == LEVEL_PROV) {
            Intent intent = new Intent(SelectActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnItemClick(R.id.lv_select)
    public void ItemClick(int position) {
        //如果在省份列表
        if (currentLevel == LEVEL_PROV) {
            selectProv = mList.get(position);
            mList = Utils.getCityStringGroup(cityDetailBeanList, mList.get(position));
            adapter.clear();
            adapter.addAll(mList);
            adapter.notifyDataSetChanged();
            toolbar.setTitle(selectProv);
            currentLevel = LEVEL_CITY;
            //如果是在城市列表
        } else if (currentLevel == LEVEL_CITY) {
            cityCode = Utils.getCityCode(cityDetailBeanList, mList.get(position));
            Intent intent = new Intent(SelectActivity.this, MainActivity.class);
            intent.putExtra("citycode", cityCode);
            startActivity(intent);
            finish();
        }
    }

}
