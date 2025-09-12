package com.apc.cng_hpcl.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apc.cng_hpcl.R

class CreateChallanFragment : Fragment() {

    private lateinit var spinnerLCV: Spinner
    private lateinit var spinnerDBS: Spinner
    private lateinit var btnView: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.create_challan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views from fragment layout
        spinnerLCV = view.findViewById(R.id.spinnerLCV)
        spinnerDBS = view.findViewById(R.id.spinnerDBS)
        btnView = view.findViewById(R.id.btnView)

        // Sample data for LCVs
        val lcvList = listOf("Select LCV", "UP80HT5145","UP80HT4943","UP80HT5144","UP80HT5143","UP80HT5142","UP80HT5141","UP80HT5140")

        // Sample data for DBS
        val dbsList = listOf("Select DBS", "Shiv Filling", "Sainik Filling", "J Chambal")

        // Set adapters for LCV Spinner
        val lcvAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lcvList)
        lcvAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLCV.adapter = lcvAdapter

        // Set adapters for DBS Spinner
        val dbsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dbsList)
        dbsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDBS.adapter = dbsAdapter

        // Handle button click
        btnView.setOnClickListener {
            val selectedLCV = spinnerLCV.selectedItem.toString()
            val selectedDBS = spinnerDBS.selectedItem.toString()

            Toast.makeText(
                requireContext(),
                "Selected LCV: $selectedLCV\nSelected DBS: $selectedDBS",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
