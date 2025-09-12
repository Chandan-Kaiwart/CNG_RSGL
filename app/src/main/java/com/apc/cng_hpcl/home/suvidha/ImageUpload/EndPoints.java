package com.apc.cng_hpcl.home.suvidha.ImageUpload;

/**
 * Created by Belal on 10/24/2017.
 */

public class EndPoints {
    private static final String ROOT_URL = "https://cng-suvidha.in/";
    //  public static final String UPLOAD_URL_GAS_METER= ROOT_URL + "/Adani_gas_api_v2.php";
    public static final String UPLOAD_URL_GAS_METER="https://www.cng-suvidha.in/CLK2validate/meter/iocl_gas_api_v5.php";

    public static final String UPLOAD_URL_PRESSURE_GENERAL = ROOT_URL + "/instru/Pressure/pressure_general_api.php";
    public static final String UPLOAD_URL_PRESSURE_ITECH = ROOT_URL + "/instru/Pressure/pressure_itech_api.php";
    public static final String UPLOAD_URL_PRESSURE_RADIX = ROOT_URL + "/instru/Pressure/pressure_radix_api.php";
    public static final String UPLOAD_URL_PRESSURE_WIKA = ROOT_URL + "/instru/Pressure/pressure_wika_api.php";
    public static final String UPLOAD_URL_TEMP_WIKA = ROOT_URL + "/instru/Temperature/temp_wika_api.php";
    public static final String UPLOAD_URL_TEMP_RADIX = ROOT_URL + "/instru/Temperature/temp_radix_api.php";
    public static final String UPLOAD_URL_TEMP_BAUMER = ROOT_URL + "/instru/Temperature/temp_baumer_api.php";

    public static final String UPLOAD_URL_PIPELINE = "https://www.cng-suvidha.in/CLK2validate/BOM/pipe_api.php";
    public static final String UPLOAD_URL_WATER_METER = ROOT_URL + "/instru/water/water_meter_api.php";
    public static final String UPLOAD_URL_MODEL_CHOOSER = ROOT_URL + "/instru/Pressure/pressure_general_api.php";

    // public static final String GET_PICS_URL = ROOT_URL + "getpics";
    public static final String UPLOAD_URL = ROOT_URL + "BGL/api/api.php";
}
