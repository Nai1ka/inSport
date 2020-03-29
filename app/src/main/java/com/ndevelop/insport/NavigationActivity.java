package com.ndevelop.insport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
import com.ndevelop.insport.Fragments.SettingsFragment;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static Toolbar toolbar;
    private AdView mAdView;
    NavigationView navigationView;
    public FragmentManager manager = getSupportFragmentManager();
    MapsFragment mapsFragment = new MapsFragment();
    RouteInfoFragment routeInfoFragment = new RouteInfoFragment();
    HistoryFragment historyFragment = new HistoryFragment();
    CreditFragment creditFragment = new CreditFragment();
    SettingsFragment settingsFragment = new SettingsFragment();
    private SharedPreferences mSettings;
    private static final String APP_SETTINGS = "Settings";
    private static final String SELECTED_ROUTE = "SelectedRoute";
    private int current_fragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("inSport");
        setSupportActionBar(toolbar);
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
        manager.beginTransaction().add(R.id.mainLayout, mapsFragment, "map").commit();


    }
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            if (manager.findFragmentByTag("map").isHidden()) {
                manager.beginTransaction()
                        .show(mapsFragment)
                        .commit();
            }
            if (manager.findFragmentByTag("history") != null) {

                manager.beginTransaction()
                        .remove(historyFragment)
                        .commit();
            }
            if (manager.findFragmentByTag("credits") != null) {
                manager.beginTransaction()
                        .remove(creditFragment)
                        .commit();
            }
            if (manager.findFragmentByTag("settings") != null) {
                manager.beginTransaction()
                        .remove(settingsFragment)
                        .commit();
            }



        } else if (id == R.id.nav_history) {

            if (manager.findFragmentByTag("map").isVisible()) {
                manager.beginTransaction()
                        .hide(mapsFragment)
                        .commit();
            }

            if (manager.findFragmentByTag("history") == null) {

                manager.beginTransaction()
                        .add(R.id.mainLayout, historyFragment, "history")
                        .commit();
            }
            if (manager.findFragmentByTag("credits") != null) {
                manager.beginTransaction()
                        .remove(creditFragment)
                        .commit();
            }
            if (manager.findFragmentByTag("settings") != null) {
                manager.beginTransaction()
                        .remove(settingsFragment)
                        .commit();
            }




        } else if (id == R.id.nav_credits) {
            if (manager.findFragmentByTag("map").isVisible()) {
                manager.beginTransaction()
                        .hide(mapsFragment)
                        .commit();
            }
            if (manager.findFragmentByTag("history") != null) {
                manager.beginTransaction()
                        .remove(historyFragment)
                        .commit();
            }
            if (manager.findFragmentByTag("credits") == null) {
                manager.beginTransaction()
                        .add(R.id.mainLayout, creditFragment, "credits")
                        .commit();
            }
            if (manager.findFragmentByTag("settings") != null) {
                manager.beginTransaction()
                        .remove(settingsFragment)
                        .commit();
            }

        } else if (id == R.id.nav_settings) {
            if (manager.findFragmentByTag("map").isVisible()) {
                manager.beginTransaction()
                        .hide(mapsFragment)
                        .commit();
            }

            if (manager.findFragmentByTag("history") != null) {
                manager.beginTransaction()
                        .remove(historyFragment)
                        .commit();
            }
            if (manager.findFragmentByTag("credits") != null) {
                manager.beginTransaction()
                        .remove(creditFragment)
                        .commit();
            }
            if (manager.findFragmentByTag("settings") == null) {
                manager.beginTransaction()
                        .add(R.id.mainLayout, settingsFragment, "settings")
                        .commit();
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static void change_title(String str) {
        toolbar.setTitle(str);
    }

    public void fromFragmentData(Integer value) {
        Bundle mArg = new Bundle();
        mArg.putInt("numberOfSelectedRoute", value + 1);
        routeInfoFragment.setArguments(mArg);
        manager.beginTransaction()
                .add(R.id.mainLayout, routeInfoFragment,"routeInfo")
                .remove(historyFragment)
                .addToBackStack(null)
                .commit();
    }
}