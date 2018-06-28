package com.example.gyun_home.gyuntalk.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gyun_home.gyuntalk.animEffect.CustomProgressDialog;
import com.example.gyun_home.gyuntalk.main.MainActivity;
import com.example.gyun_home.gyuntalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText id_Et;
    private EditText password_Et;

    private Button loginBtn;
    private Button signupBtn;

    private FirebaseRemoteConfig firebaseRemoteConfig;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;   //로그인이 됐는지 안됐는지 체크하는 리스너 , 다음으로 넘길수 있음

    private String splash_background;

    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setStatusbar();
        init();
        setFirebase();

    }

    private void setFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut(); //로그인 리소스가 남아있을 경우 로그아웃을 해준다

        //로그인이 됐거나 했을경우(change)
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //로그인
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    customProgressDialog.dismiss();
                    startActivity(intent);
                    finish();
                } else {
                    //로그아웃
                }
            }
        };
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void setStatusbar() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        //String splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        splash_background = "#aabbcc";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }
    }

    private void init() {
        id_Et = (EditText) findViewById(R.id.loginactivity_edittext_id);
        password_Et = (EditText) findViewById(R.id.loginactivity_edittext_password);

        loginBtn = (Button) findViewById(R.id.loginactivity_button_login);
        signupBtn = (Button) findViewById(R.id.loginactivity_button_signup);
        loginBtn.setBackgroundColor(Color.parseColor(splash_background));
        signupBtn.setBackgroundColor(Color.parseColor(splash_background));

        loginBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);

        customProgressDialog = new CustomProgressDialog(LoginActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginactivity_button_login:
                if (id_Et.getText().toString().equals("") || password_Et.getText().toString().equals("")) {
                    Toast.makeText(this, "아이디 비밀번호를 모두 입력하시오.", Toast.LENGTH_SHORT).show();
                } else {
                    customProgressDialog.show();

                    loginEvent();
                }
                break;
            case R.id.loginactivity_button_signup:
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                break;
        }
    }


    private void loginEvent() {
        firebaseAuth.signInWithEmailAndPassword(id_Et.getText().toString(), password_Et.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //로그인이 완료되었는지만 확인해주는 리스너
                if (!task.isSuccessful()) {   //로그인 실패한 부분
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    customProgressDialog.dismiss();
                }else {

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);   //리스너를 달아주자
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);    //리스너를 때주자
    }


}
