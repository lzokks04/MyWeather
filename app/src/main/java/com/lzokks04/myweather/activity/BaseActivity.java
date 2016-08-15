package com.lzokks04.myweather.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.lzokks04.myweather.R;

/**
 * Created by Liu on 2016/8/13.
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    public abstract void initView();

    protected abstract void initData();

    public abstract void initListener();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_to_bottom);
    }
}
