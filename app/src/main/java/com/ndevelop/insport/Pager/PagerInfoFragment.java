package com.ndevelop.insport.Pager;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ndevelop.insport.Fragments.RouteInfoFragment;
import com.ndevelop.insport.R;


public class PagerInfoFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {
    private GoogleMap mMap;
    private SharedPreferences mSettings;
    private static final String APP_SETTINGS = "Settings";
    private static final String SELECTED_ROUTE = "SelectedRoute";
    private int pageNumber;

    public static PagerInfoFragment newInstance(int page) {
        PagerInfoFragment fragment = new PagerInfoFragment();
        Bundle args = new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments() != null ? getArguments().getInt("num") : 1;
        mSettings = this.getActivity().getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pager_info, container, false);
        if (getArguments() != null) {
             int number = getArguments().getInt("num");
        }
        SupportMapFragment mapFragment =(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.historyFragmentMap);
        mapFragment.getMapAsync(this);
       switch (pageNumber) {
            case 0:
                mapFragment.getView().setVisibility(v.VISIBLE);
                int selected_route=mSettings.getInt(SELECTED_ROUTE,0);
                Toast.makeText(getActivity(), ""+selected_route, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                mapFragment.getView().setVisibility(v.GONE);
                break;
        }
        return v;
    }


    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Hello world"));
    }
}
