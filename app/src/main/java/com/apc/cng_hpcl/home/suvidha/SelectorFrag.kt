package com.apc.cng_hpcl.home.suvidha

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.cng_hpcl.R
import com.apc.cng_hpcl.databinding.FragSelectorBinding


class SelectorFrag:Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private lateinit var binding: FragSelectorBinding
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragSelectorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)
        val action=SelectorFragDirections.actionSelectorFragToDispCaptureFrag()
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(mContext,
            R.layout.spinner_item, resources.getStringArray(R.array.shifts))
        binding.dispSpin.adapter=adapter
        binding.dispCard.setOnClickListener {
            action.type=1
            action.dispType=binding.dispSpin.selectedItemPosition+1
            navController.navigate(action)
        }
        binding.priceCard.setOnClickListener {
            action.type=2
            action.dispType=binding.dispSpin.selectedItemPosition+1
            navController.navigate(action)
        }
    }
}