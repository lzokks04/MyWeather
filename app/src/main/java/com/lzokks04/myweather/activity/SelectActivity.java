package com.lzokks04.myweather.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzokks04.myweather.R;
import com.lzokks04.myweather.bean.CityDetailBean;
import com.lzokks04.myweather.bean.CityListBean;
import com.lzokks04.myweather.db.CityListHelper;
import com.lzokks04.myweather.util.API;
import com.lzokks04.myweather.util.NetUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private CityListHelper helper;
    private TextView tvTitle;
    private ArrayAdapter<String> adapter;
    List<CityDetailBean> cityDetailBeanList;

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
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    protected void initData() {
        currentLevel = LEVEL_PROV;

        helper = CityListHelper.getInstance(this);
        initListView();
    }


    @Override
    public void initListener() {
        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (currentLevel == LEVEL_PROV) {
            tvTitle.setText("省份");
            if (currentGetLevel == GET_NET){
                cityList = getCityStringGroup(cityDetailBeanList, provList.get(i));
                selectProv = provList.get(i);
            }else if (currentGetLevel == GET_DB){
                cityList = helper.loadCityData(provList.get(i));
                selectProv = provList.get(i);
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cityList);
            mListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            tvTitle.setText(selectProv);
            currentLevel = LEVEL_CITY;
        } else if (currentLevel == LEVEL_CITY){
            if (currentGetLevel == GET_NET){
                cityCode = getCityCode(cityDetailBeanList, cityList.get(i));
            }else if (currentGetLevel == GET_DB){
                cityCode = helper.loadCityCode(cityList.get(i));
            }
            Intent intent = new Intent(SelectActivity.this, MainActivity.class);
            intent.putExtra("citycode", cityCode);
            startActivity(intent);
            overridePendingTransition(0, R.anim.slide_out_to_bottom);
        }

    }

    /**
     * 初始化ListView所需要的adapter,list
     */
    private void initListView() {
//        new MyAsyncTask().execute(API.CITYLIST_CHINA_URL + API.USER_ID);
        if (helper.loadProvData().size() > 0) {
            provList = helper.loadProvData();
//            cityList = new ArrayList<String>();
            adapter = new ArrayAdapter<String>(SelectActivity.this, android.R.layout.simple_list_item_1,
                    provList);
            mListView.setAdapter(adapter);
            currentLevel = LEVEL_PROV;
            currentGetLevel = GET_DB;
        } else {
            new MyAsyncTask().execute(API.CITYLIST_CHINA_URL + API.USER_ID);
            currentGetLevel = GET_NET;
        }
    }

    /**
     * 提取List<CityDetailBean>中的省份数据并去重
     *
     * @param list
     * @return
     */
    private List<String> getProvStringGroup(List<CityDetailBean> list) {
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
    private List<String> getCityStringGroup(List<CityDetailBean> list, String prov) {
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
    private String getCityCode(List<CityDetailBean> list, String city) {
        String cityCode = null;
        for (int i = 0; i < list.size(); i++) {
            if (city.equals(list.get(i).getCity_name())) {
                cityCode = list.get(i).getCity_code();
            }
        }
        return cityCode;
    }

    /**
     * AsyncTask异步类，将json获取解析并保存到数据库且初始化beanList
     */
    private class MyAsyncTask extends AsyncTask<String, Void, List<CityDetailBean>> {
        @Override
        protected List<CityDetailBean> doInBackground(String... strings) {
            return getTempData(strings[0]);
        }

        @Override
        protected void onPostExecute(final List<CityDetailBean> beanList) {
            provList = getProvStringGroup(beanList);
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

        /**
         * asynctask操作，访问网络获取城市列表，解析
         * 然后返回List<CityDetailBean>
         *
         * @param url
         * @return
         */
        private List<CityDetailBean> getTempData(String url) {
            String jsonData = NetUtil.getJsonData(url);
            Gson gson = new Gson();
            CityListBean bean = gson.fromJson(jsonData, CityListBean.class);
            List<CityDetailBean> cityDetailBeanList = new ArrayList<CityDetailBean>();
            for (int i = 0; i < bean.getCity_info().size(); i++) {
                CityDetailBean cityDetailBean = new CityDetailBean();
                cityDetailBean.setProvince_name(bean.getCity_info().get(i).getProv());
                cityDetailBean.setCity_name(bean.getCity_info().get(i).getCity());
                cityDetailBean.setCity_code(bean.getCity_info().get(i).getId());
                cityDetailBeanList.add(cityDetailBean);
            }
            return cityDetailBeanList;
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
            currentLevel = LEVEL_PROV;
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, provList);
            mListView.setAdapter(adapter);
            tvTitle.setText("省份");
            adapter.notifyDataSetChanged();
        } else if (currentLevel == LEVEL_PROV) {
            Intent intent = new Intent(SelectActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
