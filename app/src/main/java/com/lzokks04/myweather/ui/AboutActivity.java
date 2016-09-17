package com.lzokks04.myweather.ui;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.view.activity.BaseActivity;

import butterknife.BindView;

/**
 * 关于界面
 * Created by Liu on 2016/8/29.
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int getLayout() {
        return R.layout.activity_about;
    }

    @Override
    protected void initialization() {
        setToolBar(toolbar, "关于");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
