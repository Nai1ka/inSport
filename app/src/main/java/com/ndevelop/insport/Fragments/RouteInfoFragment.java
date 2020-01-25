package com.ndevelop.insport.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ndevelop.insport.Pager.PagerAdapter;
import com.ndevelop.insport.R;


public class RouteInfoFragment extends Fragment {



    public RouteInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int recieveInfo = bundle.getInt("numberOfRoute");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_route_info, container, false);
        ViewPager pager= v.findViewById(R.id.Pager);
        pager.setAdapter(new PagerAdapter(getContext(), getActivity().getSupportFragmentManager()));
        pager.getAdapter().notifyDataSetChanged();
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


}
