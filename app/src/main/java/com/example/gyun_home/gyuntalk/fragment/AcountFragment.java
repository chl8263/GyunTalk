package com.example.gyun_home.gyuntalk.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gyun_home.gyuntalk.R;
import com.example.gyun_home.gyuntalk.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AcountFragment  extends Fragment {

    private ImageView imageView_myimage;
    private TextView textView_myName;
    private TextView textView_comment;

    public AcountFragment() {
    }

    public static AcountFragment getInstance() {
        Bundle args = new Bundle();
        AcountFragment fragment = new AcountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account,container,false);

        textView_myName = view.findViewById(R.id.fragmentacount_myView_name);
        textView_comment = view.findViewById(R.id.fragmentacount_myView_comment);
        imageView_myimage = view.findViewById(R.id.fragmentacount_myView_image);

        Button button = (Button)view.findViewById(R.id.accountfragment_button_comment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(getContext());
            }
        });

        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);

                textView_myName.setText(userModel.getUserName().toString());
                if(userModel.getComment() != null) {
                    textView_comment.setText(userModel.getComment().toString());
                }

                Glide.with(getContext())
                        .load(userModel.getProfileImageUrl())
                        .apply(new RequestOptions().circleCrop())
                        .into(imageView_myimage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.dialog_comment,null);
        final EditText editText = view.findViewById(R.id.commentDialog_edittext);

        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {    //firebase 데이터 수정 하는방법 , setValue 는 전부 없어짐 update 로 할것
                Map<String,Object> stringObjectMap = new HashMap<>();

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                stringObjectMap.put("comment",editText.getText().toString());

                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(stringObjectMap);
            }
        }).setNegativeButton("최소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}