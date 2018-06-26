package com.example.gyun_home.gyuntalk.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gyun_home.gyuntalk.R;
import com.example.gyun_home.gyuntalk.adapter.ChatRecyclerViewAdapter;

public class ChatFragment extends Fragment {


    public ChatFragment() {
    }

    public static ChatFragment getInstance() {
        Bundle args = new Bundle();
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat,container,false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.chatfragment_recyclerView);
        recyclerView.setAdapter(new ChatRecyclerViewAdapter(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        return view;
    }



}
