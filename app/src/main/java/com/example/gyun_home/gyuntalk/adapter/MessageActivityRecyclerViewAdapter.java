package com.example.gyun_home.gyuntalk.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gyun_home.gyuntalk.R;
import com.example.gyun_home.gyuntalk.model.ChatModel;
import com.example.gyun_home.gyuntalk.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String chatRoomUid;
    private String uid;
    private String destiantionuid;
    private ArrayList<ChatModel.Comment> comments;
    private UserModel userModel;
    private RecyclerView recyclerView;

    public MessageActivityRecyclerViewAdapter(Context context, String chatRoomUid, String destiantionuid,String uid,RecyclerView recyclerView) {
        this.context = context;
        this.chatRoomUid = chatRoomUid;
        this.destiantionuid = destiantionuid;
        this.uid = uid;
        this.recyclerView = recyclerView;
        comments = new ArrayList<>();



        FirebaseDatabase.getInstance().getReference().child("users").child(this.destiantionuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userModel = dataSnapshot.getValue(UserModel.class);
                getMessageList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMessageList() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.clear();
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    comments.add(item.getValue(ChatModel.Comment.class));
                }

                notifyDataSetChanged();     //새로고침

                recyclerView.scrollToPosition(comments.size()-1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageViewHolder messageViewHolder = ((MessageViewHolder) holder);
        //내가보낸 메세지
        if(comments.get(position).uid.equals(uid)){
            messageViewHolder.textView_message.setText(comments.get(position).message);
            messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
            messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
            messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
        }else { //상대가 보낸 메세지

            Glide.with(holder.itemView.getContext())
                    .load(userModel.getProfileImageUrl())
                    .apply(new RequestOptions().circleCrop())
                    .into(messageViewHolder.imageView_profile);

            messageViewHolder.textView_name.setText(userModel.getUserName());
            messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
            messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
            messageViewHolder.textView_message.setText(comments.get(position).message);
            messageViewHolder.textView_message.setTextSize(25);
            messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
        }

        ((MessageViewHolder) holder).textView_message.setText(comments.get(position).message);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView textView_message;
        public TextView textView_name;
        public ImageView imageView_profile;
        public LinearLayout linearLayout_destination;
        public LinearLayout linearLayout_main;
        public MessageViewHolder(View view) {
            super(view);
            textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
            textView_name = (TextView) view.findViewById(R.id.messageItem_textView_name);
            imageView_profile = (ImageView) view.findViewById(R.id.messageItem_imageView_profile);
            linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_destination);
            linearLayout_main = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_main);
        }
    }
}
