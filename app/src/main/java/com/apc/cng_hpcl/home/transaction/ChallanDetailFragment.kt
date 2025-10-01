package com.apc.cng_hpcl.home.transaction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.apc.cng_hpcl.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.card.MaterialCardView

class ChallanDetailFragment : Fragment() {

    companion object {
        private const val TAG = "ChallanDetailFragment"
    }

    // UI Components
    private lateinit var progressBar: ProgressBar
    private lateinit var txtChallanNumber: TextView
    private lateinit var txtLcvNumber: TextView
    private lateinit var txtDate: TextView
    private lateinit var txtMgsName: TextView
    private lateinit var txtMgsAddress: TextView
    private lateinit var txtMgsId: TextView
    private lateinit var txtDbsName: TextView
    private lateinit var txtDbsAddress: TextView
    private lateinit var txtDbsId: TextView
    private lateinit var txtDbsType: TextView
    private lateinit var cardSecondaryDbs: MaterialCardView

    // Table Data Components
    private lateinit var dataLcvNumber: TextView
    private lateinit var dataLcvCapacity: TextView
    private lateinit var dataDate: TextView
    private lateinit var dataTime: TextView
    private lateinit var dataMFMBefore: TextView
    private lateinit var dataMFMAfter: TextView
    private lateinit var dataFilledGas: TextView
    private lateinit var dataGasPrimary: TextView
    private lateinit var dataGasSecondary: TextView
    private lateinit var dataRate: TextView
    private lateinit var dataAmount: TextView
    private lateinit var dataTax: TextView
    private lateinit var dataTotalAmount: TextView

    private var challanNumber: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_challan_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            challanNumber = arguments?.getString("challanNumber")

            if (challanNumber.isNullOrEmpty()) {
                showError("Invalid challan number")
                return
            }

            Log.d(TAG, "Received challan number: $challanNumber")

