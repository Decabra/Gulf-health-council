package com.example.ghc;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{

    final BasicFragment BF = new BasicFragment();
    final AlertFragment AF = new AlertFragment();
    final SecurityFragment SF = new SecurityFragment();
    final MoreFragment MF = new MoreFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = BF;
    private FetchData fetchData = new FetchData();

    BottomNavigationView bottomNavigation;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle(getResources().getString(R.string.title_home_activity));
        fm.beginTransaction().add(R.id.fragment_container,BF, "1").commit();

        /*Threads don't allow network operations on main thread and if you try to do that it will crash your app.
        Below will put the thread in strict mode means network operations can be performed on main thread but it will slow down your app.
        Alternative option is to use AsyncTask to perform Network operation in background and avoid your app from being slowing down.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        strictMode.setThreadPolicy(policy);*/

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);
        progressDialog = new ProgressDialog(this);
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_basic:
                    fm.beginTransaction().hide(active).show(BF).commit();
                    active = BF;
                    return true;
                case R.id.nav_alert:
                    fm.beginTransaction().hide(active).show(AF).commit();
                    active = AF;
                    return true;
                case R.id.nav_security:
                    fm.beginTransaction().hide(active).show(SF).commit();
                    active = SF;
                    return true;
                case R.id.nav_more:
                    fm.beginTransaction().hide(active).show(MF).commit();
                    active = MF;
                    return true;
            }
            return false;
        }
    };

    private class AsyncTaskRunner extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {
            fm.beginTransaction().add(R.id.fragment_container, MF, "4").hide(MF).commit();
            fm.beginTransaction().add(R.id.fragment_container, SF, "3").hide(SF).commit();
            fm.beginTransaction().add(R.id.fragment_container, AF, "2").hide(AF).commit();
            return null;
        }
    }

}
