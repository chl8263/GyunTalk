package com.example.gyun_home.gyuntalk.Login;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.gyun_home.gyuntalk.AnimEffect.CustomProgressActivity;
import com.example.gyun_home.gyuntalk.AnimEffect.CustomProgressDialog;
import com.example.gyun_home.gyuntalk.Model.UserModel;
import com.example.gyun_home.gyuntalk.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int PICK_FROM_ALBUM = 10;
    private EditText email_Et;
    private EditText name_Et;
    private EditText password_Et;
    private Button signup_Btn;
    private ImageView profile_Img;
    private Uri imageUri;

    private String splash_background;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setStatusbar();
        init();

    }
    private void setStatusbar(){
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        //String splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        splash_background = "#aabbcc";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }
    }
    private void init (){
        profile_Img = (ImageView) findViewById(R.id.signupActivity_imageview_profile);
        profile_Img.setOnClickListener(this);
        email_Et = (EditText) findViewById(R.id.signupActivity_editText_email);
        name_Et = (EditText) findViewById(R.id.signupActivity_editText_name);
        password_Et = (EditText) findViewById(R.id.signupActivity_editText_password);
        signup_Btn = (Button) findViewById(R.id.signupActivity_button_signup);
        signup_Btn.setBackgroundColor(Color.parseColor(splash_background));
        signup_Btn.setOnClickListener(this);

        customProgressDialog = new CustomProgressDialog(SignupActivity.this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signupActivity_imageview_profile:
               click_profile();
                break;
            case R.id.signupActivity_button_signup:
               click_signUp();

                break;
        }
    }

    private void click_profile(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    private void click_signUp(){

        if (email_Et.getText().toString().equals("") || name_Et.getText().toString().equals("") || password_Et.getText().toString().equals("")||imageUri==null) {
            Toast.makeText(getApplicationContext(), "정보를 모두 입력하여 주세요", Toast.LENGTH_SHORT).show();
        } else {

            customProgressDialog.show();

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email_Et.getText().toString(), password_Et.getText().toString())
                    .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //회원가입이 정상적으로 firebase 에 이루어지면 넘어오는 callback

                            final String uid = task.getResult().getUser().getUid();

                            FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    String imageUrl = task.getResult().getDownloadUrl().toString();

                                    UserModel userModel = new UserModel();
                                    userModel.setUserName(name_Et.getText().toString());
                                    userModel.setProfileImageUrl(imageUrl);
                                    FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            customProgressDialog.dismiss();
                                            SignupActivity.this.finish();

                                        }
                                    });
                                }
                            });
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profile_Img.setImageURI(data.getData()); //이미지 뷰 바꿈
            imageUri = data.getData();  //이미지 데이터 가져옴
        }
    }


}
