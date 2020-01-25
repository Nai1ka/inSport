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
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ndevelop.insport.MyLocationService;
import com.ndevelop.insport.NavigationActivity;
import com.ndevelop.insport.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
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
    private int averageSpeed = 0;
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

    private Handler timerHandler = new Handler();
    private PolylineOptions polylineOptions = new PolylineOptions()
            .color(Color.GREEN)
            .width(8);
    private double old_latitude = 0;
    private double old_longitude = 0;
    private ArrayList<Integer> list_of_speeds = new ArrayList<>();

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
                if (gpsStatus()) NavigationActivity.change_title("inSport");
                if (gpsStatus()) {
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


                if (distance>0 && points != null) {
                    mEditor.putInt(APP_SETTINGS_NUMBER_OF_ROUTES, mSettings.getInt(APP_SETTINGS_NUMBER_OF_ROUTES, 0) + 1);
                    mEditor.apply();

                    for (int i = 0; i < points.size(); i++) {
                        data.add(points.get(i).latitude);
                        data.add(points.get(i).longitude);


                    }
                    points.clear();

                    JSONObject main_json = new JSONObject();
                    if (mSettings.getInt(APP_SETTINGS_NUMBER_OF_ROUTES, 0) > 1) {
                        try {
                            main_json = new JSONObject(read_data(getActivity(), "routes.json").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    JSONObject fresh_json = new JSONObject();
                    try {

                        fresh_json.put("route", data);
                        fresh_json.put("distance", roundUp(distance, 0));
                        fresh_json.put("time", hours + ":" + minutes + ":" + seconds);
                        main_json.put("" + mSettings.getInt(APP_SETTINGS_NUMBER_OF_ROUTES, 0), fresh_json);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    write_data(getActivity(), "routes.json", main_json.toString());

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
                }
                if (isPause) {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    pauseButton.setImageResource(R.drawable.ic_pause_button);
                    route = mMap.addPolyline(polylineOptions);
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
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    12);
        } else {
            mMap.setPadding(0, 110, 0, 0);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMyLocationClickListener(this);
        }
    }

    @SuppressLint("SetTextI18n")
    public void buildData(Double latitude, Double longitude, Float speed, Float accuracy) {

        if (latitude != old_latitude && longitude != old_longitude) {
            LatLng myLatLng = new LatLng(latitude, longitude);

            points = route.getPoints();
            if (isStart && !isPause) {
                if (accuracy < 50) {

                    points.add(myLatLng);
                    route.setPoints(points);
                    int correctSpeed = (roundUp((float) (speed * 3.6), 1)).intValue();
                    list_of_speeds.add(correctSpeed);
                    if (correctSpeed > maxSpeed) maxSpeed = correctSpeed;
                    if (speedType == 0) speedText.setText(correctSpeed + " км/ч");
                    if (speedType == 1) speedText.setText(maxSpeed + " км/ч");
                    if (speedType == 2) {
                        for (int i = 0; i < list_of_speeds.size(); i++) {
                            averageSpeed += list_of_speeds.get(i);
                        }
                        speedText.setText(averageSpeed + " км/ч");
                    }
                    float[] results = new float[1];
                    if (last_latitude != 0 && last_longitude != 0) {
                        Location.distanceBetween(last_latitude, last_longitude,
                                latitude, longitude,
                                results);
                    }
                    distance = distance + results[0];
                    distanceText.setText(roundUp(distance, 0) + " метров");
                    last_latitude = latitude;
                    last_longitude = longitude;

                }
            }
        }
        old_latitude = latitude;
        old_longitude = longitude;
    }

    private static BigDecimal roundUp(float value, int digits) {
        return new BigDecimal("" + value).setScale(digits, BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    private boolean gpsStatus() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean write_data(Context context, String fileName, String jsonString) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }


    }

    private JSONObject read_data(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return new JSONObject(sb.toString());
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}








