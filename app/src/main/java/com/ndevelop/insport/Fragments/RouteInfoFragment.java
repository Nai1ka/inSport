package com.ndevelop.insport.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ndevelop.insport.R;
import com.ndevelop.insport.RecyclerView.RouteInfo;
import com.ndevelop.insport.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;

public class RouteInfoFragment extends Fragment implements OnMapReadyCallback {
    private static final String APP_SETTINGS = "Settings";
    private static final String APP_SETTINGS_NUMBER_OF_ROUTES = "Routes";
    private SharedPreferences mSettings;
    private Polyline polyline;
    private GoogleMap googleMap;
    private ArrayList<LatLng> coordinates = new ArrayList<>();
private LatLng cameraPosition;
    public RouteInfoFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_route_info, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.history_map);
        mapFragment.getMapAsync(this);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();
        mSettings = this.getActivity().getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        int numberOfRoutes = mSettings.getInt(APP_SETTINGS_NUMBER_OF_ROUTES, 0);
        ArrayList<RouteInfo> routes = new ArrayList<>();
        if (coordinates != null) coordinates.clear();

        //Bundle b = getArguments();
        //int selectedRoute= b.getInt("numberOfSelectedRoute");
// TODO: 31.01.2020 сделать вывод марщрута, не забыть, что выводит в обратном порядке 
        try {
            JSONObject info_about_route = new JSONObject(Utils.read(getActivity(), "routes.json").getString(numberOfRoutes + 1 - getArguments().getInt("numberOfSelectedRoute") + ""));
            String raw = info_about_route.getString("route");
            String[] raw_array = raw.substring(1, raw.length() - 1).split(",");
            cameraPosition=new LatLng(Double.parseDouble(raw_array[0]), Double.parseDouble(raw_array[1]));
            for (int i = 0; i < raw_array.length; i = i + 2) {
                coordinates.add(new LatLng(Double.parseDouble(raw_array[i]), Double.parseDouble(raw_array[i + 1])));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap gm) {
        if (polyline != null) polyline.remove();

        googleMap = gm;

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition,13));
        polyline = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(7)
                .color(Color.GREEN)
                .addAll(coordinates));
        // googleMap.addPolyline(polyline);
    }
}
