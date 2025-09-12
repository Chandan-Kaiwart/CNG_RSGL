package com.apc.cng_hpcl.home.suvidha

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.apc.cng_hpcl.R
import com.apc.cng_hpcl.databinding.ActivitySuvidhaBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() ,NavController.OnDestinationChangedListener{
    private lateinit var navController: NavController
    private lateinit var binding: ActivitySuvidhaBinding
    private lateinit var perms:Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //    setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, com.apc.cng_hpcl.R.layout.activity_suvidha)
        navController =
            Navigation.findNavController(binding.root.findViewById(com.apc.cng_hpcl.R.id.fragment))
        navController= Navigation.findNavController(binding.root.findViewById(R.id.fragment))
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
            Toast.makeText(this,"Granted !",Toast.LENGTH_LONG).show()
            // The user granted the permission.
        } else {
            Toast.makeText(this,"Denied !",Toast.LENGTH_LONG).show()
            finish()

            // The user denied the permission.
        }
    }

}