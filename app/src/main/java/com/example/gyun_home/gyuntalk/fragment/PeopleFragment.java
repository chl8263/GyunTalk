package com.example.gyun_home.gyuntalk.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gyun_home.gyuntalk.adapter.PeopleFragmentRecyclerViewAdapter;
import com.example.gyun_home.gyuntalk.R;

public class PeopleFragment extends Fragment{

    private RecyclerView recyclerView;

    public PeopleFragment() {
    }

    public static PeopleFragment getInstance() {
        Bundle args = new Bundle();
        PeopleFragment fragment = new PeopleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people,container,false);

        recyclerView = (RecyclerView)view.findViewById(R.id.peoplefragment_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter(getContext()));

        FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.peoplefragment_floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(),SelectFriendActivity.class));
            }
        });

        return view;
    }
}
