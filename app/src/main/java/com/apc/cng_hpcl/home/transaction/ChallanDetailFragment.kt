package com.apc.cng_hpcl.home.transaction

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
import java.text.SimpleDateFormat
import java.util.*

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
    private lateinit var cardSecondaryDbs: LinearLayout
    private lateinit var txtSecondaryDbsName: TextView
    private lateinit var txtSecondaryDbsAddress: TextView
    private lateinit var txtSecondaryDbsId: TextView

    // Table Data Components
    private lateinit var dataLcvNumber: TextView
    private lateinit var dataLcvCapacity: TextView
    private lateinit var dataDate: TextView
    private lateinit var dataTime: TextView
    private lateinit var dataMEMBefore: TextView
    private lateinit var dataMEMAfter: TextView
    private lateinit var dataFilledGas: TextView
    private lateinit var dataGasPrimary: TextView
    private lateinit var dataGasSecondary: TextView
    private lateinit var dataRate: TextView
    private lateinit var dataAmount: TextView
    private lateinit var dataTax: TextView
    private lateinit var dataTotalAmount: TextView

    private var challanId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_challan_description, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get challan ID from arguments
        challanId = arguments?.getString("challanId")

        if (challanId.isNullOrEmpty()) {
            showError("Invalid challan ID")
            return
        }
        scaleAllTextSizes(view, 1.2f)
        initializeViews(view)
        fetchChallanDetails()
    }

    private fun initializeViews(view: View) {
        // Basic UI components
        progressBar = view.findViewById(R.id.progressBar)
        txtChallanNumber = view.findViewById(R.id.txtChallanNumber)
        txtLcvNumber = view.findViewById(R.id.txtLcvNumber)
        txtDate = view.findViewById(R.id.txtDate)

        // MGS Details
        txtMgsName = view.findViewById(R.id.txtMgsName)
        txtMgsAddress = view.findViewById(R.id.txtMgsAddress)
        txtMgsId = view.findViewById(R.id.txtMgsId)

        // Primary DBS Details
        txtDbsName = view.findViewById(R.id.txtDbsName)
        txtDbsAddress = view.findViewById(R.id.txtDbsAddress)
        txtDbsId = view.findViewById(R.id.txtDbsId)
        txtDbsType = view.findViewById(R.id.txtDbsType)

        // Secondary DBS Details
        cardSecondaryDbs = view.findViewById(R.id.cardSecondaryDbs)
        txtSecondaryDbsName = view.findViewById(R.id.txtSecondaryDbsName)
        txtSecondaryDbsAddress = view.findViewById(R.id.txtSecondaryDbsAddress)
        txtSecondaryDbsId = view.findViewById(R.id.txtSecondaryDbsId)

        // Table data
        dataLcvNumber = view.findViewById(R.id.dataLcvNumber)
        dataLcvCapacity = view.findViewById(R.id.dataLcvCapacity)
        dataDate = view.findViewById(R.id.dataDate)
        dataTime = view.findViewById(R.id.dataTime)
        dataMEMBefore = view.findViewById(R.id.dataMEMBefore)
        dataMEMAfter = view.findViewById(R.id.dataMEMAfter)
        dataFilledGas = view.findViewById(R.id.dataFilledGas)
        dataGasPrimary = view.findViewById(R.id.dataGasPrimary)
        dataGasSecondary = view.findViewById(R.id.dataGasSecondary)
        dataRate = view.findViewById(R.id.dataRate)
        dataAmount = view.findViewById(R.id.dataAmount)
        dataTax = view.findViewById(R.id.dataTax)
        dataTotalAmount = view.findViewById(R.id.dataTotalAmount)
    }

    private fun fetchChallanDetails() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    makeApiCall(challanId!!)
                }

                Log.d(TAG, "API Response: $response")

                if (response != null) {
                    parseChallanDetails(response)
                } else {
                    showError("Failed to fetch challan details")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching challan details", e)
                showError("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private suspend fun makeApiCall(challanId: String): String? {
        return try {
            val urlString = "https://www.cng-suvidha.in/CNGPortal/staging_gail/CNG_API/challan_api.php" +
                    "?apicall=challan_data&challan_id=$challanId"

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.doInput = true
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                response
            } else {
                Log.e(TAG, "Server Error Code: ${connection.responseCode}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "API Call Error", e)
            null
        }
    }

    private fun parseChallanDetails(response: String) {
        try {
            val jsonObject = JSONObject(response)

            if (!jsonObject.optBoolean("error", true) && jsonObject.has("data")) {
                val dataArray = jsonObject.getJSONArray("data")

                if (dataArray.length() > 0) {
                    val challanData = dataArray.getJSONObject(0)
                    populateUI(challanData)
                } else {
                    showError("No challan data found")
                }
            } else {
                val message = jsonObject.optString("message", "Unknown error")
                showError("API Error: $message")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing challan details", e)
            showError("Error parsing data: ${e.message}")
        }
    }

    private fun populateUI(data: JSONObject) {
        try {
            // Header Information
            val challanNumber = "RSGL/KOTA/24-25/${data.optString("sl_no", "")}"
            txtChallanNumber.text = challanNumber

            // Basic Information
            val lcvId = data.optString("lcv_id", "N/A")
            val dateReading = data.optString("date_reading", "")

            txtLcvNumber.text = "LCV Number: $lcvId"
            txtDate.text = "Date: ${formatDateTime(dateReading)}"

            // MGS Information
            txtMgsName.text = data.optString("mgs_name", "N/A")
            txtMgsAddress.text = data.optString("mgs_address", "N/A")
            txtMgsId.text = "Station ID: ${data.optString("station_id", "N/A")}"

            // Primary DBS Information
            txtDbsName.text = data.optString("dbs_name", "N/A")
            txtDbsAddress.text = data.optString("dbs_address", "N/A")
            txtDbsId.text = "Station ID: ${data.optString("dbs_station_id", "N/A")}"
            txtDbsType.text = "Type: ${data.optString("dbs_type", "N/A")}"

            // Secondary DBS Information
            val secondaryDbsName = data.optString("sec_dbs_name", "")
            if (!secondaryDbsName.isNullOrEmpty() && secondaryDbsName != "null") {
                cardSecondaryDbs.visibility = View.VISIBLE
                txtSecondaryDbsName.text = secondaryDbsName
                txtSecondaryDbsAddress.text = data.optString("sec_dbs_address", "N/A")
                txtSecondaryDbsId.text = "Station ID: ${data.optString("secondary_dbs_id", "N/A")}"
            } else {
                cardSecondaryDbs.visibility = View.GONE
            }

            // Table Data
            dataLcvNumber.text = lcvId
            dataLcvCapacity.text = "${data.optString("Cascade_Capacity", "0")} WL"
            dataDate.text = formatDate(dateReading)
            dataTime.text = formatTime(dateReading)

            // MEM Readings
            dataMEMBefore.text = data.optString("before_filing_at_mgs_mfm_value_read", "0")
            dataMEMAfter.text = data.optString("after_filling_at_mgs_mfm_value_read", "0")

            // Gas Calculations
            val filledGas = calculateFilledGas(
                data.optString("before_filing_at_mgs_mass_cng", "0"),
                data.optString("after_filling_at_mgs_mass_cng", "0")
            )
            dataFilledGas.text = String.format("%.2f", filledGas)

            val primaryDelivered = calculateDeliveredGas(
                data.optString("before_empty_at_db_mass_cng", "0"),
                data.optString("after_empty_at_dbs_mass_cng", "0")
            )
            dataGasPrimary.text = String.format("%.2f", primaryDelivered)

            val secondaryDelivered = calculateDeliveredGas(
                data.optString("before_empty_at_secondary_dbs_mass_cng", "0"),
                data.optString("after_empty_at_secondary_dbs_mass_cng", "0")
            )
            dataGasSecondary.text = if (secondaryDelivered > 0) String.format("%.2f", secondaryDelivered) else "0.00"

            // Financial Information (placeholder values - adjust based on your business logic)
            val rate = 50.0 // Rs per Kg - you might need to fetch this from another API or calculate
            val totalDelivered = primaryDelivered + secondaryDelivered
            val amount = totalDelivered * rate
            val taxRate = 18.0 // 18% GST
            val totalWithTax = amount + (amount * taxRate / 100)

            dataRate.text = String.format("%.2f", rate)
            dataAmount.text = String.format("%.2f", amount)
            dataTax.text = String.format("%.0f", taxRate)
            dataTotalAmount.text = String.format("%.2f", totalWithTax)

        } catch (e: Exception) {
            Log.e(TAG, "Error populating UI", e)
            showError("Error displaying data: ${e.message}")
        }
    }
    private fun scaleAllTextSizes(root: View, scaleFactor: Float) {
        if (root is TextView) {
            val currentSize = root.textSize / resources.displayMetrics.scaledDensity
            root.textSize = currentSize * scaleFactor
        } else if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                scaleAllTextSizes(root.getChildAt(i), scaleFactor)
            }
        }
    }

    private fun calculateFilledGas(beforeMass: String, afterMass: String): Double {
        return try {
            val before = beforeMass.toDoubleOrNull() ?: 0.0
            val after = afterMass.toDoubleOrNull() ?: 0.0
            Math.max(0.0, after - before)
        } catch (e: Exception) {
            0.0
        }
    }

    private fun calculateDeliveredGas(beforeMass: String, afterMass: String): Double {
        return try {
            val before = beforeMass.toDoubleOrNull() ?: 0.0
            val after = afterMass.toDoubleOrNull() ?: 0.0
            Math.max(0.0, before - after)
        } catch (e: Exception) {
            0.0
        }
    }

    private fun formatDateTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateTime
        }
    }

    private fun formatDate(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateTime.split(" ").firstOrNull() ?: ""
        }
    }

    private fun formatTime(dateTime: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = inputFormat.parse(dateTime)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateTime.split(" ").getOrNull(1)?.substring(0, 5) ?: ""
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        Log.e(TAG, "Error: $message")
    }
}