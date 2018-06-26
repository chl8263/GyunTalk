package com.example.gyun_home.gyuntalk.Main;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.gyun_home.gyuntalk.Fragment.PeopleFragment;
import com.example.gyun_home.gyuntalk.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private BottomNavigationView navigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    private PeopleFragment peopleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragment();

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


        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.mainActivity_frameLayout, peopleFragment, "first");
        //fragmentTransaction.add(R.id.mainActivity_frameLayout, secondFragment, "second");
        //fragmentTransaction.add(R.id.mainActivity_frameLayout, thirdFragment, "third");

        //fragmentTransaction.hide(secondFragment);
        //fragmentTransaction.hide(thirdFragment);
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
