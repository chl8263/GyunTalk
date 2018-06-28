package com.example.gyun_home.gyuntalk.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.util.ArrayList;

public class SelectFriendRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<UserModel> userModels;
    private Context context;
    private ChatModel chatModel;

    public SelectFriendRecyclerViewAdapter(Context context,ChatModel chatModel) {
        this.chatModel = chatModel;
        this.context = context;
        userModels = new ArrayList<>();
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userModels.clear(); //clear
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if(userModel.getUid().equals(myUid)){
                        continue;
                    }
                    userModels.add(snapshot.getValue(UserModel.class));
                }
                notifyDataSetChanged(); //새로고침
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_select,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        Glide.with(holder.itemView.getContext())
                .load(userModels.get(position).getProfileImageUrl())
                .apply(new RequestOptions().circleCrop())
                .into(((CustomViewHolder)holder).imageView);

        ((CustomViewHolder)holder).textView.setText(userModels.get(position).getUserName().toString());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(v.getContext(), MessageActivity.class);
                intent.putExtra("destinationUid",userModels.get(position).getUid());
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(context, R.anim.fromright,R.anim.toleft);
                context.startActivity(intent,activityOptions.toBundle());*/
            }
        });
        if(userModels.get(position).getComment()!=null) {
            ((CustomViewHolder) holder).textView_comment.setText(userModels.get(position).getComment());
        }
        ((CustomViewHolder)holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //true  면 체크된 상태
                if(isChecked){
                    chatModel.users.put(userModels.get(position).getUid(),true);
                }else{
                    chatModel.users.remove(userModels.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView textView_comment;
        public CheckBox checkBox;

        public CustomViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.friendselect_imageView);
            textView = view.findViewById(R.id.friendselect_textView);
            textView_comment = (TextView)view.findViewById(R.id.friendselect_textView_comment);
            checkBox = view.findViewById(R.id.friendselect_checkbox);
        }
    }
}

