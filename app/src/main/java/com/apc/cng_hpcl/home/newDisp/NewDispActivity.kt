package com.apc.cng_hpcl.home.newDisp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.cng_hpcl.databinding.ActivityNewDispBinding
import com.apc.cng_hpcl.databinding.ActivityNewTransBinding
import com.apc.cng_hpcl.home.newTrans.TransViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewDispActivity : AppCompatActivity() {
    private lateinit var binding:ActivityNewDispBinding
    private lateinit var navController:NavController
    private val transViewModel: DispViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  setContentView(R.layout.activity_new_trans)
        binding= DataBindingUtil.setContentView(this,com.apc.cng_hpcl.R.layout.activity_new_disp)
        navController= Navigation.findNavController(binding.root.findViewById(com.apc.cng_hpcl.R.id.fragment))
        val extras = intent.extras
        if (extras != null) {
            val bundle = Bundle()
            bundle.putString("username",extras.getString("username"))
            bundle.putString("station",extras.getString("station_id"))
            navController.setGraph(com.apc.cng_hpcl.R.navigation.disp_nav_graph,bundle)


        }

    }
}