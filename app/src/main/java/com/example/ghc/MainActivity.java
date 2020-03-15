package com.example.ghc;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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


    private FetchData fetchData;

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle(getResources().getString(R.string.title_home_activity));

        //Need to be look strictly and change
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);

        fm.beginTransaction().add(R.id.fragment_container, MF, "4").hide(MF).commit();
        fm.beginTransaction().add(R.id.fragment_container, SF, "3").hide(SF).commit();
        fm.beginTransaction().add(R.id.fragment_container, AF, "2").hide(AF).commit();
        fm.beginTransaction().add(R.id.fragment_container,BF, "1").commit();

        fetchData = new FetchData();
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


}
