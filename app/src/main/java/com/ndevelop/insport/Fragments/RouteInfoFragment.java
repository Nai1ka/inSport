package com.ndevelop.insport.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;

import com.google.android.gms.maps.GoogleMap;

public class RouteInfoFragment extends Fragment implements OnMapReadyCallback {
    private static final String APP_SETTINGS = "Settings";
    private static final String APP_SETTINGS_NUMBER_OF_ROUTES = "Routes";
    private SharedPreferences mSettings;
    private Polyline polyline;
    private GoogleMap googleMap;

    private ArrayList<LatLng> coordinates = new ArrayList<>();
    JSONObject info_about_route;
    private LatLng cameraPosition;

    public RouteInfoFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getActivity().getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        int numberOfRoutes = mSettings.getInt(APP_SETTINGS_NUMBER_OF_ROUTES, 0);
        if (coordinates != null) coordinates.clear();


        try {

            info_about_route = new JSONObject(Utils.read(getActivity(), "routes.json").getString(numberOfRoutes + 1 - getArguments().getInt("numberOfSelectedRoute") + ""));
            String raw = info_about_route.getString("route");
            String[] raw_array = raw.substring(1, raw.length() - 1).split(",");
            cameraPosition = new LatLng(Double.parseDouble(raw_array[0]), Double.parseDouble(raw_array[1]));
            for (int i = 0; i < raw_array.length; i = i + 2) {
                coordinates.add(new LatLng(Double.parseDouble(raw_array[i]), Double.parseDouble(raw_array[i + 1])));
            }
            Toast.makeText(getActivity(), "" + info_about_route.getString("distance"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_route_info, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.history_map);
        TextView speedText = v.findViewById(R.id.speedText);
        TextView averageSpeedText = v.findViewById(R.id.averageSpeedText);
        TextView distanceText = v.findViewById(R.id.distanceText);
        try {
            //Toast.makeText(getActivity(), ""+info_about_route.getString("distance"), Toast.LENGTH_SHORT).show();
            distanceText.setText("Расстояние: " + info_about_route.getString("distance"));
            speedText.setText("Максимальная скорость: " + info_about_route.getString("maxSpeed"));
            averageSpeedText.setText("Средняя скорость: " + info_about_route.getString("averageSpeed"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    }


    @Override
    public void onMapReady(GoogleMap gm) {
        if (polyline != null) polyline.remove();

        googleMap = gm;

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition, 13));
        polyline = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(7)
                .color(Color.GREEN)
                .addAll(coordinates));
        // googleMap.addPolyline(polyline);
    }
}
