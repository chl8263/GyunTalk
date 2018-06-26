package com.example.gyun_home.gyuntalk.AnimEffect;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.gyun_home.gyuntalk.R;

public class CustomProgressDialog extends ProgressDialog {

    private Context context;
    private ImageView imageView;
    private Animation animation;

    public CustomProgressDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);

        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        init();

    }

    private void init(){
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        imageView = (ImageView) findViewById(R.id.progressActivity_imageView);
        animation = AnimationUtils.loadAnimation(context,R.anim.loding);
        imageView.setAnimation(animation);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

}
