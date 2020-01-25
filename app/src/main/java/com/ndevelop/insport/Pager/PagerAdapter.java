package com.ndevelop.insport.Pager;

import android.content.Context;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PagerAdapter extends FragmentPagerAdapter  {
    private Context context = null;
    private GoogleMap mMap;
    public PagerAdapter(Context context, FragmentManager mgr) {
        super(mgr);
        this.context = context;
    }

    @Override
    public int getCount() {
        return (2);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    @Override
    public Fragment getItem(int position) {

            return PagerInfoFragment.newInstance(position);

    }

    @Override
    public String getPageTitle(int position) {
        switch (position) {
            case (0): return "Карта";
            case (1): return  "Информация";

        }
        return ""+position;
    }
}