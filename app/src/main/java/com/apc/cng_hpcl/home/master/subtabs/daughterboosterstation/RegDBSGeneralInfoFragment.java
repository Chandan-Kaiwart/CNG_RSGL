package com.apc.cng_hpcl.home.master.subtabs.daughterboosterstation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.apc.cng_hpcl.R;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegDBSGeneralInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegDBSGeneralInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String[] shift = { "06:00AM-02:00PM","02:00PM-10:00PM","10:00PM-06:00AM"};
    public RegDBSGeneralInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MGSGeneralInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegDBSGeneralInfoFragment newInstance(String param1, String param2) {
        RegDBSGeneralInfoFragment fragment = new RegDBSGeneralInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_reg_d_b_s_general_info, container, false);
        Spinner dropdown = root.findViewById(R.id.shift_time);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, shift);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter1);
        return root;
    }
}