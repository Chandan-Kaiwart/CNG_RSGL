package com.apc.cng_hpcl.home.master.subtabs.daughterboosterstation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.apc.cng_hpcl.R;

import java.util.Calendar;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegDBSEquipmentInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegDBSEquipmentInfoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegDBSEquipmentInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MGSEquipmentInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegDBSEquipmentInfoFragment newInstance(String param1, String param2) {
        RegDBSEquipmentInfoFragment fragment = new RegDBSEquipmentInfoFragment();
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
    private int year, month, day, hour, minute;
//    TextView tv_date_hydro, tv_time_hydro,tv_date_install,tv_time_install;
//    ImageView date_hydro, time_hydro,date_install,time_install;

    Button date_status,date_install;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root=  inflater.inflate(R.layout.fragment_reg_d_b_s_equipment_info, container, false);
//        tv_date_hydro = BASE_URL.findViewById(R.id.tv_date_hydro);
//        tv_time_hydro = BASE_URL.findViewById(R.id.tv_time_hydro);
//        date_hydro = BASE_URL.findViewById(R.id.date_hydro);
//        time_hydro = BASE_URL.findViewById(R.id.time_hydro);
//        tv_date_install = BASE_URL.findViewById(R.id.tv_date_install);
//        tv_time_install = BASE_URL.findViewById(R.id.tv_time_install);
        date_install = root.findViewById(R.id.date_install);
        date_status=root.findViewById(R.id.date_status);
//        time_install = BASE_URL.findViewById(R.id.time_install);
        date_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date_status.setText(day+"/"+(month+1)+"/"+year);
                    }
                }, year, month,day);
                picker.show();
            }
        });

//        date_status.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                final Calendar c = Calendar.getInstance();
//                hour = c.get(Calendar.HOUR_OF_DAY);
//                minute = c.get(Calendar.MINUTE);
//
//                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener()
//                {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
//                        date_status.setText(hour+":"+min);
//                    }
//                },hour,minute,false);
//                timePickerDialog.show();
//            }
//        });


        date_install.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date_status.setText(day+"/"+(month+1)+"/"+year);
                    }
                }, year, month,day);
                picker.show();
            }
        });

//        time_install.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                final Calendar c = Calendar.getInstance();
//                hour = c.get(Calendar.HOUR_OF_DAY);
//                minute = c.get(Calendar.MINUTE);
//
//                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener()
//                {
//                    @Override
//                    public void onTimeSet(TimePicker timePicker, int hour, int min) {
//                        tv_time_install.setText(hour+":"+min);
//                    }
//                },hour,minute,false);
//                timePickerDialog.show();
//            }
//        });

        return root;
    }
}