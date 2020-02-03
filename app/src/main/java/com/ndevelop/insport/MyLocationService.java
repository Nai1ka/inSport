package com.ndevelop.insport;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.ndevelop.insport.Fragments.MapsFragment;

public class MyLocationService extends BroadcastReceiver {
    public static final String ACTION_PROCESS_UPDATE = " com.ndevelop.googlemapstest.UPDATE_LOCATION";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATE.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {


                    Location location = result.getLastLocation();

                    try {
                        MapsFragment.getInstance().buildData(location.getLatitude(), location.getLongitude(), location.getSpeed(),location.getAccuracy());


                    } catch (Exception ex) {
                    }
                }
            }
        }
    }

}

