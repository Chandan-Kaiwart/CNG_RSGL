package com.apc.cng_hpcl.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.cng_hpcl.R
import com.apc.cng_hpcl.databinding.FragCascadeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CascadeFrag: Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private lateinit var binding: FragCascadeBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragCascadeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        val bundle = Bundle()
        binding.cascadeCard.setOnClickListener {
            val stat=binding.statEt.text.toString().trim()
            if(stat.isEmpty()){
                binding.statEt.error="Enter State"
                return@setOnClickListener
            }
            else{
                bundle.putString("username",stat)
                bundle.putString("station",stat)

                bundle.putBoolean("isSch",true)

                //val action=CascadeFragDirections.actionCascadeFragToNewTransActivity()
                navController.navigate(R.id.action_cascadeFrag_to_newTransActivity,bundle)
            }

        }
        binding.lcvCard.setOnClickListener {
            val stat=binding.statEt.text.toString().trim()
            if(stat.isEmpty()){
                binding.statEt.error="Enter State"
                return@setOnClickListener
            }
            else{
                bundle.putString("username",stat)
                bundle.putString("station",stat)
                bundle.putBoolean("isSch",false)
                navController.navigate(R.id.action_cascadeFrag_to_newTransActivity,bundle)
            }

        }
    }
}