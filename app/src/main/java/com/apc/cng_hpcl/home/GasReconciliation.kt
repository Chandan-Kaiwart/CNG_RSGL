package com.apc.cng_hpcl.home

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apc.cng_hpcl.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GasReconciliation : Fragment() {

    private lateinit var tvInputGas: TextView
    private lateinit var tvOutputGas: TextView
    private lateinit var tvLoss: TextView
    private lateinit var tvBalance: TextView
    private lateinit var pieChart: PieChart
    private lateinit var tvPieChartValues: TextView

    private lateinit var tvStartDate: TextView
    private lateinit var tvEndDate: TextView
    private lateinit var btnFilter: Button

    private var selectedStartDate: String? = null
    private var selectedEndDate: String? = null

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.gas_reconciliation, container, false)

        // Bind Views
        tvInputGas = view.findViewById(R.id.tvInputGas)
        tvOutputGas = view.findViewById(R.id.tvOutputGas)
        tvLoss = view.findViewById(R.id.tvLoss)
        tvBalance = view.findViewById(R.id.tvBalance)
        pieChart = view.findViewById(R.id.pieChart)
        tvPieChartValues = view.findViewById(R.id.tvPieChartValues)

        tvStartDate = view.findViewById(R.id.tvStartDate)
        tvEndDate = view.findViewById(R.id.tvEndDate)
        btnFilter = view.findViewById(R.id.btnFilter)

        // Set default dates to today
        val today = getTodayDate()
        selectedStartDate = today
        selectedEndDate = today
        tvStartDate.text = formatDateForDisplay(today)
        tvEndDate.text = formatDateForDisplay(today)

        // Date pickers
        tvStartDate.setOnClickListener {
            showDatePicker { date ->
                selectedStartDate = date
                tvStartDate.text = formatDateForDisplay(date)
            }
        }

        tvEndDate.setOnClickListener {
            showDatePicker { date ->
                selectedEndDate = date
                tvEndDate.text = formatDateForDisplay(date)
            }
        }

        // Filter button click
        btnFilter.setOnClickListener {
            if (selectedStartDate != null && selectedEndDate != null) {
                fetchGasReconciliationData(selectedStartDate!!, selectedEndDate!!)
            } else {
                Toast.makeText(requireContext(), "Please select both dates", Toast.LENGTH_SHORT).show()
            }
        }

        // Default load with today's date
        fetchGasReconciliationData(today, today)

        return view
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val c = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            // Format: yyyy-MM-dd (API format)
            val date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day)
            onDateSelected(date)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun getTodayDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun formatDateForDisplay(date: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate ?: Date())
        } catch (e: Exception) {
            date
        }
    }

    private fun fetchGasReconciliationData(startDate: String, endDate: String) {
        scope.launch {
            try {
                // Show loading message
                tvInputGas.text = "Loading..."
                tvOutputGas.text = "Loading..."
                tvLoss.text = "Loading..."
                tvBalance.text = "Loading..."
                tvPieChartValues.text = "Loading..."

                val apiData = withContext(Dispatchers.IO) {
                    fetchDataFromAPI(startDate, endDate)
                }

                if (apiData != null && apiData.length() > 0) {
                    // Aggregate all records for the selected date range
                    aggregateAndDisplayData(apiData, startDate, endDate)
                } else {
                    showError("No data found for selected date range")
                    tvInputGas.text = "No data available"
                    tvOutputGas.text = "No data available"
                    tvLoss.text = "No data available"
                    tvBalance.text = "No data available"
                    tvPieChartValues.text = "No data available"
                }
            } catch (e: Exception) {
                showError("Network error: ${e.message}")
                tvInputGas.text = "Error loading data"
                tvOutputGas.text = "Error loading data"
                tvLoss.text = "Error loading data"
                tvBalance.text = "Error loading data"
                tvPieChartValues.text = "Error loading data"
            }
        }
    }

    private suspend fun fetchDataFromAPI(startDate: String, endDate: String): JSONArray? {
        return try {
            val apiUrl = "https://www.cng-suvidha.in/CNGPortal/staging_test_dispenser/dispenser/API/gas_reconcilation_api.php?apicall=get_all_gas_reconcilation"

            android.util.Log.d("GasReconciliation", "API URL: $apiUrl")
            android.util.Log.d("GasReconciliation", "Filtering for: $startDate to $endDate")

            val url = URL(apiUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                android.util.Log.d("GasReconciliation", "API Response received")

                val jsonResponse = JSONObject(response)
                if (!jsonResponse.getBoolean("error")) {
                    val allData = jsonResponse.getJSONArray("data")
                    val filteredData = filterDataByDateRange(allData, startDate, endDate)
                    android.util.Log.d("GasReconciliation", "Total records: ${allData.length()}, Filtered: ${filteredData.length()}")
                    filteredData
                } else {
                    android.util.Log.e("GasReconciliation", "API returned error")
                    null
                }
            } else {
                android.util.Log.e("GasReconciliation", "HTTP Error: ${connection.responseCode}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("GasReconciliation", "Exception: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }

    private fun filterDataByDateRange(allData: JSONArray, startDate: String, endDate: String): JSONArray {
        val filteredArray = JSONArray()

        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)

            if (start == null || end == null) {
                android.util.Log.e("GasReconciliation", "Invalid date format")
                return allData
            }

            for (i in 0 until allData.length()) {
                val record = allData.getJSONObject(i)
                val entryDateStr = record.getString("entry_date")
                val entryDate = dateFormat.parse(entryDateStr)

                if (entryDate != null && !entryDate.before(start) && !entryDate.after(end)) {
                    filteredArray.put(record)
                    android.util.Log.d("GasReconciliation", "Included: $entryDateStr")
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("GasReconciliation", "Error filtering data: ${e.message}")
            return allData
        }

        return filteredArray
    }

    private fun aggregateAndDisplayData(dataArray: JSONArray, startDate: String, endDate: String) {
        try {
            var totalCompressorInlet = 0f
            var totalCompressorVent = 0f
            var totalGasToLcv = 0f
            var totalGasToDispenser = 0f
            var totalGasLoss = 0f
            var recordCount = 0

            android.util.Log.d("GasReconciliation", "Processing ${dataArray.length()} records")

            for (i in 0 until dataArray.length()) {
                val record = dataArray.getJSONObject(i)
                val entryDate = record.getString("entry_date")

                android.util.Log.d("GasReconciliation", "Record $i - Date: $entryDate")

                totalCompressorInlet += record.getString("compressor_inlet").toFloatOrNull() ?: 0f
                totalCompressorVent += record.getString("compressor_vent").toFloatOrNull() ?: 0f
                totalGasToLcv += record.getString("gas_to_lcv").toFloatOrNull() ?: 0f
                totalGasToDispenser += record.getString("gas_to_dispenser").toFloatOrNull() ?: 0f
                totalGasLoss += record.getString("total_gas_loss").toFloatOrNull() ?: 0f
                recordCount++
            }

            val totalInput = totalCompressorInlet
            val totalOutput = totalGasToLcv + totalGasToDispenser + totalCompressorVent
            val actualLoss = totalInput - totalOutput

            val dateRangeText = if (startDate == endDate) {
                "${formatDateForDisplay(startDate)} ($recordCount record)"
            } else {
                "${formatDateForDisplay(startDate)} to ${formatDateForDisplay(endDate)} ($recordCount records)"
            }

            tvInputGas.text = "Total Input: %.2f KG\n$dateRangeText".format(totalInput)
            tvOutputGas.text = "Total Output: %.2f KG".format(totalOutput)
            tvLoss.text = "Loss: %.2f KG (API: %.2f KG)".format(actualLoss, totalGasLoss)
            tvLoss.setTextColor(Color.RED)
            tvBalance.text = "Balance: %.2f KG".format(totalInput - totalOutput)
            tvBalance.setTextColor(Color.BLACK)

            setupPieChartWithAPIData(totalGasToLcv, totalGasToDispenser, totalCompressorVent)

            android.util.Log.d("GasReconciliation", "Total Input: $totalInput, Output: $totalOutput, Loss: $actualLoss")

        } catch (e: Exception) {
            android.util.Log.e("GasReconciliation", "Error in aggregateAndDisplayData: ${e.message}", e)
            showError("Error parsing data: ${e.message}")
        }
    }

    private fun setupPieChartWithAPIData(
        gasToLcv: Float,
        gasToDispenser: Float,
        compressorVent: Float
    ) {
        val entries = ArrayList<PieEntry>()
        val valuesText = StringBuilder()

        if (gasToLcv > 0) {
            entries.add(PieEntry(gasToLcv, "LCV Gas"))
            valuesText.append("• LCV Gas: %.2f KG\n".format(gasToLcv))
        }
        if (gasToDispenser > 0) {
            entries.add(PieEntry(gasToDispenser, "Dispenser Gas"))
            valuesText.append("• Dispenser Gas: %.2f KG\n".format(gasToDispenser))
        }
        if (compressorVent > 0) {
            entries.add(PieEntry(compressorVent, "Compressor Vent"))
            valuesText.append("• Compressor Vent: %.2f KG".format(compressorVent))
        }

        if (entries.isEmpty()) {
            entries.add(PieEntry(1f, "No Data"))
            valuesText.append("No Data Available")
        }

        tvPieChartValues.text = valuesText.toString().trimEnd()

        val dataSet = PieDataSet(entries, "Gas Distribution")
        dataSet.colors = listOf(
            Color.parseColor("#4CAF50"),  // Green for LCV
            Color.parseColor("#2196F3"),  // Blue for Dispenser
            Color.parseColor("#FF9800")   // Orange for Compressor Vent
        )
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE
        dataSet.sliceSpace = 2f

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Gas Distribution\n(KG)"
        pieChart.setCenterTextSize(14f)
        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setEntryLabelTextSize(11f)
        pieChart.setDrawEntryLabels(true)
        pieChart.legend.isEnabled = true
        pieChart.animateY(1000)
        pieChart.invalidate()
    }

    private fun showError(message: String) {
        context?.let { ctx ->
            Toast.makeText(ctx, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
    }
}