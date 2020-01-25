package com.ndevelop.insport.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ndevelop.insport.R;


public class CreditFragment extends Fragment {

    static CreditFragment instance;
    public static CreditFragment getInstance() {
        return instance;
    }




    public CreditFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        instance = this;
        View v = inflater.inflate(R.layout.fragment_credit, container, false);
        return v;
    }




}
