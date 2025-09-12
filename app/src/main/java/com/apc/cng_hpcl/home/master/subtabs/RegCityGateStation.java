package com.apc.cng_hpcl.home.master.subtabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.apc.cng_hpcl.R;


public class RegCityGateStation extends Fragment {


    public RegCityGateStation() {
        // Required empty public constructor
    }
    String[] shift = { "06:00AM-02:00PM","02:00PM-10:00PM","10:00PM-06:00AM"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_reg_city_gate_station, container, false);
        Spinner dropdown = root.findViewById(R.id.shift_time);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, shift);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter1);
        return root;
    }
}