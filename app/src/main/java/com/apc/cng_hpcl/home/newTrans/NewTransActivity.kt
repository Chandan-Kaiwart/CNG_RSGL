package com.apc.cng_hpcl.home.newTrans

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import com.apc.cng_hpcl.R
import com.apc.cng_hpcl.databinding.ActivityNewTransBinding
import com.apc.cng_hpcl.home.suvidha.PermissionHelper
import com.apc.cng_hpcl.ui.GailActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewTransActivity : AppCompatActivity(),NavController.OnDestinationChangedListener {
    private lateinit var binding:ActivityNewTransBinding
    private lateinit var navController:NavController
    private lateinit var perms: Array<String>
    private val transViewModel: TransViewModel by viewModels()
    private  var station:String=""
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // first parameter is the file for icon and second one is menu
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // We are using switch case because multiple icons can be kept
        if (item.itemId == R.id.action_logout) {
            val sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
            val myEdit = sharedPreferences.edit()
            myEdit.putString("username", "")
            myEdit.putString("station", "")
            myEdit.putString("lcv", "")

            myEdit.putBoolean("isDbs", false)
            myEdit.putBoolean("isLoggedIn", false)
            myEdit.apply()
            val intent = Intent(
                this@NewTransActivity,
                GailActivity::class.java,
            )
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
            //            moveTaskToBack(true);
//            Process.killProcess(Process.myPid());
//            System.exit(1);
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_new_trans)
         binding= DataBindingUtil.setContentView(this,com.apc.cng_hpcl.R.layout.activity_new_trans)
        navController= Navigation.findNavController(binding.root.findViewById(com.apc.cng_hpcl.R.id.fragment))
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
        val extras = intent.extras
        if (extras != null) {
            station= extras.getString("username").toString()
            val bundle = Bundle()
            bundle.putString("op_id",extras.getString("username"))
            val isSch=extras.getBoolean("isSch")
            val isRep=extras.getBoolean("isRep")


  if(isRep){
      bundle.putString("station",extras.getString("station"))
                navController.setGraph(com.apc.cng_hpcl.R.navigation.sch_report_nav_graph,bundle)

            }
            else if(!isSch){
                navController.setGraph(com.apc.cng_hpcl.R.navigation.trans_nav_graph,bundle)

            }
else{

                bundle.putString("lcv","def")
                bundle.putString("transId","def")
                bundle.putString("dbs_id",extras.getString("station"))
                navController.setGraph(com.apc.cng_hpcl.R.navigation.scheduling_nav_graph,bundle)

            }

        }
        navController.addOnDestinationChangedListener(this)

    }
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        title="${destination.label}"
        supportActionBar?.subtitle="Station : $station"


    }
}