package com.ndevelop.insport.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ndevelop.insport.R;
import com.ndevelop.insport.RecyclerView.RouteAdapter;
import com.ndevelop.insport.RecyclerView.RouteInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class HistoryFragment extends Fragment{


    private SharedPreferences mSettings;
    private static final String APP_SETTINGS = "Settings";
    private static final String APP_SETTINGS_NUMBER_OF_ROUTES = "Routes";


    public HistoryFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        mSettings = this.getActivity().getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);



        return v;

    }

    @Override
    public void onStart() {
        super.onStart();

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ArrayList<RouteInfo> routes = new ArrayList<>();
        int numberOfRoutes = mSettings.getInt(APP_SETTINGS_NUMBER_OF_ROUTES, 0);
        try {
            for (int i = numberOfRoutes; i > 0; i--) {
                JSONObject number_of_route = new JSONObject(read_data(getActivity(), "routes.json").getString(i + ""));
                //String route = number_of_route.getString("route");
                //route = route.substring(1, route.length() - 1);
                int distance = number_of_route.getInt("distance");
                String time = number_of_route.getString("time");
                routes.add(new RouteInfo(distance, time));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RouteAdapter routeAdapter = new RouteAdapter(routes);
        recyclerView.setAdapter(routeAdapter);
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
