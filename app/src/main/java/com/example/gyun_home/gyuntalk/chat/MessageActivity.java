package com.example.gyun_home.gyuntalk.chat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.gyun_home.gyuntalk.R;
import com.example.gyun_home.gyuntalk.adapter.MessageActivityRecyclerViewAdapter;
import com.example.gyun_home.gyuntalk.model.ChatModel;
import com.example.gyun_home.gyuntalk.model.NotificationModel;
import com.example.gyun_home.gyuntalk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {

    private String destinationUid;
    private Button button;
    private EditText editText;

    private String uid;
    private String chatRoomUid;

    private RecyclerView recyclerView;

    private UserModel destinationUserModel = MessageActivityRecyclerViewAdapter.userModel;

    public static DatabaseReference databaseReference;
    public static ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();    //나
        destinationUid = getIntent().getStringExtra("destinationUid");  //상대

        button = (Button) findViewById(R.id.messageActivity_button);
        editText = (EditText) findViewById(R.id.messageActivity_editText);

        button.setOnClickListener(this);

        recyclerView = (RecyclerView)findViewById(R.id.messageActivity_recyclerView);
        checkChatRoom();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messageActivity_button:

                ChatModel chatModel = new ChatModel();

                chatModel.users.put(uid, true);
                chatModel.users.put(destinationUid, true);

                if(chatRoomUid == null){
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel)     // push -> primary key 같은 역할을 한다.
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    checkChatRoom();
                                }
                            });
                }else {
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    comment.timestamp = ServerValue.TIMESTAMP;  //firebase에서 제공하는 서버 시간

                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").push().setValue(comment)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //sendGcm();
                                    editText.setText("");
                                }
                            });
                }

                break;
        }
    }

    private void sendGcm(){
        Gson gson = new Gson();

        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = destinationUserModel.pushToken;
        notificationModel.notification.title = username;
        notificationModel.notification.text = editText.getText().toString();
        notificationModel.data.title = username;
        notificationModel.data.text = editText.getText().toString();

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));

        Request request = new Request.Builder().header("Content-Type","application/json")
                .addHeader("Authorization","key=AIzaSyBoNGZldnwBhLUKm6G75wcLxcWOE3NIGqU")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }



    private void checkChatRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid).equalTo(true) //orderBychild -> 중복검사
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.e("!!!!!!!!!!","!!!!!!!!!!!");
                        for (DataSnapshot item : dataSnapshot.getChildren()) {
                            ChatModel chatModel = item.getValue(ChatModel.class);
                            if(chatModel.users.containsKey(destinationUid)){
                                chatRoomUid = item.getKey();
                                button.setEnabled(true);

                                recyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                                recyclerView.setAdapter(new MessageActivityRecyclerViewAdapter(getApplicationContext(),chatRoomUid,destinationUid,uid,recyclerView));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        databaseReference.removeEventListener(valueEventListener);
        finish();
        overridePendingTransition(R.anim.fromleft,R.anim.toright);  //finish 밑에 들어가야 anmation 이 적용 가능하다
    }
}
