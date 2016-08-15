package com.lzokks04.myweather.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.adapter.CityListSelectAdapter;
import com.lzokks04.myweather.bean.CityListBean;
import com.lzokks04.myweather.db.CityListHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选择城市列表activity
 * Created by Liu on 2016/8/13.
 */
public class SelectActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;
    private AutoCompleteTextView mAutoCompleteTextView;
    private CityListHelper helper;
    private CityListSelectAdapter adapter;
    private List<CityListBean.CityInfoBean> beanList;
    private Map<String, String> map;
    private ArrayAdapter<String> arrayAdapter;
    private String resultCityCode;

    @Override
    public void initView() {
        setContentView(R.layout.activity_select);
        mListView = (ListView) findViewById(R.id.lv_select);
        mAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.actv_in);
    }

    @Override
    protected void initData() {
        helper = CityListHelper.getInstance(this);
        initListView();
        initAutoCompleteTextView();
    }


    @Override
    public void initListener() {
        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String cityCode = map.get(beanList.get(i).getCity());
        Intent intent = new Intent(SelectActivity.this, MainActivity.class);
        intent.putExtra("citycode", cityCode);
        startActivity(intent);
        overridePendingTransition(0, R.anim.slide_out_to_bottom);
        finish();
    }

    /**
     * 初始化ListView所需要的adapter,list
     */
    private void initListView() {
        beanList = helper.loadCityList();
        map = searchRes(beanList);
        adapter = new CityListSelectAdapter(SelectActivity.this, beanList);
        mListView.setAdapter(adapter);
    }

    /**
     * 初始化AutoCompleteTextView所需要的Adapter
     */
    private void initAutoCompleteTextView() {
        String[] tempArr = getCityStringGroup(beanList, false);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tempArr);
        mAutoCompleteTextView.setAdapter(arrayAdapter);
    }

    /**
     * 把List中的city或prov数据转换为String数组
     *
     * @param bean      CityInfoBean类
     * @param isAllData true为返回所有数据(city+prov) false只返回city数据
     * @return 返回String数组
     */
    private String[] getCityStringGroup(List<CityListBean.CityInfoBean> bean, boolean isAllData) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bean.size(); i++) {
            if (isAllData) {
                sb.append(bean.get(i).getProv());
                removeRepeat(sb);
                sb.append(bean.get(i).getCity());
            } else {
                sb.append(bean.get(i).getCity());
            }
        }
        return new String[]{sb.toString()};
    }

    /**
     * 去除Stringbuffer中重复的数据
     *
     * @param sb
     * @return
     */
    private StringBuffer removeRepeat(StringBuffer sb) {
        String[] str = new String[]{sb.toString()};
        StringBuffer sb2 = new StringBuffer();
        List<String> list = new ArrayList<String>();
        String temp;
        for (int i = 0; i < str.length; i++) {
            if (!list.contains(str[i])) {
                list.add(str[i]);
            }
        }
        for (int j = 0; j < list.size(); j++) {
            sb2.append(list.get(j));
        }
        return sb2;
    }

    /**
     * List<CityListBean.CityInfoBean>将数据转换成map,提取city中的名称和city对应的id
     *
     * @param bean
     * @return
     */
    private Map<String, String> searchRes(List<CityListBean.CityInfoBean> bean) {
        Map<String, String> map = new HashMap<>();
        for (CityListBean.CityInfoBean c : bean) {
            map.put(c.getCity(), c.getId());
        }
        return map;
    }

    private List<CityListBean.CityInfoBean> matcheSearchRes(List<CityListBean.CityInfoBean> list) {
        String str = mAutoCompleteTextView.getText().toString();
        String[] strCityGroup = getCityStringGroup(list, true);
        if (isSuccessMatch(str, strCityGroup)) {
            resultCityCode = map.get(str);
        } else {
            Toast.makeText(this, "然而并没有", Toast.LENGTH_SHORT).show();
            mAutoCompleteTextView.setText("");
        }
        return list;
    }

    /**
     * 匹配是否含有这个数
     *
     * @param str      要匹配的字符
     * @param strGroup 字符串
     * @return 布尔值
     */
    private boolean isSuccessMatch(String str, String[] strGroup) {
        for (int i = 0; i < strGroup.length; i++) {
            if (str == strGroup[i]) {
                return true;
            }
        }
        return false;
    }
}
