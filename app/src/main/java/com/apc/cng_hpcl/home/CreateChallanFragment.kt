package com.apc.cng_hpcl.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.core.content.ContextCompat
import com.apc.cng_hpcl.R
import com.apc.cnghpcl.home.ChallanNumberGenerator
import com.google.android.material.card.MaterialCardView
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.fragment.findNavController

class CreateChallanFragment : Fragment() {

    private lateinit var spinnerLCV: Spinner
    private lateinit var layoutTransactionCheckBoxes: LinearLayout
    private lateinit var spinnerDBS: Spinner
    private lateinit var btnSelectAll: Button
    private lateinit var btnClearAll: Button
    private lateinit var tvSelectedCount: TextView
    private lateinit var cardSummary: MaterialCardView
    private lateinit var tvSummary: TextView
    private lateinit var btnSave: Button
    private lateinit var apiService: ApiService
    private lateinit var challanNumberGenerator: ChallanNumberGenerator

    // Spinner Items
    private val lcvItems = mutableListOf<SpinnerItem>()
    private val dbsItems = mutableListOf<SpinnerItem>()

    // Temporary Data Storage - Store complete API responses
    private var allLcvData = listOf<LcvData>()
    private var allTransactionData = listOf<TransactionData>()
    private var allStationData = listOf<StationData>()

    // Selected Objects - Store currently selected items
    private var selectedLcvData: LcvData? = null
    private var selectedStationData: StationData? = null
    private var mgsStationData: StationData? = null

    // Multiple Transaction Selection
    private val selectedTransactions = mutableListOf<TransactionData>()
    private val selectedTransactionDetails = mutableListOf<TransactionDetailsData>()
    private val transactionCheckBoxes = mutableListOf<CheckBox>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.create_challan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        challanNumberGenerator = ChallanNumberGenerator(requireContext())
        setupRetrofit()
        setupSpinnerListeners()
        setupButtonListeners()

        // Load all data and store temporarily
        loadAndStoreAllData()

