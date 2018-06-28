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
import com.example.gyun_home.gyuntalk.chat.MessageActivity;
import com.example.gyun_home.gyuntalk.model.ChatModel;
import com.example.gyun_home.gyuntalk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class MessageActivityRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String chatRoomUid;
    private String uid;
    private String destiantionuid;
    private ArrayList<ChatModel.Comment> comments;
    public static UserModel userModel;
    private RecyclerView recyclerView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");


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
        MessageActivity.databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments");
        MessageActivity.valueEventListener = MessageActivity.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.clear();

                Map<String,Object> readUsersMap = new HashMap<>();

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    String key = item.getKey();
                    ChatModel.Comment comment = item.getValue(ChatModel.Comment.class);
                    comment.readUsers.put(uid,true);

                    readUsersMap.put(key,comment);
                    comments.add(comment);
                }

                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").updateChildren(readUsersMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        notifyDataSetChanged();     //새로고침

                        recyclerView.scrollToPosition(comments.size()-1);
                    }
                });

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

            setReadCounter(position,messageViewHolder.textView_readCounter_left);
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

            setReadCounter(position,messageViewHolder.textView_readCounter_rigft);
        }
        //unix time 을 현재 시간으로 컨버팅 하는 부분
        long unixTime = (long) comments.get(position).timestamp;
        Date date = new Date(unixTime);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String time = simpleDateFormat.format(date);
        //////////////////////////////
        messageViewHolder.textView_timeStamp.setText(time);
        ((MessageViewHolder) holder).textView_message.setText(comments.get(position).message);
    }

    private void setReadCounter (final int position, final TextView textView){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String,Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();

                int count = users.size() - comments.get(position).readUsers.size();

                if(count > 0){
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                }else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        public TextView textView_timeStamp;
        public TextView textView_readCounter_left;
        public TextView textView_readCounter_rigft;

        public MessageViewHolder(View view) {
            super(view);
            textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
            textView_name = (TextView) view.findViewById(R.id.messageItem_textView_name);
            imageView_profile = (ImageView) view.findViewById(R.id.messageItem_imageView_profile);
            linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_destination);
            linearLayout_main = (LinearLayout)view.findViewById(R.id.messageItem_linearlayout_main);
            textView_timeStamp = view.findViewById(R.id.messageItem_textView_timestamp);
            textView_readCounter_left = view.findViewById(R.id.messageItem_textview_readCounter_left);
            textView_readCounter_rigft = view.findViewById(R.id.messageItem_textview_readCounter_right);
        }
    }
}
