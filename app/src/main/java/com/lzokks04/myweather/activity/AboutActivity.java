package com.lzokks04.myweather.activity;

import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lzokks04.myweather.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 关于界面
 * Created by Liu on 2016/8/29.
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void initialization() {
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initToolbar();
    }

    private void initToolbar() {
        toolbar.setTitle("关于");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