            initializeViews(view)
            scaleAllTextSizes(view, 1.2f)
            displayBundleData()
            fetchChallanDetails()

        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewCreated", e)
            showError("Error initializing view: ${e.message}")
        }
    }

    private fun initializeViews(view: View) {
        try {
            progressBar = view.findViewById(R.id.progressBar)
            txtChallanNumber = view.findViewById(R.id.txtChallanNumber)
            txtLcvNumber = view.findViewById(R.id.txtLcvNumber)
            txtDate = view.findViewById(R.id.txtDate)

            txtMgsName = view.findViewById(R.id.txtMgsName)
            txtMgsAddress = view.findViewById(R.id.txtMgsAddress)
            txtMgsId = view.findViewById(R.id.txtMgsId)

            txtDbsName = view.findViewById(R.id.txtDbsName)
            txtDbsAddress = view.findViewById(R.id.txtDbsAddress)
            txtDbsId = view.findViewById(R.id.txtDbsId)
            txtDbsType = view.findViewById(R.id.txtDbsType)

            cardSecondaryDbs = view.findViewById(R.id.cardSecondaryDbs)

            // Table data initialization
            dataLcvNumber = view.findViewById(R.id.dataLcvNumber)
            dataLcvCapacity = view.findViewById(R.id.dataLcvCapacity)
            dataDate = view.findViewById(R.id.dataDate)
            dataTime = view.findViewById(R.id.dataTime)
            dataMFMBefore = view.findViewById(R.id.dataMFMBefore)
            dataMFMAfter = view.findViewById(R.id.dataMFMAfter)
            dataFilledGas = view.findViewById(R.id.dataFilledGas)
            dataGasPrimary = view.findViewById(R.id.dataGasPrimary)
            dataGasSecondary = view.findViewById(R.id.dataGasSecondary)
            dataRate = view.findViewById(R.id.dataRate)
            dataAmount = view.findViewById(R.id.dataAmount)
            dataTax = view.findViewById(R.id.dataTax)
            dataTotalAmount = view.findViewById(R.id.dataTotalAmount)

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            throw e
        }
    }

    private fun displayBundleData() {
        try {
            arguments?.let { bundle ->
                txtChallanNumber.text = bundle.getString("challanNumber", "N/A")
                txtLcvNumber.text = "LCV Number: ${bundle.getString("lcvNumber", "N/A")}"
                txtDate.text = "Date: ${bundle.getString("date", "N/A")} ${bundle.getString("time", "")}"

                txtMgsName.text = bundle.getString("mgsStation", "N/A")
                txtDbsName.text = bundle.getString("dbsStation", "N/A")

                dataLcvNumber.text = bundle.getString("lcvNumber", "N/A")
                dataDate.text = bundle.getString("date", "N/A")
                dataTime.text = bundle.getString("time", "N/A")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error displaying bundle data", e)
        }
    }

    private fun fetchChallanDetails() {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    makeApiCall(challanNumber!!)
                }

                Log.d(TAG, "API Response: $response")
                if (response != null) {
                    parseChallanDetails(response)
                } else {
                    showError("Could not fetch detailed data. Showing basic info.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching challan details", e)
                showError("Network error. Showing basic info.")
            } finally {
                showLoading(false)
            }
        }
    }

    private suspend fun makeApiCall(challanNumber: String): String? {
        return try {
            val encodedChallanNumber = URLEncoder.encode(challanNumber, "UTF-8")
            val urlString = "https://www.cng-suvidha.in/CNGPortal/staging_test_dispenser/dispenser/API/challan_api.php?apicall=fetch_challan_by_number&challan_no=$encodedChallanNumber"

            Log.d(TAG, "Making API call to: $urlString")
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.doInput = true
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty("Accept", "application/json")

            val responseCode = connection.responseCode
            Log.d(TAG, "Response code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                connection.disconnect()
                response
            } else {
                Log.e(TAG, "Server returned error code: $responseCode")
                connection.disconnect()
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in API call", e)
            null
        }
    }

    private fun parseChallanDetails(response: String) {
        try {
            Log.d(TAG, "Parsing response: $response")
            val jsonObject = JSONObject(response)
            val success = jsonObject.optBoolean("success", false)

            if (success && jsonObject.has("data")) {
                val challanData = jsonObject.getJSONObject("data")
                populateUIWithDetailedData(challanData)
            } else {
                val message = jsonObject.optString("message", "Unknown error")
                showError("API Error: $message")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing challan details", e)
            showError("Error parsing server response")
        }
    }

    private fun populateUIWithDetailedData(data: JSONObject) {
        try {
            Log.d(TAG, "Populating UI with detailed API data")

            // Header Information
            val challanNo = data.optString("challan_no", "N/A")
            val lcvId = data.optString("lcv_id", "N/A")
            val dateReading = data.optString("date_reading", "")

            txtChallanNumber.text = challanNo
            txtLcvNumber.text = "LCV Number: $lcvId"
            txtDate.text = "Date: ${formatDateTime(dateReading)}"

            // MGS Station Information
            val mgsName = data.optString("mgs_station_name", "N/A")
            val mgsAddress = data.optString("mgs_station_address", "N/A")
            val mgsId = data.optString("mgs_station_id", "N/A")
            val mgsType = data.optString("mgs_station_type", "N/A")

            txtMgsName.text = mgsName
            txtMgsAddress.text = mgsAddress
            txtMgsId.text = "Station ID: $mgsId ($mgsType)"

            // DBS Station Information
            val dbsName = data.optString("dbs_station_name", "N/A")
            val dbsAddress = data.optString("dbs_station_address", "N/A")
            val dbsId = data.optString("dbs_station_id_ref", "N/A")
            val dbsType = data.optString("dbs_station_type", "N/A")

            txtDbsName.text = dbsName
            txtDbsAddress.text = dbsAddress
            txtDbsId.text = "Station ID: $dbsId"
            txtDbsType.text = "Type: $dbsType"

            // Hide secondary DBS card (since secondary_dbs is 0.00)
            val secondaryDbs = data.optString("secondary_dbs", "0.00").toDoubleOrNull() ?: 0.0
            cardSecondaryDbs.visibility = if (secondaryDbs > 0) View.VISIBLE else View.GONE

            // Table Data Population
            dataLcvNumber.text = lcvId

            // Capacity
            val cascadeCapacity = data.optString("cascade_capacity", "0")
            val lcvCapacity = data.optString("lcv_capacity", "0")
            dataLcvCapacity.text = "${lcvCapacity} L"

            // Date and Time
            dataDate.text = formatDate(dateReading)
            dataTime.text = formatTime(dateReading)

            // MFM Readings
            val beforeMfm = data.optString("before_mfm_reading", "0")
            val afterMfm = data.optString("after_mfm_reading", "0")
            dataMFMBefore.text = String.format("%.2f", beforeMfm.toDoubleOrNull() ?: 0.0)
            dataMFMAfter.text = String.format("%.2f", afterMfm.toDoubleOrNull() ?: 0.0)

            // Gas Information
            val filledGas = data.optString("filled_gas_kg", "0")
            val primaryDbs = data.optString("primary_dbs", "0")
            val secondaryDbsValue = data.optString("secondary_dbs", "0")

            dataFilledGas.text = String.format("%.2f kg", filledGas.toDoubleOrNull() ?: 0.0)
            dataGasPrimary.text = String.format("%.2f kg", primaryDbs.toDoubleOrNull() ?: 0.0)
            dataGasSecondary.text = String.format("%.2f kg", secondaryDbsValue.toDoubleOrNull() ?: 0.0)

            // Financial Information
            val rate = data.optString("rate", "0")
            val amount = data.optString("amount", "0")
            val tax = data.optString("tax", "0")
            val totalAmount = data.optString("total_amount", "0")

            dataRate.text = "₹${String.format("%.2f", rate.toDoubleOrNull() ?: 0.0)}"
            dataAmount.text = "₹${String.format("%.2f", amount.toDoubleOrNull() ?: 0.0)}"
            dataTax.text = "₹${String.format("%.2f", tax.toDoubleOrNull() ?: 0.0)}"
            dataTotalAmount.text = "₹${String.format("%.2f", totalAmount.toDoubleOrNull() ?: 0.0)}"

            Log.d(TAG, "UI populated successfully with detailed data")
            Toast.makeText(requireContext(), "Challan details loaded successfully", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e(TAG, "Error populating UI with detailed data", e)
            showError("Error displaying detailed challan data")
        }
    }

    private fun scaleAllTextSizes(root: View, scaleFactor: Float) {
        try {
            if (root is TextView) {
                val currentSize = root.textSize / resources.displayMetrics.scaledDensity
                root.textSize = currentSize * scaleFactor
            } else if (root is ViewGroup) {
                for (i in 0 until root.childCount) {
                    scaleAllTextSizes(root.getChildAt(i), scaleFactor)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error scaling text sizes", e)
        }
    }

    private fun formatDateTime(dateTime: String): String {
        return try {
            if (dateTime.isBlank()) return "N/A"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting datetime: $dateTime", e)
            dateTime
        }
    }

    private fun formatDate(dateTime: String): String {
        return try {
            if (dateTime.isBlank()) return "N/A"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting date: $dateTime", e)
            dateTime.split(" ").firstOrNull() ?: "N/A"
        }
    }

    private fun formatTime(dateTime: String): String {
        return try {
            if (dateTime.isBlank()) return "N/A"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting time: $dateTime", e)
            dateTime.split(" ").getOrNull(1)?.substring(0, 5) ?: "N/A"
        }
    }

    private fun showLoading(show: Boolean) {
        try {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
        } catch (e: Exception) {
            Log.e(TAG, "Error setting loading state", e)
        }
    }

    private fun showError(message: String) {
        try {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            Log.e(TAG, "Error: $message")
        } catch (e: Exception) {
            Log.e(TAG, "Error showing error message", e)
        }
    }
}
