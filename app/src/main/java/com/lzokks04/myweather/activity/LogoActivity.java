package com.lzokks04.myweather.activity;

import android.content.Intent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.lzokks04.myweather.R;

/**
 * Created by Liu on 2016/8/13.
 */
public class LogoActivity extends BaseActivity {

    private Animation anim;

    @Override
    public void initView() {
        setContentView(R.layout.activity_logo);
    }

    @Override
    protected void initData() {
        anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(6000);
        anim.setFillAfter(true);
    }

    @Override
    public void initListener() {
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(LogoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        View view = findViewById(R.id.logo);
        view.startAnimation(anim);
    }


}
