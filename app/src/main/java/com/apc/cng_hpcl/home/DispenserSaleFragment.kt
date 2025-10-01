package com.apc.cng_hpcl.home

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.apc.cng_hpcl.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.GZIPInputStream
import java.io.ByteArrayInputStream

class DispenserSaleFragment : Fragment() {

    private lateinit var etFromDate: EditText
    private lateinit var etToDate: EditText
    private lateinit var spinnerStation: Spinner
    private lateinit var spinnerDispenser: Spinner // NEW: Dispenser filter spinner
    private lateinit var radioGroup: RadioGroup
    private lateinit var llChartsContainer: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView

    // Chart views
    private lateinit var salesLineChart: LineChart
    private lateinit var shiftBarChart: BarChart
    private lateinit var dispenserPieChart: PieChart
    private lateinit var dailySalesBarChart: BarChart

    // Statistics views
    private lateinit var tvTotalSales: TextView
    private lateinit var tvAvgDaily: TextView
    private lateinit var tvBestShift: TextView

    // Hardcoded values
    private val companyId = "rsgl"
    private val gaId = "Kota"

    // All available stations
    private val stationList = arrayOf(
        "riico"
    )

    // Available dispensers - update based on your API response
    private val dispenserList = arrayOf(
        "Dispenser1", "Dispenser2", "Dispenser3", "Dispenser4"
    )

    // Data storage for charts
    private val salesData = mutableListOf<SalesDataPoint>()
    private val shiftData = mutableMapOf<String, Double>()
    private val dispenserData = mutableMapOf<String, Double>()

    // Map slot number to shift based on hourly slots
// Slots are hourly starting from 06:00 (slot1)
// slot1 = 06:00-07:00, slot2 = 07:00-08:00, etc.
// 8 slots = 1 shift
    private fun getShiftFromSlot(slotKey: String): String {
        val slotNumber = try {
            slotKey.replace("slot", "").toInt()
        } catch (e: Exception) {
            return "Morning (8AM-4PM)" // Default
        }

        // slot1 starts at 06:00
        // slot1-slot2 = 06:00-08:00 (Night shift tail)
        // slot3-slot10 = 08:00-16:00 (Morning shift - 8 hours)
        // slot11-slot18 = 16:00-00:00 (Evening shift - 8 hours)
        // slot19-slot24 + slot1-slot2 = 00:00-08:00 (Night shift - 8 hours)

        return when (slotNumber) {
            in 3..10 -> "Morning (8AM-4PM)"      // slot3-slot10: 08:00-16:00
            in 11..18 -> "Evening (4PM-12AM)"    // slot11-slot18: 16:00-00:00
            in 19..24, in 1..2 -> "Night (12AM-8AM)"  // slot19-24 + slot1-2: 00:00-08:00
            else -> "Morning (8AM-4PM)"          // Default
        }
    }
    // Updated: Slot to Shift mapping - removed slot0, renamed slot3 to Night
    // Updated: Slot to Shift mapping - removed slot0, renamed slot3 to Night
    private val slotToShiftMap = mapOf(
        "slot1" to "Morning (8AM-4PM)",
        "slot2" to "Evening (4PM-12AM)",
        "slot3" to "Night (12AM-8AM)"
    )

