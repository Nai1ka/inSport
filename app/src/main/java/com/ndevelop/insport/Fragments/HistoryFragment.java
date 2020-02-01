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
import com.ndevelop.insport.Utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class HistoryFragment extends Fragment {


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
                JSONObject number_of_route = new JSONObject(Utils.read(getActivity(), "routes.json").getString(i + ""));
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


}
