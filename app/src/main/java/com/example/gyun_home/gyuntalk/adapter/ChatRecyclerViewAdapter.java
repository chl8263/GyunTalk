package com.example.gyun_home.gyuntalk.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gyun_home.gyuntalk.R;
import com.example.gyun_home.gyuntalk.chat.MessageActivity;
import com.example.gyun_home.gyuntalk.model.ChatModel;
import com.example.gyun_home.gyuntalk.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<ChatModel> chatModels = new ArrayList<>();
    private String uid;
    private ArrayList<String> destinationUsers = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private Context context;

    public ChatRecyclerViewAdapter(Context context) {
        this.context = context;
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatModels.clear();
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    chatModels.add(item.getValue(ChatModel.class));
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final CustomViewHolder customViewHolder = (CustomViewHolder)holder;
        String destinationUid = null;

        //채팅방에 있는 유저를 전부 체크
        for(String user: chatModels.get(position).users.keySet()){
            if(!user.equals(uid)){
                destinationUid = user;
                destinationUsers.add(destinationUid);
            }
        }
        FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                Glide.with(customViewHolder.itemView.getContext())
                        .load(userModel.getProfileImageUrl())
                        .apply(new RequestOptions().circleCrop())
                        .into(customViewHolder.imageView);

                customViewHolder.textView_title.setText(userModel.getUserName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //메세지를 내림차순으로 정렬후 마지막 메세지의 키값을 가져옴
        Map<String,ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
        commentMap.putAll(chatModels.get(position).comments);

        String lastMessageKey = (String)commentMap.keySet().toArray()[0];
        customViewHolder.textView_last_messagge.setText(chatModels.get(position).comments.get(lastMessageKey).message);
        ////////

        customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra("destinationUid",destinationUsers.get(position));

                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(),R.anim.fromright,R.anim.toleft);
                context.startActivity(intent,activityOptions.toBundle());
            }
        });

        //unix time 을 현재 시간으로 컨버팅 하는 부분
        long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
        Date date = new Date(unixTime);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String time = simpleDateFormat.format(date);
        //////////////////////////////

        customViewHolder.textView_last_timestamp.setText(time);
    }

    @Override
    public int getItemCount() {
        return chatModels.size();
    }


    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView_title;
        public TextView textView_last_messagge;
        public TextView textView_last_timestamp;
        public CustomViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.chatitem_imageview);
            textView_title = view.findViewById(R.id.chatitem_textview_title);
            textView_last_messagge = view.findViewById(R.id.chatitme_textview_lastMessage);
            textView_last_timestamp = view.findViewById(R.id.chatitem_textview_timestamp);
        }
    }
}
