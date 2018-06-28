package com.example.gyun_home.gyuntalk.fragment;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.gyun_home.gyuntalk.R;
import com.example.gyun_home.gyuntalk.adapter.SelectFriendRecyclerViewAdapter;
import com.example.gyun_home.gyuntalk.model.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SelectFriendActivity extends AppCompatActivity {
    private RecyclerView recyclerView ;
    private Button button;
    private SelectFriendRecyclerViewAdapter selectFriendRecyclerViewAdapter;
    private ChatModel chatModel = new ChatModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);

        init();

    }

    private void init(){
        recyclerView = (RecyclerView) findViewById(R.id.selectFriendactivity_recyclerView);
        selectFriendRecyclerViewAdapter = new SelectFriendRecyclerViewAdapter(getApplicationContext(),chatModel);
        recyclerView.setAdapter(selectFriendRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        button = (Button)findViewById(R.id.selectFriendactivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                chatModel.users.put(myUid,true);

                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);
            }
        });

    }
}
