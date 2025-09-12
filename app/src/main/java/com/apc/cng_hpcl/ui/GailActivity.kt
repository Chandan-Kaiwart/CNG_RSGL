package com.apc.cng_hpcl.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.BuildConfig.BASE_URL
import com.apc.cng_hpcl.R
import com.apc.cng_hpcl.databinding.ActivityGailBinding
import com.apc.cng_hpcl.home.newTrans.TransViewModel
import com.apc.cng_hpcl.home.suvidha.PermissionHelper
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class GailActivity: AppCompatActivity() , NavController.OnDestinationChangedListener{
    private lateinit var navController: NavController
    private lateinit var binding: ActivityGailBinding
    private lateinit var perms:Array<String>
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var latitude: String? = ""
    private var longitude: String? = ""
    private val vm: TransViewModel by viewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_gail)
        navController= Navigation.findNavController(binding.root.findViewById(R.id.fragment))
        navController.setGraph(com.apc.cng_hpcl.R.navigation.gail_nav_graph)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        val permissionHelper = PermissionHelper(this)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            perms=arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
            )
        }
        else{
            perms=arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
            )
        }

        if (!permissionHelper.checkPermissions(perms)) {
            permissionHelper.requestPermissions(perms, 100)
        }
        else{
            lastLocation
        }
        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        title=destination.label


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Granted !", Toast.LENGTH_LONG).show()
            lastLocation
            // The user granted the permission.
        } else {
            Toast.makeText(this,"Denied !", Toast.LENGTH_LONG).show()
            finish()

            // The user denied the permission.
        }
    }

    @get:SuppressLint("MissingPermission")
    private val lastLocation: Unit
        private get() {
            // check if permissions are given

            // check if location is enabled
            if (isLocationEnabled) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient!!.lastLocation.addOnCompleteListener { task ->
                    val location = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        Log.d("LAT>>>", location.latitude.toString())
                        Log.d("LONG>>>", location.longitude.toString())
                        getCurrentStation(latitude.toString(),longitude.toString())

                        //  latitudeTextView.setText(location.getLatitude() + "");
                        //longitTextView.setText(location.getLongitude() + "");
                    }
                }
            } else {

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                //   startActivity(intent)

                val locationRequest = LocationRequest.create().apply {
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                    interval = 10000
                    fastestInterval = 5000


                }
                val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
                val locationSettingsRequest = builder.build()
                val settingsClient: SettingsClient = LocationServices.getSettingsClient(this)
                val task = settingsClient.checkLocationSettings(locationSettingsRequest)


                task.addOnSuccessListener(this, OnSuccessListener<LocationSettingsResponse> { response ->
                    Log.d("LOC>>","All location settings are satisfied.")
                    lastLocation
                    // All location settings are satisfied. The client can initialize location requests here.
                })

                task.addOnFailureListener(this, OnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        // Location settings are not satisfied, but this can be fixed by showing the user a dialog.
                        try {
                            exception.startResolutionForResult(this, 1001)
                        } catch (sendEx: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    }
                })
            }
        }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            if (mLastLocation != null) {
                getCurrentStation(mLastLocation.latitude.toString(),mLastLocation.longitude.toString())
            }
            //  latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            //longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    }


    // method to request for permissions

    // method to check
    // if location is enabled
    private val isLocationEnabled: Boolean
        private get() {
            val locationManager = getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }


    private fun getCurrentStation(lat:String,long:String) {
        val s = BASE_URL+"v2/app/get_current_station.php?lat=$lat&long=$long"
        val jsonObjectRequest: StringRequest = object : StringRequest(
            Method.GET, s,
            Response.Listener { resp ->
                try {
                    Log.d("RES>>", resp.toString())
                    val jsonObject = JSONObject(resp)
                    val status = jsonObject.getString("status")
                    if(status.equals("success")){
                        val json2 = jsonObject.getJSONObject("data")
                        val json3 = json2.getJSONObject("closest_coordinate")
                        val station=json3.getString("Station_Name")
                        val id=json3.getString("Station_Id")
                        vm.stationId.value=id
                        vm.latlng.value= "$lat,$long"


                        //      val sharedPreferences =
                        //              mContext.getSharedPreferences("login", Context.MODE_PRIVATE)
                        //     val myEdit = sharedPreferences.edit()
                        //   myEdit.putString("station", station)
                        //myEdit.apply()
                    }


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
            2,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.setShouldCache(false)
        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    lastLocation
                    // All required changes were successfully made
                }
                Activity.RESULT_CANCELED -> {
                    // The user was asked to change settings, but chose not to
                }
                else -> {
                }
            }
        }
    }
}