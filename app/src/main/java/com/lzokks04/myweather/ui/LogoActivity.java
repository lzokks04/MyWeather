package com.lzokks04.myweather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.lzokks04.myweather.R;
import com.lzokks04.myweather.view.activity.MainActivity;

/**
 * Created by Liu on 2016/8/13.
 */
public class LogoActivity extends AppCompatActivity {

    private Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_logo);
        initData();
        initListener();
    }

    private void initData() {
        anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(3000);
        anim.setFillAfter(true);
    }


    private void initListener() {
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
