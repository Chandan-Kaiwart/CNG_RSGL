package com.apc.cng_hpcl.util;

import com.apc.cng_hpcl.BuildConfig;


public class Constant {
        public static final String Get_Mapping = BuildConfig.BASE_URL+"read_mapping.php?Emp_Id=";
        public static final String Station_List = BuildConfig.BASE_URL+"read_master_data_dropdown.php";

        public static final double molarmass=18.1547;
        public static final String BASE_URL = BuildConfig.BASE_URL;
        public static final String AI_URL_Temp = BASE_URL+"AIintegration/Temperature/";
        public static final String AI_URL_Pressure= BASE_URL+"AIintegration/Pressure/";
//    public static final String BASE_URL = "http://172.17.10.124/LUAG_HPCL/";
//    public static final String BASE_URL = "http://192.168.43.43/LUAG_HPCL/";
//    public static final String BASE_URL = "http://192.168.43.1/LUAG_HPCL/";
//    public static final String BASE_URL = "http://192.168.6.108/LUAG_HPCL/";
//    public static final String BASE_URL = "http://172.17.10.110/LUAG_HPCL/";
}
