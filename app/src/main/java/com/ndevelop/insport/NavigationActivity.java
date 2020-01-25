package com.ndevelop.insport;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.ndevelop.insport.Fragments.CreditFragment;
import com.ndevelop.insport.Fragments.HistoryFragment;
import com.ndevelop.insport.Fragments.MapsFragment;
import com.ndevelop.insport.Fragments.RouteInfoFragment;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static Toolbar toolbar;
    private AdView mAdView;
    NavigationView navigationView;
    public FragmentManager manager = getSupportFragmentManager();
    MapsFragment mapsFragment = new MapsFragment();
    RouteInfoFragment routeInfoFragment= new RouteInfoFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    CreditFragment creditFragment = new CreditFragment();
    private SharedPreferences mSettings;
    private static final String APP_SETTINGS = "Settings";
    private static final String SELECTED_ROUTE = "SelectedRoute";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("inSport");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        mAdView = findViewById(R.id.adView);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {

                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Toast.makeText(NavigationActivity,"Ошибка загрузки рекламы. Сообщите разработчика код: " + errorCode, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mSettings = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_map);
        manager.beginTransaction().add(R.id.mainLayout, mapsFragment).commit();
        manager.beginTransaction().add(R.id.mainLayout, historyFragment).hide(historyFragment).commit();
        manager.beginTransaction().add(R.id.mainLayout, creditFragment).hide(creditFragment).commit();
        manager.beginTransaction().add(R.id.mainLayout, routeInfoFragment).hide(routeInfoFragment).commit();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();
        if (id == R.id.nav_map && !(navigationView.getMenu().findItem(R.id.nav_map).isChecked())) {

            manager.beginTransaction()
                    .hide(creditFragment)
                    .hide(historyFragment)
                    .show(mapsFragment)
                    .commit();

        } else if (id == R.id.nav_history) {
            manager.beginTransaction()
                    .hide(mapsFragment)
                    .hide(creditFragment)
                    .show(historyFragment)
                    .commit();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_credits) {
            manager.beginTransaction()
                    .hide(mapsFragment)
                    .hide(historyFragment)
                    .show(creditFragment)
                    .commit();

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

public static void change_title(String str){
    toolbar.setTitle(str);
}
public void fromFragmentData(Integer value) {
mSettings.edit().putInt(SELECTED_ROUTE,value).apply();
   // Toast.makeText(this, mSettings.getInt(SELECTED_ROUTE,0)+"", Toast.LENGTH_SHORT).show();
    manager.beginTransaction()
            .show(routeInfoFragment)
            .hide(historyFragment)
            .addToBackStack(null)
            //.replace(R.id.mainLayout,routeInfoFragment)
            .commit();
    }
}
//TODO исправить переходы фрагментов