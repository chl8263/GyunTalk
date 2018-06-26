package com.example.gyun_home.gyuntalk.Login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.gyun_home.gyuntalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LoginActivity extends AppCompatActivity{

    private EditText id_Et;
    private EditText password_Et;

    private Button loginBtn;
    private Button signupBtn;

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;   //로그인이 됐는지 안됐는지 체크하는 리스너 , 다음으로 넘길수 있음
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        //String splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        String splash_background = "#aabbcc";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }

        id_Et = (EditText)findViewById(R.id.loginactivity_edittext_id);
        password_Et = (EditText)findViewById(R.id.loginactivity_edittext_password);

        loginBtn = (Button)findViewById(R.id.loginactivity_button_login);
        signupBtn = (Button)findViewById(R.id.loginactivity_button_signup);
        loginBtn.setBackgroundColor(Color.parseColor(splash_background));
        signupBtn.setBackgroundColor(Color.parseColor(splash_background));

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
    }
    private void loginEvent(){
        firebaseAuth.signInWithEmailAndPassword(id_Et.getText().toString(),password_Et.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //로그인이 완료되었는지만 확인해주는 리스너
            }
        });
    }
}
