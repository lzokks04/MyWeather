package com.lzokks04.myweather.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzokks04.myweather.R;
import com.lzokks04.myweather.bean.CityWeatherBean;
import com.lzokks04.myweather.util.API;
import com.lzokks04.myweather.util.ActivityCollector;
import com.lzokks04.myweather.util.NetUtil;
import com.lzokks04.myweather.util.NetUtilBitmapCallBack;

public class MainActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button btnSwitch;
    private Button btnRefresh;
    private TextView tvTitle;
    private ImageView ivImage;//天气图像
    private TextView tvTemp;//温度
    private TextView tvWeather;//天气状态
    private TextView tvUptime;//最后更新时间
    private TextView tvHum;//相对湿度
    private TextView tvWind;//风力
    private ListView lvDetail;
    private String changeCityCode;

    private long exitTime = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap) msg.obj;
            ivImage.setImageBitmap(bitmap);
        }
    };

    private static final String URL = API.CITY_RESPONSE + "CN101020100" + API.USER_ID;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
        btnSwitch = (Button) findViewById(R.id.btn_switch);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivImage = (ImageView) findViewById(R.id.iv_image);
        tvTemp = (TextView) findViewById(R.id.tv_temp);
        tvWeather = (TextView) findViewById(R.id.tv_weather);
        tvUptime = (TextView) findViewById(R.id.tv_uptime);
        tvHum = (TextView) findViewById(R.id.tv_hum);
        tvWind = (TextView) findViewById(R.id.tv_wind);
        lvDetail = (ListView) findViewById(R.id.lv_detail);
    }

    @Override
    protected void initData() {
        isFirstBoot();
        getCityCode();
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
            overridePendingTransition(0, R.anim.slide_out_to_bottom);
        }
    }

    /**
     * 判断是否从选择界面过来并修改SharedPreferences
     */
    private void getCityCode() {
        Intent intent = getIntent();
        String cityCode = intent.getStringExtra("citycode");
        SharedPreferences pref = getPreferences(MODE_PRIVATE);
        if (cityCode != null) {
            pref.edit().putString("citycode", cityCode).commit();
            changeCityCode = cityCode;
            new MyAsyncTask().execute(API.CITY_RESPONSE + cityCode + API.USER_ID);
        } else {
            if (pref.getString("citycode", null) != null) {
                cityCode = pref.getString("citycode", null);
                changeCityCode = cityCode;
                new MyAsyncTask().execute(API.CITY_RESPONSE + cityCode + API.USER_ID);
            }
        }
    }

    @Override
    public void initListener() {
        btnSwitch.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_switch: {
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(0, R.anim.slide_out_to_bottom);
                break;
            }
            case R.id.btn_refresh: {
                new MyAsyncTask().execute(API.CITY_RESPONSE + changeCityCode + API.USER_ID);
                break;
            }
        }
    }


    /**
     * 获得json数据，并解析成CityWeatherBean对象
     *
     * @param url
     * @return
     */
    private CityWeatherBean getJSONData(String url) {
        String jsonData = NetUtil.getJsonData(url);
        CityWeatherBean bean = GsonFormat(jsonData);
        return bean;
    }

    /**
     * 由于json中字段含有违规字符，所以去除（空格，点，数字）
     *
     * @param str
     * @return
     */
    private String deleteErrData(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            sb.append(str.charAt(i));
        }
        sb.deleteCharAt(11);
        sb.deleteCharAt(15);
        sb.delete(22, 26);
        return sb.toString();
    }

    /**
     * 获取到的json数据解析成bean
     *
     * @param str
     * @return
     */
    private CityWeatherBean GsonFormat(String str) {
        String res = deleteErrData(str);
        Gson gson = new Gson();
        CityWeatherBean bean = gson.fromJson(res, CityWeatherBean.class);
        return bean;
    }

    /**
     * asynctask异步类，传入URL并获得bean类并设置settext
     */
    private class MyAsyncTask extends AsyncTask<String, Void, CityWeatherBean> {

        @Override
        protected CityWeatherBean doInBackground(String... strings) {
            return getJSONData(strings[0]);
        }

        @Override
        protected void onPostExecute(CityWeatherBean cityWeatherBean) {
            super.onPostExecute(cityWeatherBean);
            tvTitle.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getBasic().getCity());
            tvTemp.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getTmp() + "°");
            tvWeather.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getCond().getTxt());
            tvUptime.setText(getTime(cityWeatherBean.getHeWeatherdataservice().get(0)
                    .getBasic().getUpdate().getLoc() + "更新"));
            tvHum.setText("湿度:" + cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getHum() + "%");
            tvWind.setText(cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getWind().getDir() + ":"
                    + cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getWind().getSc() + "级");

            NetUtil.getBitmap(API.WEATHER_ICON +
                    cityWeatherBean.getHeWeatherdataservice().get(0).getNow().getCond().getCode()
                    + API.ICON_SUFFIX, new NetUtilBitmapCallBack() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    sendMessageForHandler(bitmap);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("onFailure: ", "然而并没有");
                }
            });

        }
    }

    /**
     * 把更新时间前的日期去掉
     * @param str
     * @return
     */
    private String getTime(String str){
        StringBuffer sb = new StringBuffer(str);
        sb.delete(0, 11);
        return sb.toString();
    }

    /***
     * 将bitmap发送给handler并更新UI
     *
     * @param bitmap
     */
    private void sendMessageForHandler(Bitmap bitmap) {
        Message msg = Message.obtain();
        msg.obj = bitmap;
        handler.sendMessage(msg);
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
}
