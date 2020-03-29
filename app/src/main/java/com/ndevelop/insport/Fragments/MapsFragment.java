package com.ndevelop.insport.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.ndevelop.insport.MyLocationService;
import com.ndevelop.insport.NavigationActivity;
import com.ndevelop.insport.R;
import com.ndevelop.insport.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback {
    private double last_latitude = 0;
    private double last_longitude = 0;
    private boolean isStart = false;
    private boolean isPause = false;
    private byte speedType = 0;
    private boolean hasVisited;
    private float distance;
    private int seconds;
    private int minutes;
    private int hours;
    private int past_seconds;
    private int past_minutes;
    private int past_hours;
    private long startTime = 0;
    private int maxSpeed = 0;
    private float averageSpeed = 0;
    private LocationRequest locationRequest;
    private static MapsFragment instance;
    private GoogleMap mMap;
    private Polyline route;
    private static final String APP_SETTINGS = "Settings";
    private static final String APP_SETTINGS_ENTRY = "FirstEntry";
    private static final String APP_SETTINGS_WEIGHT = "Weight";
    private static final String APP_SETTINGS_HEIGHT = "Height";
    private static final String APP_SETTINGS_NUMBER_OF_ROUTES = "Routes";
    private SharedPreferences.Editor mEditor;
    private TextView speedText;
    private TextView distanceText;
    private ImageButton startButton;
    private ImageButton stopButton;
    private ImageButton pauseButton;
    private EditText weightText;
    private EditText heightText;
    private Button okButton;
    private View view_medium;
    private View view_background;
    private List<LatLng> points;
    private ArrayList<Integer> speedList = new ArrayList<>();
    private ArrayList<LatLng> resultPoints = new ArrayList<>();
    private Handler timerHandler = new Handler();
    private PolylineOptions polylineOptions = new PolylineOptions()
            .color(Color.GREEN)
            .width(6);
    LatLng myLatLng;
    private double old_latitude = 0;
    private double old_longitude = 0;


    public static MapsFragment getInstance() {
        return instance;
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            seconds = past_seconds + (int) (millis / 1000);
            minutes = past_minutes + seconds / 60;
            hours = past_hours + minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;
            NavigationActivity.change_title(hours + ":" + minutes + ":" + seconds);
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        instance = this;
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        final SharedPreferences mSettings = this.getActivity().getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        mEditor = mSettings.edit();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            updateLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
        startButton = v.findViewById(R.id.startButton);
        stopButton = v.findViewById(R.id.stopButton);
        pauseButton = v.findViewById(R.id.pauseButton);
        speedText = v.findViewById(R.id.speedText);
        distanceText = v.findViewById(R.id.distanceText);
        heightText = v.findViewById(R.id.heightText);
        weightText = v.findViewById(R.id.weightText);
        okButton = v.findViewById(R.id.okButton);
        view_medium = v.findViewById(R.id.view_medium);
        view_background = v.findViewById(R.id.view_background);
        speedList.add(10);
        speedList.add(11);


        Spinner speedSpinner = v.findViewById(R.id.speedSpinner);
        speedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int position, long selectedId) {
                if (position == 0) speedType = 0;
                if (position == 1) {
                    speedType = 1;
                    speedText.setText(maxSpeed + " км/ч");
                }
                if (position == 2) {
                    speedType = 2;
                    speedText.setText(averageSpeed + " км/ч");
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        startButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.gpsStatus(getContext())) {
                    NavigationActivity.change_title("inSport");
                    startTime = System.currentTimeMillis();
                    startButton.setVisibility(View.GONE);
                    pauseButton.setVisibility(View.VISIBLE);
                    stopButton.setVisibility(View.VISIBLE);
                    timerHandler.postDelayed(timerRunnable, 0);
                    isPause = false;
                    isStart = true;
                    past_seconds = 0;
                    past_hours = 0;
                    past_minutes = 0;
                    distance = 0;
                    distanceText.setText("0 метров");
                    mMap.clear();
                    route = mMap.addPolyline(polylineOptions);
                    old_latitude = 0;
                    old_longitude = 0;
                    maxSpeed = 0;
                    averageSpeed = 0;
                    if (!Utils.checkGPSPermissoins(getActivity())) {
                        // mMap.addCircle(new CircleOptions().center(tempList.get(0)).radius(0.2)
                        //  .fillColor(Color.YELLOW).strokeColor(Color.YELLOW));
                         //TODO исправить, что первая точка появлется лишь после первого изменения положения(при нажатии на кнопку старт первое изменение не учитывается)
                    }

                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Включите GPS")
                            .setMessage("Для корректной работы, включите GPS в настройках")
                            .setCancelable(true)
                            .setNegativeButton("Включить",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                ArrayList<Double> data = new ArrayList<>();
                timerHandler.removeCallbacks(timerRunnable);
                isStart = false;
                isPause = false;
                startButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.GONE);
                pauseButton.setImageResource(R.drawable.ic_pause_button);
                mMap.addCircle(new CircleOptions().center(myLatLng).radius(0.2)
                        .fillColor(Color.RED).strokeColor(Color.RED));
                if (distance > 0 && points != null) {
                    mEditor.putInt(APP_SETTINGS_NUMBER_OF_ROUTES, mSettings.getInt(APP_SETTINGS_NUMBER_OF_ROUTES, 0) + 1);
                    mEditor.apply();
                    for (int i = 0; i < points.size(); i++) {
                        resultPoints.add(points.get(i));
                    }
                    Log.d("DEBUG", "resultPoints"+resultPoints);
                    for (int i = 0; i < resultPoints.size(); i++) {
                        data.add(resultPoints.get(i).latitude);
                        data.add(resultPoints.get(i).longitude);
                    }
                    resultPoints.clear();
                    points.clear();

                    JSONObject main_json = new JSONObject();
                    if (mSettings.getInt(APP_SETTINGS_NUMBER_OF_ROUTES, 0) > 1) {
                        try {
                            main_json = new JSONObject(Utils.read(getActivity(), "routes.json").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONObject fresh_json = new JSONObject();
                    try {

                        fresh_json.put("route", data);
                        fresh_json.put("distance", Utils.roundUp(distance, 0));
                        fresh_json.put("maxSpeed", maxSpeed);
                        fresh_json.put("averageSpeed", averageSpeed);
                        fresh_json.put("time", hours + ":" + minutes + ":" + seconds);
                        main_json.put("" + mSettings.getInt(APP_SETTINGS_NUMBER_OF_ROUTES, 0), fresh_json);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Utils.write(getActivity(), "routes.json", main_json.toString());

                }
            }
        });

        pauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPause) {
                    past_seconds = seconds;
                    past_hours = hours;
                    past_minutes = minutes;
                    timerHandler.removeCallbacks(timerRunnable);
                    pauseButton.setImageResource(R.drawable.ic_start_button);
                    route.setEndCap(new RoundCap());
                    //TODO сделать EndCap
                    if (myLatLng != null)
                        mMap.addCircle(new CircleOptions().center(myLatLng).radius(0.2).fillColor(Color.BLUE).strokeColor(Color.BLUE));
                    for (int i = 0; i < points.size(); i++) {
                        resultPoints.add(points.get(i));
                    }
                }
                if (isPause) {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    pauseButton.setImageResource(R.drawable.ic_pause_button);
                    if (!Utils.checkGPSPermissoins(getActivity())) {
                        ArrayList<LatLng> tempList = new ArrayList<>();
                        tempList.add(myLatLng);
                        route.setPoints(tempList);
                        mMap.addCircle(new CircleOptions().center(tempList.get(0)).radius(0.2).fillColor(Color.BLUE).strokeColor(Color.BLUE));
                    }
                }
                isPause = !isPause;
            }
        });
        hasVisited = mSettings.getBoolean(APP_SETTINGS_ENTRY, false);
        if (!hasVisited) {
            okButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (weightText.getText().length() > 1 && heightText.getText().length() > 2) {
                        float weight = Float.valueOf(weightText.getText().toString());
                        mEditor.putFloat(APP_SETTINGS_WEIGHT, weight);
                        mEditor.apply();
                        float height = Float.valueOf(heightText.getText().toString());
                        mEditor.putFloat(APP_SETTINGS_HEIGHT, height);
                        mEditor.apply();
                        okButton.setVisibility(View.GONE);
                        weightText.setVisibility(View.GONE);
                        heightText.setVisibility(View.GONE);
                        view_medium.setVisibility(View.GONE);
                        view_background.setVisibility(View.GONE);
                        mEditor.putBoolean(APP_SETTINGS_ENTRY, true);
                        mEditor.apply();
                        hasVisited = true;
                        Toast.makeText(getActivity(), "Данные успешно записаны.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Введите действительные данные", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if (hasVisited) {
            okButton.setVisibility(View.GONE);
            weightText.setVisibility(View.GONE);
            heightText.setVisibility(View.GONE);
            view_medium.setVisibility(View.GONE);
            view_background.setVisibility(View.GONE);
        }
        return v;
    }


    private void updateLocation() {
        buildLocationRequest();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        fusedLocationClient.requestLocationUpdates(locationRequest, getPendingIntent());
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(getActivity(), MyLocationService.class);
        intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
        return PendingIntent.getBroadcast(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1000);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        if (!Utils.checkGPSPermissoins(getActivity())) {
            mMap.setPadding(0, 110, 0, 0);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
    }

    @SuppressLint("SetTextI18n")
    public void buildData(Double latitude, Double longitude, Float speed, Float accuracy) {
        if (latitude != old_latitude && longitude != old_longitude) {
            myLatLng = new LatLng(latitude, longitude);
            points = route.getPoints();
            if (isStart && !isPause && accuracy < 50) {
                points.add(myLatLng);
                route.setPoints(points);
                Log.d("DEBUG", "LatLng"+myLatLng);
                Log.d("DEBUG",  "points"+points);
                Log.d("DEBUG", "route"+route.getPoints());
                Log.d("DEBUG", "--------------------");
                int correctSpeed = (Utils.roundUp((float) (speed * 3.6), 1)).intValue();
                if (correctSpeed != 0) speedList.add(correctSpeed);
                if (correctSpeed > maxSpeed) maxSpeed = correctSpeed;
                if (speedType == 0) speedText.setText(correctSpeed + " км/ч");
                if (speedType == 1) speedText.setText(maxSpeed + " км/ч");
                if (speedType == 2) {
                    int sum = 0;
                    for (int i = 0; i < speedList.size(); i++) {
                        sum += speedList.get(i);
                    }
                    averageSpeed = sum / (speedList.size());
                    speedText.setText(averageSpeed + " км/ч");
                }
                float[] results = new float[1];
                if (last_latitude != 0 && last_longitude != 0) {
                    Location.distanceBetween(last_latitude, last_longitude,
                            latitude, longitude,
                            results);
                }
                distance = distance + results[0];
                distanceText.setText(Utils.roundUp(distance, 0) + " метров");
                last_latitude = latitude;
                last_longitude = longitude;

            }
        }
        old_latitude = latitude;
        old_longitude = longitude;
    }



    @Override
    public void onMyLocationClick(@NonNull Location location) {
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


}








