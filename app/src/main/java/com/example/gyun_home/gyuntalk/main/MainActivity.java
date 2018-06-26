package com.example.gyun_home.gyuntalk.main;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.gyun_home.gyuntalk.fragment.AcountFragment;
import com.example.gyun_home.gyuntalk.fragment.ChatFragment;
import com.example.gyun_home.gyuntalk.fragment.PeopleFragment;
import com.example.gyun_home.gyuntalk.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BottomNavigationView navigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    private PeopleFragment peopleFragment;
    private ChatFragment chatFragment;
    private AcountFragment acountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragment();
        passPushTokenToServer();

    }

    public void passPushTokenToServer(){     //firebase cloud message 보내는 부분
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);    //setValue 하면 기존을 지워서 안됨 이렇게 업뎃할것
    }

    public void initFragment() {
        onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_people:
                        SelectNavView("first");
                        return true;
                    case R.id.action_chat:
                        SelectNavView("second");
                        return true;
                    case R.id.action_account:
                        SelectNavView("third");
                        return true;
                }
                return false;
            }
        };

        peopleFragment = PeopleFragment.getInstance();
        chatFragment = ChatFragment.getInstance();
        acountFragment = AcountFragment.getInstance();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.mainActivity_frameLayout, peopleFragment, "first");
        fragmentTransaction.add(R.id.mainActivity_frameLayout, chatFragment, "second");
        fragmentTransaction.add(R.id.mainActivity_frameLayout, acountFragment, "third");

        fragmentTransaction.hide(chatFragment);
        fragmentTransaction.hide(acountFragment);
        fragmentTransaction.commit();

        navigationView = (BottomNavigationView) findViewById(R.id.mainactivity_bottomNavigationView);
        navigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private void SelectNavView(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        @SuppressLint("RestrictedApi") List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment.getTag().equals(tag)) {
                fragmentTransaction.show(fragment);
            } else {
                fragmentTransaction.hide(fragment);
            }
        }
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

}
