package com.apc.cng_hpcl.home.controlRoom

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
import com.apc.cng_hpcl.R
import com.apc.cng_hpcl.databinding.ActivityControlRoomBinding
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
class ControlRoomActivity: AppCompatActivity() , NavController.OnDestinationChangedListener{
    private lateinit var navController: NavController
    private lateinit var binding: ActivityControlRoomBinding
    private lateinit var perms:Array<String>
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val vm: ControlRoomViewModel by viewModels()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_control_room)
        navController= Navigation.findNavController(binding.root.findViewById(R.id.fragment))
        val extras = intent.extras
        if (extras != null) {
            val station = extras.getString("station").toString()
            val lcv = extras.getString("lcv").toString()
            val latlng = extras.getString("latlng").toString()
            val type = extras.getInt("type")

            val bundle = Bundle()
            bundle.putInt("type", type)
            bundle.putString("station", station)
            bundle.putString("lcv", lcv)
            bundle.putString("latlng",latlng)
            navController.setGraph(R.navigation.control_room_graph,bundle)

        }
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


        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        title=destination.label


    }












}