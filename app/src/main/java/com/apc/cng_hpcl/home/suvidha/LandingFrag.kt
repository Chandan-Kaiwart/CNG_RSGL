package com.apc.cng_hpcl.home.suvidha

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.cng_hpcl.databinding.FragLandingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingFrag:Fragment() {
    private lateinit var mContext: Context
    private lateinit var binding: FragLandingBinding
    private lateinit var navController: NavController
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragLandingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)
        binding.dispPdf.setOnClickListener{
            // Web URL of the PDF
            val pdfUrl = "https://www.cng-suvidha.in/dispenser/docs/Dispenser_user_Manual.pdf"
            // Uri to the PDF
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
            try {
                startActivity(fallbackIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(mContext, "No browser found to open the video", Toast.LENGTH_LONG).show()
            }

        }
        binding.dispRead.setOnClickListener{
            val videoUrl = "https://www.cng-suvidha.in/dispenser/docs/Dispenser_video.mp4"
            // Uri to the video
            // URL of the remote MP4 file

            // Create an intent to open the URL in the default browser (Chrome)
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            try {
                startActivity(fallbackIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(mContext, "No browser found to open the video", Toast.LENGTH_LONG).show()
            }

        }
        binding.nextBt.setOnClickListener {
            val action=LandingFragDirections.actionLandingPageToDispCaptureFrag()
            action.type=1
            action.pid=1.toString()
            action.dispType=1
            navController.navigate(action)
        }
        binding.dispenserSale.setOnClickListener {
            val action=LandingFragDirections.actionLandingPageToDispenserSale()
            navController.navigate(action)
        }
    }
}