    data class SalesDataPoint(
        val date: String,
        val shift: String,
        val dispenser: String,
        val nozzle: String,
        val sale: Double,
        val reading: String,
        val dateTime: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dispenser_sale, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupSpinners()
        setupDatePickers()
        setupCharts()

        // Auto-load data on fragment creation
        loadDataForAllStations()

        // Set up date change listeners to auto-reload data
        etFromDate.setOnClickListener {
            showDatePicker(etFromDate) { loadDataForAllStations() }
        }
        etToDate.setOnClickListener {
            showDatePicker(etToDate) { loadDataForAllStations() }
        }

        // Set up station spinner change listener
        spinnerStation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) { // Skip "All Stations" option
                    loadDataForSelectedStation()
                } else {
                    loadDataForAllStations()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // NEW: Set up dispenser spinner change listener
        spinnerDispenser.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateChartsWithFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initializeViews(view: View) {
        etFromDate = view.findViewById(R.id.etFromDate)
        etToDate = view.findViewById(R.id.etToDate)
        spinnerStation = view.findViewById(R.id.spinnerStation)
        spinnerDispenser = view.findViewById(R.id.spinnerDispenser) // NEW: Initialize dispenser spinner
        llChartsContainer = view.findViewById(R.id.llChartsContainer)
        progressBar = view.findViewById(R.id.progressBar)
        tvStatus = view.findViewById(R.id.tvStatus)

        // Initialize chart views
        salesLineChart = view.findViewById(R.id.salesLineChart)
        shiftBarChart = view.findViewById(R.id.shiftBarChart)
        dispenserPieChart = view.findViewById(R.id.dispenserPieChart)
        dailySalesBarChart = view.findViewById(R.id.dailySalesBarChart)

        // Initialize statistics views
        tvTotalSales = view.findViewById(R.id.tvTotalSales)
        tvAvgDaily = view.findViewById(R.id.tvAvgDaily)
        tvBestShift = view.findViewById(R.id.tvBestShift)
    }

    private fun setupSpinners() {
        // Station spinner
        val stationAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("All Stations") + stationList
        )
        stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStation.adapter = stationAdapter

        // NEW: Dispenser spinner
        val dispenserAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("All Dispensers") + dispenserList
        )
        dispenserAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDispenser.adapter = dispenserAdapter
    }

    private fun setupDatePickers() {
        // Set default dates to today
        val currentDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = dateFormat.format(currentDate.time)
        etFromDate.setText(today)
        etToDate.setText(today)
    }

    private fun showDatePicker(editText: EditText, onDateSelected: (() -> Unit)? = null) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, y, m, d ->
            val mm = String.format("%02d", m + 1)
            val dd = String.format("%02d", d)
            editText.setText("$dd-$mm-$y")
            onDateSelected?.invoke()
        }, year, month, day).show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadDataForAllStations() {
        val startDate = formatDateForApi(etFromDate.text.toString())
        val endDate = formatDateForApi(etToDate.text.toString())

        if (startDate == null || endDate == null) {
            showError("Please select valid dates")
            return
        }

        showLoading(true)
        clearData()

        var completedStations = 0
        val totalStations = stationList.size

        for (station in stationList) {
            if (startDate == endDate) {
                fetchSalesResult(gaId, startDate, endDate, companyId, station) { summaryJson ->
                    completedStations++

                    if (!summaryJson.optBoolean("error", true) &&
                        summaryJson.optBoolean("data_available", false)) {
                        val data = summaryJson.optJSONObject("data")
                        if (data != null) {
                            processJsonData(data, startDate, station)
                        }
                    }

                    if (completedStations == totalStations) {
                        activity?.runOnUiThread {
                            showLoading(false)
                            if (salesData.isNotEmpty() || shiftData.isNotEmpty() || dispenserData.isNotEmpty()) {
                                updateChartsWithFilter()
                            } else {
                                showError("No data found for any station")
                            }
                        }
                    }
                }
            } else {
                fetchDateRangeDataForStation(gaId, startDate, endDate, companyId, station) {
                    completedStations++
                    if (completedStations == totalStations) {
                        activity?.runOnUiThread {
                            showLoading(false)
                            if (salesData.isNotEmpty() || shiftData.isNotEmpty() || dispenserData.isNotEmpty()) {
                                updateChartsWithFilter()
                            } else {
                                showError("No data found for any station")
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadDataForSelectedStation() {
        val selectedPosition = spinnerStation.selectedItemPosition
        if (selectedPosition <= 0) return // Skip if "All Stations" or invalid

        val selectedStation = stationList[selectedPosition - 1]
        val startDate = formatDateForApi(etFromDate.text.toString())
        val endDate = formatDateForApi(etToDate.text.toString())

        if (startDate == null || endDate == null) {
            showError("Please select valid dates")
            return
        }

        showLoading(true)
        clearData()

        if (startDate == endDate) {
            fetchSalesResult(gaId, startDate, endDate, companyId, selectedStation) { summaryJson ->
                activity?.runOnUiThread {
                    processAndDisplayGraphs(summaryJson, startDate, selectedStation)
                }
            }
        } else {
            fetchDateRangeDataForStation(gaId, startDate, endDate, companyId, selectedStation) {
                activity?.runOnUiThread {
                    showLoading(false)
                    if (salesData.isNotEmpty() || shiftData.isNotEmpty() || dispenserData.isNotEmpty()) {
                        updateChartsWithFilter()
                    } else {
                        showError("No data found for selected station")
                    }
                }
            }
        }
    }

    // NEW: Function to update charts with dispenser filter
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateChartsWithFilter() {
        val selectedDispenserPosition = spinnerDispenser.selectedItemPosition
        val selectedDispenser = if (selectedDispenserPosition > 0) {
            dispenserList[selectedDispenserPosition - 1]
        } else null

        Log.d("DispenserSalesGraph", "Updating charts with filter - Selected dispenser: $selectedDispenser")

        // Filter data based on selected dispenser
        val filteredSalesData = if (selectedDispenser != null) {
            salesData.filter { it.dispenser == selectedDispenser }
        } else {
            salesData
        }

        val filteredShiftData = if (selectedDispenser != null) {
            // Recalculate shift data for selected dispenser only
            val tempShiftData = mutableMapOf<String, Double>()
            filteredSalesData.forEach { dataPoint ->
                tempShiftData[dataPoint.shift] = tempShiftData.getOrDefault(dataPoint.shift, 0.0) + dataPoint.sale
            }

            // IMPORTANT: Always include all 3 shifts, even with 0 values
            val allShifts = listOf(
                "Morning (8AM-4PM)",
                "Evening (4PM-12AM)",
                "Extra (12AM-8AM)"
            )
            allShifts.forEach { shift ->
                if (!tempShiftData.containsKey(shift)) {
                    tempShiftData[shift] = 0.0
                }
            }
            tempShiftData
        } else {
            // For "All Dispensers", ensure all 3 shifts are represented
            val tempShiftData = shiftData.toMutableMap()
            val allShifts = listOf(
                "Morning (8AM-4PM)",
                "Evening (4PM-12AM)",
                "Night (12AM-8AM)"
            )
            allShifts.forEach { shift ->
                if (!tempShiftData.containsKey(shift)) {
                    tempShiftData[shift] = 0.0
                }
            }
            tempShiftData
        }

        val filteredDispenserData = if (selectedDispenser != null) {
            // For individual dispenser selection, recalculate from filtered sales data
            val tempDispenserData = mutableMapOf<String, Double>()
            filteredSalesData.forEach { dataPoint ->
                tempDispenserData[dataPoint.dispenser] = tempDispenserData.getOrDefault(dataPoint.dispenser, 0.0) + dataPoint.sale
            }
            tempDispenserData
        } else {
            dispenserData
        }

        Log.d("DispenserSalesGraph", "Filtered data - Sales: ${filteredSalesData.size}, Dispensers: ${filteredDispenserData}")
        updateChartsWithData(filteredSalesData, filteredShiftData, filteredDispenserData)
    }

    // NEW: Function to update charts with specific data
    private fun updateChartsWithData(
        salesData: List<SalesDataPoint>,
        shiftData: Map<String, Double>,
        dispenserData: Map<String, Double>
    ) {
        Log.d("DispenserSalesGraph", "Updating charts with filtered data...")
        updateStatisticsWithData(salesData, shiftData)
        updateLineChartWithData(salesData)
        updateBarChartWithData(shiftData)
        updatePieChartWithData(dispenserData)
        updateDailySalesChartWithData(salesData)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun fetchDateRangeDataForStation(
        gaId: String,
        startDate: String,
        endDate: String,
        companyId: String,
        stationId: String,
        onComplete: () -> Unit
    ) {
        val dateList = generateDateRange(startDate, endDate)
        var completedCalls = 0
        val totalCalls = dateList.size

        for (date in dateList) {
            fetchSalesResult(gaId, date, date, companyId, stationId) { summaryJson ->
                completedCalls++

                if (!summaryJson.optBoolean("error", true) &&
                    summaryJson.optBoolean("data_available", false)) {
                    val data = summaryJson.optJSONObject("data")
                    if (data != null) {
                        processJsonData(data, date, stationId)
                    }
                }

                if (completedCalls == totalCalls) {
                    onComplete()
                }
            }
        }
    }

    private fun setupCharts() {
        setupLineChart()
        setupBarChart()
        setupPieChart()
        setupDailySalesChart()
    }

    private fun setupLineChart() {
        salesLineChart.apply {
            description = Description().apply {
                text = "Sales Trend Over Time"
                textSize = 12f
                textColor = Color.BLACK
            }
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setBackgroundColor(Color.WHITE)

            // Enable data display
            setDrawGridBackground(false)
            isHighlightPerDragEnabled = true

            legend.apply {
                isEnabled = true
                form = Legend.LegendForm.LINE
                textSize = 12f
                textColor = Color.BLACK
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                granularity = 1f
                textSize = 10f
                textColor = Color.BLACK
                setAvoidFirstLastClipping(true)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                textSize = 10f
                textColor = Color.BLACK
                axisMinimum = 0f
                setDrawZeroLine(true)
                zeroLineColor = Color.BLACK
            }

            axisRight.isEnabled = false
            invalidate()
        }
    }

    private fun setupBarChart() {
        shiftBarChart.apply {
            description = Description().apply {
                text = "Sales by Shift"
                textSize = 12f
                textColor = Color.BLACK
            }
            setTouchEnabled(true)
            setBackgroundColor(Color.WHITE)
            setDrawGridBackground(false)

            legend.apply {
                isEnabled = true
                textSize = 12f
                textColor = Color.BLACK
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textSize = 12f
                textColor = Color.BLACK
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                textSize = 10f
                textColor = Color.BLACK
                axisMinimum = 0f
                setDrawZeroLine(true)
            }

            axisRight.isEnabled = false
            invalidate()
        }
    }

    private fun setupPieChart() {
        dispenserPieChart.apply {
            description = Description().apply {
                text = "Sales Distribution by Dispenser"
                textSize = 12f
                textColor = Color.BLACK
            }
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            setBackgroundColor(Color.WHITE)

            // Configure hole
            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)
            holeRadius = 40f
            transparentCircleRadius = 45f

            legend.apply {
                isEnabled = true
                orientation = Legend.LegendOrientation.VERTICAL
                verticalAlignment = Legend.LegendVerticalAlignment.CENTER
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                textSize = 12f
                textColor = Color.BLACK
            }

            setUsePercentValues(true)
            setEntryLabelTextSize(12f)
            setEntryLabelColor(Color.BLACK)
            setDrawEntryLabels(true)
            invalidate()
        }
    }

    private fun setupDailySalesChart() {
        dailySalesBarChart.apply {
            description = Description().apply {
                text = "Daily Sales Summary"
                textSize = 12f
                textColor = Color.BLACK
            }
            setTouchEnabled(true)
            setBackgroundColor(Color.WHITE)
            setDrawGridBackground(false)

            legend.apply {
                isEnabled = true
                textSize = 12f
                textColor = Color.BLACK
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textSize = 10f
                textColor = Color.BLACK
                labelRotationAngle = -45f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                textSize = 10f
                textColor = Color.BLACK
                axisMinimum = 0f
                setDrawZeroLine(true)
            }

            axisRight.isEnabled = false
            invalidate()
        }
    }

    private fun formatDateForApi(dateString: String): String? {
        return try {
            if (dateString.isBlank()) return null
            val parts = dateString.split("-")
            if (parts.size == 3) {
                val (d, m, y) = parts
                "$y-$m-$d"
            } else null
        } catch (e: Exception) {
            Log.e("DispenserSalesGraph", "Error formatting date: ${e.message}")
            null
        }
    }

    private fun formatDateForDisplay(dateString: String): String {
        return try {
            val parts = dateString.split("-")
            if (parts.size == 3) {
                "${parts[2]}-${parts[1]}-${parts[0]}"
            } else {
                dateString
            }
        } catch (e: Exception) {
            dateString
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        tvStatus.visibility = if (show) View.VISIBLE else View.GONE
        tvStatus.text = if (show) "Loading sales data..." else ""
        tvStatus.setTextColor(Color.BLUE)

        llChartsContainer.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun clearData() {
        salesData.clear()
        shiftData.clear()
        dispenserData.clear()

        // Clear existing chart data
        salesLineChart.clear()
        shiftBarChart.clear()
        dispenserPieChart.clear()
        dailySalesBarChart.clear()

        // Reset statistics
        tvTotalSales.text = "0"
        tvAvgDaily.text = "0"
        tvBestShift.text = "-"
    }

    private fun fetchSalesResult(
        gaId: String,
        startDate: String,
        endDate: String,
        companyId: String,
        stationId: String,
        onResult: (JSONObject) -> Unit
    ) {
        val url = "https://www.cng-suvidha.in/dispenser/API/iot_dispenser_sales.php" +
                "?apicall=read_sales" +
                "&ga_id=$gaId" +
                "&station_id=$stationId" +
                "&start_date=$startDate" +
                "&end_date=$endDate" +
                "&frequency_type=interval" +
                "&interval=8%24"

        val client = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

        // Debug log for parameters
        Log.d(
            "PARAMS>>",
            "GET -> $url"
        )

        val request = Request.Builder()
            .url(url)
            .get()   // ✅ GET request
            .addHeader("Cache-Control", "no-cache")
            .addHeader("User-Agent", "OkHttp")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DispenserSalesGraph", "API call failed: ${e.message}")
                activity?.runOnUiThread {
                    showError("Network error: ${e.message}")
                }
                onResult(JSONObject())
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseString = response.body?.string()
                    Log.d("DispenserSalesGraph", "Response: $responseString")

                    if (response.isSuccessful && !responseString.isNullOrEmpty()) {
                        try {
                            val json = JSONObject(responseString)
                            onResult(json)
                        } catch (e: Exception) {
                            Log.e("DispenserSalesGraph", "Error parsing JSON: ${e.message}")
                            Log.e("DispenserSalesGraph", "Raw response: $responseString")
                            activity?.runOnUiThread {
                                showError("Error parsing response: ${e.message}")
                            }
                            onResult(JSONObject())
                        }
                    } else {
                        Log.e("DispenserSalesGraph", "API error: ${response.code} - ${response.message}")
                        activity?.runOnUiThread {
                            showError("API error: ${response.code} - ${response.message}")
                        }
                        onResult(JSONObject())
                    }
                } catch (e: Exception) {
                    Log.e("DispenserSalesGraph", "Error handling response: ${e.message}", e)
                    activity?.runOnUiThread {
                        showError("Error handling response: ${e.message}")
                    }
                    onResult(JSONObject())
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun processAndDisplayGraphs(
        summaryJson: JSONObject,
        startDate: String,
        stationId: String
    ) {
        try {
            Log.d("DispenserSalesGraph", "Processing JSON: $summaryJson")

            val hasError = summaryJson.optBoolean("error", true)
            val dataAvailable = summaryJson.optBoolean("data_available", false)

            Log.d("DispenserSalesGraph", "hasError: $hasError, dataAvailable: $dataAvailable")

            if (!hasError && dataAvailable) {
                val data = summaryJson.optJSONObject("data")
                if (data != null) {
                    Log.d("DispenserSalesGraph", "Data object found, processing...")
                    processJsonData(data, startDate, stationId)

                    if (salesData.isNotEmpty() || shiftData.isNotEmpty() || dispenserData.isNotEmpty()) {
                        showLoading(false)
                        updateChartsWithFilter()
                        Log.d("DispenserSalesGraph", "Charts updated successfully")
                    } else {
                        showError("No valid sales data found in response")
                    }
                } else {
                    showError("No data object found in response")
                }
            } else {
                val errorMessage = summaryJson.optString("message", "No data available for selected criteria")
                showError(errorMessage)
            }
        } catch (e: Exception) {
            Log.e("DispenserSalesGraph", "Error processing data: ${e.message}", e)
            showError("Error processing data: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun fetchDateRangeData(
        gaId: String,
        startDate: String,
        endDate: String,
        companyId: String,
        stationId: String
    ) {
        val dateList = generateDateRange(startDate, endDate)
        var completedCalls = 0
        val totalCalls = dateList.size

        Log.d("DispenserSalesGraph", "Fetching data for ${dateList.size} dates")

        for (date in dateList) {
            fetchSalesResult(gaId, date, date, companyId, stationId) { summaryJson ->
                completedCalls++

                Log.d("DispenserSalesGraph", "Completed $completedCalls/$totalCalls calls")

                if (!summaryJson.optBoolean("error", true) &&
                    summaryJson.optBoolean("data_available", false)) {
                    val data = summaryJson.optJSONObject("data")
                    if (data != null) {
                        processJsonData(data, date, stationId)
                    }
                }

                if (completedCalls == totalCalls) {
                    activity?.runOnUiThread {
                        showLoading(false)
                        if (salesData.isNotEmpty() || shiftData.isNotEmpty() || dispenserData.isNotEmpty()) {
                            updateChartsWithFilter()
                            Log.d("DispenserSalesGraph", "All charts updated for date range")
                        } else {
                            showError("No data found for the selected date range")
                        }
                    }
                }
            }
        }
    }

    private fun generateDateRange(startDate: String, endDate: String): List<String> {
        val dateList = mutableListOf<String>()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        try {
            val start = sdf.parse(startDate)
            val end = sdf.parse(endDate)

            if (start != null && end != null) {
                val calendar = Calendar.getInstance()
                calendar.time = start

                while (!calendar.time.after(end)) {
                    dateList.add(sdf.format(calendar.time))
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }
            }
        } catch (e: Exception) {
            Log.e("DispenserSalesGraph", "Error generating date range: ${e.message}")
            dateList.add(startDate)
        }

        Log.d("DispenserSalesGraph", "Generated date range: $dateList")
        return dateList
    }

// Now using slotToShiftMap directly for slot1/slot2/slot3 mapping

    @RequiresApi(Build.VERSION_CODES.N)
    private fun processJsonData(data: JSONObject, date: String, stationId: String) {
        try {
            val formattedDisplayDate = formatDateForDisplay(date)

            synchronized(this) {
                // Remove existing data for this date
                salesData.removeAll { it.date == formattedDisplayDate }

                Log.d("DispenserSalesGraph", "Processing data for date: $date, station: $stationId")
                val stationData = data.optJSONObject(stationId) ?: return

                val dateData = stationData.optJSONObject(date) ?: return
                Log.d("DispenserSalesGraph", "Date data keys: ${dateData.keys().asSequence().toList()}")

                val slotKeys = dateData.keys()
                while (slotKeys.hasNext()) {
                    val slotKey = slotKeys.next()   // e.g. "slot0", "slot1", "slot2", "slot3"
                    val slotData = dateData.optJSONObject(slotKey) ?: continue

                    // Skip slot0 as it contains empty/zero data
                    if (slotKey == "slot0") {
                        Log.d("DispenserSalesGraph", "Skipping slot0 (empty data)")
                        continue
                    }

                    // FIXED: Use slotToShiftMap instead of getShiftFromSlot()
                    val shiftName = slotToShiftMap[slotKey]
                    if (shiftName == null) {
                        Log.w("DispenserSalesGraph", "Unknown slot key: $slotKey, skipping")
                        continue
                    }

                    val dispenserKeys = slotData.keys()
                    while (dispenserKeys.hasNext()) {
                        val dispenserKey = dispenserKeys.next()
                        val dispenserDataObj = slotData.optJSONObject(dispenserKey) ?: continue

                        val dispenserName = mapDispenserKeyToName(dispenserKey)

                        // Iterate A/B nozzle sides
                        val sideKeys = dispenserDataObj.keys()
                        while (sideKeys.hasNext()) {
                            val side = sideKeys.next()   // "A" / "B"
                            val nozzleData = dispenserDataObj.optJSONObject(side) ?: continue

                            val sale = nozzleData.optDouble("sale", 0.0)
                            val reading = nozzleData.optString("reading", "")
                            val time = nozzleData.optString("time", "")

                            Log.d("DispenserSalesGraph", "Slot=$slotKey, Shift=$shiftName, Dispenser=$dispenserName, Side=$side, Sale=$sale")

                            // Only add non-zero sales
                            if (sale > 0.0) {
                                // Check if this exact data point already exists
                                val existingData = salesData.find {
                                    it.date == formattedDisplayDate &&
                                            it.shift == shiftName &&
                                            it.dispenser == dispenserName &&
                                            it.nozzle == side &&
                                            it.sale == sale
                                }

                                if (existingData == null) {
                                    salesData.add(
                                        SalesDataPoint(
                                            date = formattedDisplayDate,
                                            shift = shiftName,
                                            dispenser = dispenserName,
                                            nozzle = side,
                                            sale = sale,
                                            reading = reading,
                                            dateTime = time
                                        )
                                    )
                                    updateAggregatedData(shiftName, dispenserName, sale)
                                }
                            }
                        }
                    }
                }
            }
            Log.d("DispenserSalesGraph", "After processing → Sales: ${salesData.size}, Shifts: ${shiftData.size}, Dispensers: ${dispenserData.size}")
        } catch (e: Exception) {
            Log.e("DispenserSalesGraph", "Error processing JSON data: ${e.message}", e)
        }
    }

    // Also update the updateAggregatedData to prevent doubling
    private fun updateAggregatedData(shiftName: String, dispenserName: String, sale: Double) {
        // Update shift totals
        val currentShiftTotal = shiftData[shiftName] ?: 0.0
        shiftData[shiftName] = currentShiftTotal + sale

        // Update dispenser totals
        val currentDispenserTotal = dispenserData[dispenserName] ?: 0.0
        dispenserData[dispenserName] = currentDispenserTotal + sale

        Log.d("DispenserSalesGraph", "Updated aggregated data - Shift $shiftName: ${shiftData[shiftName]}, $dispenserName: ${dispenserData[dispenserName]}")
    }


    private fun updateAllCharts() {
        Log.d("DispenserSalesGraph", "Updating all charts...")
        updateStatistics()
        updateLineChart()
        updateBarChart()
        updatePieChart()
        updateDailySalesChart()
    }

    // NEW: Updated chart functions that use filtered data
    private fun updateLineChart() {
        updateLineChartWithData(salesData)
    }

    private fun updateBarChart() {
        updateBarChartWithData(shiftData)
    }

    private fun updatePieChart() {
        updatePieChartWithData(dispenserData)
    }

    private fun updateDailySalesChart() {
        updateDailySalesChartWithData(salesData)
    }

    private fun updateStatistics() {
        updateStatisticsWithData(salesData, shiftData)
    }

    private fun updateLineChartWithData(salesDataList: List<SalesDataPoint>) {
        Log.d("DispenserSalesGraph", "Updating line chart with ${salesDataList.size} data points")

        if (salesDataList.isEmpty()) {
            Log.w("DispenserSalesGraph", "No sales data for line chart")
            salesLineChart.clear()
            salesLineChart.invalidate()
            return
        }

        try {
            val dispenserGroups = salesDataList.groupBy { it.dispenser }
            val uniqueDates = salesDataList.map { it.date }.distinct().sorted()

            Log.d("DispenserSalesGraph", "Unique dates: $uniqueDates")
            Log.d("DispenserSalesGraph", "Dispenser groups: ${dispenserGroups.keys}")

            var colorIndex = 0
            val lineDataSets = mutableListOf<LineDataSet>()

            for ((dispenser, dataPoints) in dispenserGroups) {
                val entries = mutableListOf<Entry>()
                val dailyTotals = dataPoints.groupBy { it.date }
                    .mapValues { (_, points) -> points.sumOf { it.sale } }

                uniqueDates.forEachIndexed { index, date ->
                    val total = dailyTotals[date] ?: 0.0
                    entries.add(Entry(index.toFloat(), total.toFloat()))
                }

                if (entries.isNotEmpty()) {
                    val dataSet = LineDataSet(entries, dispenser).apply {
                        color = ColorTemplate.MATERIAL_COLORS[colorIndex % ColorTemplate.MATERIAL_COLORS.size]
                        setCircleColor(color)
                        lineWidth = 3f
                        circleRadius = 5f
                        setDrawCircleHole(false)
                        valueTextSize = 10f
                        setDrawValues(true)
                        valueTextColor = Color.BLACK
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                    }
                    lineDataSets.add(dataSet)
                    colorIndex++

                    Log.d("DispenserSalesGraph", "Added line dataset for $dispenser with ${entries.size} entries")
                }
            }

            if (lineDataSets.isNotEmpty()) {
                val lineData = LineData(lineDataSets as List<ILineDataSet>)
                salesLineChart.data = lineData
                salesLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(uniqueDates)
                salesLineChart.notifyDataSetChanged()
                salesLineChart.invalidate()

                Log.d("DispenserSalesGraph", "Line chart updated successfully")
            } else {
                Log.w("DispenserSalesGraph", "No line datasets created")
            }
        } catch (e: Exception) {
            Log.e("DispenserSalesGraph", "Error updating line chart: ${e.message}", e)
        }
    }

    private fun updateBarChartWithData(shiftDataMap: Map<String, Double>) {
        Log.d("DispenserSalesGraph", "Updating bar chart with shift data: $shiftDataMap")

        try {
            val entries = mutableListOf<BarEntry>()
            val labels = mutableListOf<String>()

            // Only show shifts that have data (non-zero values)
            val shiftsWithData = shiftDataMap.filter { it.value > 0.0 }

            // Define preferred order for shifts
            val preferredOrder = listOf(
                "Morning (8AM-4PM)",
                "Evening (4PM-12AM)",
                "Night (12AM-8AM)"
            )

            // Sort shifts by preferred order, but only include shifts with data
            val sortedShifts = preferredOrder.filter { shift ->
                shiftsWithData.containsKey(shift)
            }.plus(
                // Add any other shifts not in preferred order
                shiftsWithData.keys.filter { !preferredOrder.contains(it) }
            )

            var index = 0
            for (shiftName in sortedShifts) {
                val total = shiftsWithData[shiftName] ?: 0.0
                entries.add(BarEntry(index.toFloat(), total.toFloat()))

                // Use shorter labels for better display
                val shortLabel = when (shiftName) {
                    "Morning (8AM-4PM)" -> "Morning"
                    "Evening (4PM-12AM)" -> "Evening"
                    "Night (12AM-8AM)" -> "Night"
                    else -> shiftName
                }
                labels.add(shortLabel)
                Log.d("DispenserSalesGraph", "Added bar entry for $shiftName: $total")
                index++
            }

            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, "Sales by Shift").apply {
                    // Use different colors based on number of shifts
                    colors = when (entries.size) {
                        1 -> listOf(Color.parseColor("#4ECDC4"))
                        2 -> listOf(Color.parseColor("#4ECDC4"), Color.parseColor("#45B7D1"))
                        else -> listOf(
                            Color.parseColor("#4ECDC4"), // Teal for Morning
                            Color.parseColor("#45B7D1"), // Blue for Evening
                            Color.parseColor("#96CEB4")  // Green for Night
                        )
                    }
                    valueTextSize = 12f
                    valueTextColor = Color.BLACK
                    setDrawValues(true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()} kg"
                        }
                    }
                }

                val barData = BarData(dataSet)
                barData.barWidth = if (entries.size == 1) 0.3f else 0.6f

                shiftBarChart.data = barData
                shiftBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                shiftBarChart.xAxis.labelRotationAngle = 0f // No rotation for shorter labels
                shiftBarChart.notifyDataSetChanged()
                shiftBarChart.invalidate()

                Log.d("DispenserSalesGraph", "Shift bar chart updated successfully with ${entries.size} entries")
            } else {
                // Clear chart if no data
                shiftBarChart.clear()
                shiftBarChart.invalidate()
                Log.d("DispenserSalesGraph", "No shift data to display, chart cleared")
            }
        } catch (e: Exception) {
            Log.e("DispenserSalesGraph", "Error updating bar chart: ${e.message}", e)
        }
    }

    private fun updatePieChartWithData(dispenserDataMap: Map<String, Double>) {
        Log.d("DispenserSalesGraph", "Updating pie chart with dispenser data: $dispenserDataMap")
        if (dispenserDataMap.isEmpty() || dispenserDataMap.values.all { it == 0.0 }) {
            Log.w("DispenserSalesGraph", "No dispenser data for pie chart")
            dispenserPieChart.clear()
            dispenserPieChart.invalidate()
            return
        }

        try {
            val entries = mutableListOf<PieEntry>()
            val colors = mutableListOf<Int>()
            var colorIndex = 0

            for ((dispenser, total) in dispenserDataMap) {
                if (total > 0) {
                    entries.add(PieEntry(total.toFloat(), dispenser))
                    colors.add(ColorTemplate.MATERIAL_COLORS[colorIndex % ColorTemplate.MATERIAL_COLORS.size])
                    colorIndex++
                    Log.d("DispenserSalesGraph", "Added pie entry for $dispenser: $total %")
                }
            }

            if (entries.isNotEmpty()) {
                val dataSet = PieDataSet(entries, "Sales Distribution").apply {
                    this.colors = colors
                    valueTextSize = 12f
                    valueTextColor = Color.WHITE
                    setDrawValues(true)
                    // FIXED: Change from kg to percentage
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return String.format("%.1f%%", value)
                        }
                    }
                }

                val pieData = PieData(dataSet)
                dispenserPieChart.data = pieData
                dispenserPieChart.description.text = if (entries.size == 1) {
                    "Sales for ${entries[0].label}"
                } else {
                    "Sales Distribution by Dispenser"
                }

                // Enable percentage values display
                dispenserPieChart.setUsePercentValues(true)
                dispenserPieChart.notifyDataSetChanged()
                dispenserPieChart.invalidate()
                Log.d("DispenserSalesGraph", "Pie chart updated successfully with ${entries.size} entries")
            }
        } catch (e: Exception) {
            Log.e("DispenserSalesGraph", "Error updating pie chart: ${e.message}", e)
        }
    }


    private fun updateDailySalesChartWithData(salesDataList: List<SalesDataPoint>) {
        Log.d("DispenserSalesGraph", "Updating daily sales chart")

        if (salesDataList.isEmpty()) {
            Log.w("DispenserSalesGraph", "No sales data for daily chart")
            dailySalesBarChart.clear()
            dailySalesBarChart.invalidate()
            return
        }

        try {
            val dailyTotals = salesDataList.groupBy { it.date }
                .mapValues { (_, points) -> points.sumOf { it.sale } }
                .toList()
                .sortedBy { it.first }

            Log.d("DispenserSalesGraph", "Daily totals: $dailyTotals")

            val entries = dailyTotals.mapIndexed { index, (_, total) ->
                BarEntry(index.toFloat(), total.toFloat())
            }

            val labels = dailyTotals.map { it.first }

            if (entries.isNotEmpty()) {
                val dataSet = BarDataSet(entries, "Daily Sales Total").apply {
                    color = Color.parseColor("#FF9800")
                    valueTextSize = 10f
                    valueTextColor = Color.BLACK
                    setDrawValues(true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()} kg"
                        }
                    }
                }

                val barData = BarData(dataSet)
                barData.barWidth = 0.8f

                dailySalesBarChart.data = barData
                dailySalesBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                dailySalesBarChart.notifyDataSetChanged()
                dailySalesBarChart.invalidate()

                Log.d("DispenserSalesGraph", "Daily sales chart updated successfully")
            }
        } catch (e: Exception) {
            Log.e("DispenserSalesGraph", "Error updating daily sales chart: ${e.message}", e)
        }
    }

    private fun updateStatisticsWithData(salesDataList: List<SalesDataPoint>, shiftDataMap: Map<String, Double>) {
        Log.d("DispenserSalesGraph", "Updating statistics")

        if (salesDataList.isEmpty()) {
            tvTotalSales.text = "0"
            tvAvgDaily.text = "0"
            tvBestShift.text = "-"
            return
        }

        // Calculate total sales
        val totalSales = salesDataList.sumOf { it.sale }
        tvTotalSales.text = String.format("%.1f", totalSales)

        // Calculate average daily sales
        val uniqueDates = salesDataList.map { it.date }.distinct().size
        val avgDaily = if (uniqueDates > 0) totalSales / uniqueDates else 0.0
        tvAvgDaily.text = String.format("%.1f", avgDaily)

        // Find best shift
        val bestShift = shiftDataMap.maxByOrNull { it.value }?.key ?: "-"
        val bestShiftDisplay = when (bestShift) {
            "Morning (8AM-4PM)" -> "Morning"
            "Evening (4PM-12AM)" -> "Evening"
            "Night (12AM-8AM)" -> "Night"
            else -> bestShift
        }
        tvBestShift.text = if (bestShift != "-") bestShiftDisplay else "-"

        Log.d("DispenserSalesGraph", "Statistics updated - Total: $totalSales, Avg: $avgDaily, Best: $bestShiftDisplay")
    }

    private fun mapDispenserKeyToName(dispenserKey: String): String {
        val mappedName = when (dispenserKey.lowercase()) {
            "bus_point_cgs_harua", "bus_point" -> "Disp Bus"
            "d1_cgs_harua", "d1" -> "D1"
            "d2_cgs_harua", "d2" -> "D2"
            "d3_cgs_harua", "d3" -> "D3"
            "d5_cgs_harua", "d5" -> "D5"
            "dispenser1" -> "Dispenser1"
            "dispenser2" -> "Dispenser2"
            "dispenser3" -> "Dispenser3"
            "dispenser4" -> "Dispenser4"
            else -> {
                when {
                    dispenserKey.contains("bus", ignoreCase = true) -> "Disp Bus"
                    dispenserKey.contains("d1", ignoreCase = true) -> "D1"
                    dispenserKey.contains("d2", ignoreCase = true) -> "D2"
                    dispenserKey.contains("d3", ignoreCase = true) -> "D3"
                    dispenserKey.contains("d5", ignoreCase = true) -> "D5"
                    else -> {
                        Log.w("DispenserSalesGraph", "Unknown dispenser key: $dispenserKey")
                        dispenserKey // Return original key if no mapping found
                    }
                }
            }
        }

        Log.d("DispenserSalesGraph", "Mapped '$dispenserKey' to '$mappedName'")
        return mappedName
    }

    private fun showError(message: String) {
        showLoading(false)
        tvStatus.visibility = View.VISIBLE
        tvStatus.text = "Error: $message"
        tvStatus.setTextColor(Color.RED)
        llChartsContainer.visibility = View.GONE

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        Log.e("DispenserSalesGraph", "Showing error: $message")
    }
}