        btnSave.setOnClickListener {
            if (validateSelections()) {
                saveMultipleChallans()
            }
        }
    }

    private fun initializeViews(view: View) {
        spinnerLCV = view.findViewById(R.id.spinnerLCV)
        layoutTransactionCheckBoxes = view.findViewById(R.id.layoutTransactionCheckBoxes)
        spinnerDBS = view.findViewById(R.id.spinnerDBS)
        btnSelectAll = view.findViewById(R.id.btnSelectAll)
        btnClearAll = view.findViewById(R.id.btnClearAll)
        tvSelectedCount = view.findViewById(R.id.tvSelectedCount)
        cardSummary = view.findViewById(R.id.cardSummary)
        tvSummary = view.findViewById(R.id.tvSummary)
        btnSave = view.findViewById(R.id.btnView)
    }

    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.cng-suvidha.in/CNGPortal/staging_test_dispenser/dispenser/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    private fun setupButtonListeners() {
        btnSelectAll.setOnClickListener {
            transactionCheckBoxes.forEach { it.isChecked = true }
            updateSelectedTransactions()
        }

        btnClearAll.setOnClickListener {
            transactionCheckBoxes.forEach { it.isChecked = false }
            updateSelectedTransactions()
        }
    }

    // ✅ Create CheckBoxes for Transactions
    private fun createTransactionCheckBoxes() {
        layoutTransactionCheckBoxes.removeAllViews()
        transactionCheckBoxes.clear()

        allTransactionData.forEach { transaction ->
            val checkBox = CheckBox(requireContext()).apply {
                text = "${transaction.transaction_id} (${transaction.gas_transferred}kg)"
                textSize = 12f
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                setPadding(8, 8, 8, 8)

                setOnCheckedChangeListener { _, _ ->
                    updateSelectedTransactions()
                }
            }

            layoutTransactionCheckBoxes.addView(checkBox)
            transactionCheckBoxes.add(checkBox)
        }
    }

    // ✅ Update Selected Transactions
    private fun updateSelectedTransactions() {
        selectedTransactions.clear()
        selectedTransactionDetails.clear()

        transactionCheckBoxes.forEachIndexed { index, checkBox ->
            if (checkBox.isChecked && index < allTransactionData.size) {
                val transaction = allTransactionData[index]
                selectedTransactions.add(transaction)
                // Fetch transaction details for each selected transaction
                fetchTransactionDetails(transaction.transaction_id)
            }
        }

        tvSelectedCount.text = selectedTransactions.size.toString()

        if (selectedTransactions.isEmpty()) {
            cardSummary.visibility = View.GONE
        } else {
            cardSummary.visibility = View.VISIBLE
            updateSummary()
        }
    }

    // ✅ Fetch Transaction Details
    private fun fetchTransactionDetails(transactionId: String) {
        val request = TransactionDetailsRequest(transaction_id = transactionId)

        apiService.getTransactionDetails(request).enqueue(object : Callback<TransactionDetailsResponse> {
            @OptIn(UnstableApi::class)
            override fun onResponse(call: Call<TransactionDetailsResponse>, response: Response<TransactionDetailsResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val detailsResponse = response.body()!!
                    if (detailsResponse.success) {
                        // Add details if not already present
                        if (!selectedTransactionDetails.any { it.transaction_id == transactionId }) {
                            selectedTransactionDetails.add(detailsResponse.data)
                        }
                        updateSummary()
                        Log.d("CreateChallan", "Transaction Details loaded for: $transactionId")
                    } else {
                        Log.e("CreateChallan", "Failed to fetch transaction details: ${detailsResponse.message}")
                    }
                } else {
                    Log.e("CreateChallan", "Server error while fetching transaction details")
                }
            }

            @OptIn(UnstableApi::class)
            override fun onFailure(call: Call<TransactionDetailsResponse>, t: Throwable) {
                Log.e("CreateChallan", "Network error: ${t.message}")
            }
        })
    }

    // ✅ Update Summary
    private fun updateSummary() {
        if (selectedTransactionDetails.isNotEmpty()) {
            var totalGas = 0.0
            var totalAmount = 0.0

            selectedTransactionDetails.forEach { details ->
                val gas = details.gas_transferred.toDoubleOrNull() ?: 0.0
                val rate = details.rate.toDoubleOrNull() ?: 93.5
                totalGas += gas
                totalAmount += (rate * gas)
            }

            val totalTax = totalAmount * 0.18
            val grandTotal = totalAmount + totalTax

            val summaryText = buildString {
                append("Transactions: ${selectedTransactions.size}\n")
                append("Total Gas: ${String.format("%.2f", totalGas)} kg\n")
                append("Amount: ₹${String.format("%.2f", totalAmount)}\n")
                append("Tax (18%): ₹${String.format("%.2f", totalTax)}\n")
                append("Grand Total: ₹${String.format("%.2f", grandTotal)}")
            }

            tvSummary.text = summaryText
        }
    }

    private fun setupSpinnerListeners() {
        // LCV Selection
        spinnerLCV.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val selectedLcvNum = lcvItems[position].hiddenId
                    selectedLcvData = allLcvData.find { it.Lcv_Num == selectedLcvNum }
                    updateSummary()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // DBS Station Selection
        spinnerDBS.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    val selectedStationId = dbsItems[position].hiddenId
                    selectedStationData = allStationData.find { it.Station_Id == selectedStationId }
                    updateSummary()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadAndStoreAllData() {
        loadAndStoreLcvData()
        loadAndStoreTransactionData()
        loadAndStoreStationData()
    }

    // Load LCV data and store temporarily
    private fun loadAndStoreLcvData() {
        apiService.getLcvData().enqueue(object : Callback<LcvResponse> {
            override fun onResponse(call: Call<LcvResponse>, response: Response<LcvResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val lcvResponse = response.body()!!

                    // Store complete data temporarily
                    allLcvData = lcvResponse.data

                    // Populate spinner
                    lcvItems.clear()
                    lcvItems.add(SpinnerItem("Select LCV", ""))

                    allLcvData.forEach { lcv ->
                        lcvItems.add(SpinnerItem("${lcv.Lcv_Num} (${lcv.Cascade_Capacity}L)", lcv.Lcv_Num))
                    }

                    updateLcvSpinner()
                } else {
                    showError("Failed to load LCV data")
                }
            }
            override fun onFailure(call: Call<LcvResponse>, t: Throwable) {
                showError("LCV API Error: ${t.message}")
            }
        })
    }

    // ✅ Load Transaction data and create CheckBoxes
    private fun loadAndStoreTransactionData() {
        apiService.getPendingTransactions().enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val transactionResponse = response.body()!!

                    // Store complete data temporarily
                    allTransactionData = transactionResponse.data

                    // ✅ Create CheckBoxes after data loaded
                    createTransactionCheckBoxes()
                } else {
                    showError("Failed to load transaction data")
                }
            }
            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                showError("Transaction API Error: ${t.message}")
            }
        })
    }

    // Load Station data and store temporarily
    private fun loadAndStoreStationData() {
        apiService.getStationData().enqueue(object : Callback<StationResponse> {
            override fun onResponse(call: Call<StationResponse>, response: Response<StationResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val stationResponse = response.body()!!

                    // Store complete data temporarily
                    allStationData = stationResponse.data

                    // Find and store MGS station (riico)
                    mgsStationData = allStationData.find {
                        it.Station_Id == "riico" && it.Station_type == "Mother Gas Station"
                    }

                    // Filter and populate DBS stations
                    filterAndUpdateDBSStations("riico")

                    if (mgsStationData != null) {
                        Toast.makeText(requireContext(), "MGS Station Found: ${mgsStationData!!.Station_Name}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    showError("Failed to load station data")
                }
            }
            override fun onFailure(call: Call<StationResponse>, t: Throwable) {
                showError("Station API Error: ${t.message}")
            }
        })
    }

    // ✅ Updated - Remove rate from DBS station display
    private fun filterAndUpdateDBSStations(mgsId: String) {
        dbsItems.clear()
        dbsItems.add(SpinnerItem("Select DBS Station", ""))

        val filteredStations = allStationData.filter { station ->
            station.mgsId == mgsId && station.Station_type == "Daughter Booster Station"
        }

        filteredStations.forEach { station ->
            // ✅ Removed rate display
            dbsItems.add(SpinnerItem(station.Station_Name, station.Station_Id))
        }

        updateDBSSpinner()

        Toast.makeText(requireContext(), "Found ${filteredStations.size} DBS stations for $mgsId", Toast.LENGTH_SHORT).show()
    }

    // Update spinners
    private fun updateLcvSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lcvItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLCV.adapter = adapter
    }

    private fun updateDBSSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dbsItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDBS.adapter = adapter
    }

    // ✅ Validate Multiple Selections
    @OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun validateSelections(): Boolean {
        Log.d("CreateChallan", "Validating selections...")

        if (selectedLcvData == null) {
            showError("Please select an LCV")
            return false
        }
        if (selectedTransactions.isEmpty()) {
            showError("Please select at least one transaction")
            return false
        }
        if (selectedStationData == null) {
            showError("Please select a DBS station")
            return false
        }
        if (mgsStationData == null) {
            showError("MGS station (riico) not found in stored data")
            return false
        }
        if (selectedTransactionDetails.size != selectedTransactions.size) {
            showError("Transaction details still loading. Please wait.")
            return false
        }

        Log.d("CreateChallan", "All validations passed")
        return true
    }

    // ✅ Save Multiple Challans
    @OptIn(UnstableApi::class)
    private fun saveMultipleChallans() {
        var successCount = 0
        val totalTransactions = selectedTransactions.size

        selectedTransactions.forEachIndexed { index, transaction ->
            val details = selectedTransactionDetails.find { it.transaction_id == transaction.transaction_id }

            if (details != null) {
                val challanNo = challanNumberGenerator.generateChallanNumber()
                val challanData = createChallanData(challanNo, transaction, details)
                val request = SaveChallanRequest(apicall = "save_challan", data = challanData)

                apiService.saveChallan(request).enqueue(object : Callback<JsonObject> {
                    override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                        if (response.isSuccessful && response.body() != null) {
                            val jsonResponse = response.body()!!
                            val success = jsonResponse.get("success")?.asBoolean ?: false

                            if (success) {
                                successCount++
                                Log.d("CreateChallan", "Challan $successCount/$totalTransactions saved: $challanNo")

                                if (successCount == totalTransactions) {
                                    if (isAdded && context != null) {
                                        Toast.makeText(
                                            requireContext(),
                                            "✅ All $totalTransactions challans saved successfully!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        clearForm()

                                        try {
                                            findNavController().popBackStack()
                                        } catch (e: Exception) {
                                            if (isAdded) parentFragmentManager.popBackStack()
                                        }
                                    }
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                        showError("Failed to save challan ${index + 1}: ${t.message}")
                    }
                })
            }
        }
    }

    // ✅ Create Challan Data for Individual Transaction
    private fun createChallanData(challanNo: String, transaction: TransactionData, details: TransactionDetailsData): ChallanData {
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val mfmBefore = details.start_reading.toDoubleOrNull() ?: 0.0
        val mfmAfter = details.close_reading.toDoubleOrNull() ?: 0.0
        val gasFilled = details.gas_transferred.toDoubleOrNull() ?: 0.0
        val apiRate = details.rate.toDoubleOrNull() ?: 0.0

        val rate = if (apiRate > 0) apiRate else 93.5

        val amount = rate * gasFilled
        val taxAmount = amount * 0.18
        val totalAmount = amount + taxAmount

        val primaryDBSGas = gasFilled
        val secondaryDBSGas = 0.0

        return ChallanData(
            challan_no = challanNo,
            lcv_id = selectedLcvData!!.Lcv_Num,
            cascade_capacity = selectedLcvData!!.Cascade_Capacity,
            station_id = mgsStationData!!.Station_Id,
            dbs_station_id_ref = selectedStationData!!.Station_Id,
            lcv_capacity = selectedLcvData!!.Cascade_Capacity,
            date_reading = currentDateTime,
            before_mfm_reading = String.format("%.2f", mfmBefore),
            after_mfm_reading = String.format("%.2f", mfmAfter),
            filled_gas_kg = String.format("%.2f", gasFilled),
            primary_dbs = String.format("%.2f", primaryDBSGas),
            secondary_dbs = String.format("%.2f", secondaryDBSGas),
            rate = String.format("%.2f", rate),
            amount = String.format("%.2f", amount),
            tax = String.format("%.2f", taxAmount),
            total_amount = String.format("%.2f", totalAmount),
            transaction_id = transaction.transaction_id,

            mgs_station_name = mgsStationData!!.Station_Name,
            mgs_station_id = mgsStationData!!.Station_Id,
            mgs_station_type = mgsStationData!!.Station_type,
            mgs_station_address = mgsStationData!!.Station_Address,
            dbs_station_name = selectedStationData!!.Station_Name,
            dbs_station_id = selectedStationData!!.Station_Id,
            dbs_station_type = selectedStationData!!.Station_type,
            dbs_station_address = selectedStationData!!.Station_Address
        )
    }

    private fun clearForm() {
        spinnerLCV.setSelection(0)
        spinnerDBS.setSelection(0)
        transactionCheckBoxes.forEach { it.isChecked = false }

        selectedLcvData = null
        selectedStationData = null
        selectedTransactions.clear()
        selectedTransactionDetails.clear()

        updateSelectedTransactions()
    }

    private fun showError(message: String) {
        if (isAdded && context != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}