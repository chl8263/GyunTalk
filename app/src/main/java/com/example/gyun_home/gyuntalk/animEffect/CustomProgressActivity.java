package com.example.gyun_home.gyuntalk.animEffect;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.gyun_home.gyuntalk.R;

public class CustomProgressActivity extends AppCompatActivity{

    private ImageView imageView;
    private Animation animation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

        init();

    }

    private void init(){
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        imageView = (ImageView) findViewById(R.id.progressActivity_imageView);
        animation = AnimationUtils.loadAnimation(this,R.anim.loding);
        imageView.setAnimation(animation);
    }
}
