package com.apc.cng_hpcl.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.cng_hpcl.BuildConfig
import com.apc.cng_hpcl.R
import com.apc.cng_hpcl.databinding.FragNewDashboardBinding
import com.apc.cng_hpcl.home.newTrans.TransViewModel
import com.google.android.gms.common.wrappers.Wrappers.packageManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewDashFrag: Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private lateinit var binding: FragNewDashboardBinding
    private val vm: TransViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragNewDashboardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)
       // navController.navigate(R.id.action_newDashFrag_to_mainMenuFrag)
        binding.verTv.text = "v${BuildConfig.VERSION_NAME}"
        binding.challanCard.setOnClickListener {
                  val action=NewDashFragDirections.actionNewDashFragToChallanFragment()
            //val action=CascadeFragDirections.actionCascadeFragToNewTransActivity()
                    navController.navigate(action)
        }
        binding.pos.setOnClickListener {
            try {
                // 1. Default way - package name se
                val launchIntent = requireActivity().packageManager.getLaunchIntentForPackage("com.rsgl.cngpos")
                if (launchIntent != null) {
                    startActivity(launchIntent)
                } else {
                    // 2. Backup explicit intent agar default fail kare
                    val intent = Intent()
                    intent.setClassName(
                        "com.rsgl.cngpos",
                        "com.rsgl.cngpos.MainActivity"
                    )
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Toast.makeText(mContext, "React App not installed or cannot be opened", Toast.LENGTH_SHORT).show()
            }
        }

        vm.stationId.observe(viewLifecycleOwner){
        if(it.trim().isNotEmpty()) {

            val station = vm.stationId.value!!
//            binding.titleCard.visibility = View.VISIBLE
//            binding.statTv.setText("Station : $station")


            binding.lcvTracking.setOnClickListener {
            val action=NewDashFragDirections.actionNewDashFragToLcvTracking()
            navController.navigate(action) }
        }
        }

        binding.dispCard.setOnClickListener {
            val action=NewDashFragDirections.actionNewDashFragToDispenserSaleFragment()
            navController.navigate(action)
        }
        binding.gas.setOnClickListener {
            val action=NewDashFragDirections.actionNewDashFragToGasReconciliation()
            navController.navigate(action)

        }
        binding.ctrlRoomLL.setOnClickListener {
           val action= NewDashFragDirections.actionNewDashFragToControlRoomActivity("",vm.stationId.value.toString(),vm.latlng.value.toString())
            navController.navigate(action)
        }
        binding.lcvStatusLL.setOnClickListener {
            val bundle = Bundle()
            val station = vm.stationId.value!!
            bundle.putString("username", station)
            bundle.putString("station", station)

            bundle.putBoolean("isSch", false)
            bundle.putBoolean("isRep", true)


            //val action=CascadeFragDirections.actionCascadeFragToNewTransActivity()
            navController.navigate(R.id.action_newDashFrag_to_newTransActivity, bundle)
            //        navController.navigate(action)

        }
    }
    //Location



}