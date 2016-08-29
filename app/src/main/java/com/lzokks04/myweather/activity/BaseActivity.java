package com.lzokks04.myweather.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.util.ActivityCollector;

/**
 * Created by Liu on 2016/8/13.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        initView();
        initData();
        initListener();
    }

    public abstract void initView();

    protected abstract void initData();

    public abstract void initListener();

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_to_bottom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
