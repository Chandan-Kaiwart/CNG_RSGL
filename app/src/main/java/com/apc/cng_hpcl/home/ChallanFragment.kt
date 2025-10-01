package com.apc.cng_hpcl.home

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import com.apc.cng_hpcl.R
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChallanFragment : Fragment() {

    companion object {
        private const val TAG = "ChallanFragment"
    }

    private lateinit var spinnerLcv: Spinner
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var btnGo: Button
    private lateinit var containerCards: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var btnCreate: Button
    private lateinit var apiService: ApiService

    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    // ✅ Updated LCV data from API
    private val lcvItems = mutableListOf<SpinnerItem>()
    private var allLcvData = listOf<LcvData>()
    private var selectedLcvId: String? = null

    private var allChallanData = mutableListOf<ChallanData>()
    private var filteredData = mutableListOf<ChallanData>()

    // Data class for Challan
    data class ChallanData(
        val sNo: String,
        val date: String,
        val time: String,
        val lcvNumber: String,
        val mgsStationName: String,
        val dbsStationName: String,
        val challanNumber: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_challan, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)
        setupRetrofit()
        loadLcvData()  // ✅ Load LCV data from API
        setupDatePickers()
        setupGoButton()

        // Default dates = today
        val today = dateFormat.format(Date())
        startDate.setText(today)
        endDate.setText(today)

        // Load initial data
        fetchChallanData()

        btnCreate.setOnClickListener {
            findNavController().navigate(R.id.action_challanFragment_to_createChallan)
        }
    }

    private fun initializeViews(view: View) {
        spinnerLcv = view.findViewById(R.id.spinnerLcv)
        startDate = view.findViewById(R.id.startDate)
        endDate = view.findViewById(R.id.endDate)
        btnGo = view.findViewById(R.id.btnGo)
        btnCreate = view.findViewById(R.id.btnCreate)
        containerCards = view.findViewById(R.id.containerCards)

        progressBar = ProgressBar(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
            visibility = View.GONE
        }
        containerCards.addView(progressBar)
    }

    // ✅ Setup Retrofit
    private fun setupRetrofit() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.cng-suvidha.in/CNGPortal/staging_test_dispenser/dispenser/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    // ✅ Load LCV Data from API
    private fun loadLcvData() {
        apiService.getLcvData().enqueue(object : Callback<LcvResponse> {
            override fun onResponse(call: Call<LcvResponse>, response: Response<LcvResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val lcvResponse = response.body()!!
                    allLcvData = lcvResponse.data

                    // ✅ Populate spinner with API data
                    lcvItems.clear()
                    lcvItems.add(SpinnerItem("All LCVs", "")) // Default option

                    allLcvData.forEach { lcv ->
                        lcvItems.add(SpinnerItem("${lcv.Lcv_Num} (${lcv.Cascade_Capacity}L)", lcv.Lcv_Num))
                    }

                    setupSpinner()
                } else {
                    // ✅ Fallback to hardcoded options
                    lcvItems.clear()
                    lcvItems.add(SpinnerItem("All LCVs", ""))
                    lcvItems.add(SpinnerItem("CGS Haraua", "CGS"))
                    lcvItems.add(SpinnerItem("UPSIDC Karkhiyaon", "UPSIDC"))
                    setupSpinner()

                    Toast.makeText(requireContext(), "Using default LCV options", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LcvResponse>, t: Throwable) {
                Log.e(TAG, "LCV API Error: ${t.message}")
                // ✅ Fallback to hardcoded options
                lcvItems.clear()
                lcvItems.add(SpinnerItem("All LCVs", ""))
                lcvItems.add(SpinnerItem("CGS Haraua", "CGS"))
                lcvItems.add(SpinnerItem("UPSIDC Karkhiyaon", "UPSIDC"))
                setupSpinner()
            }
        })
    }

    // ✅ Setup Spinner with LCV selection listener
    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lcvItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLcv.adapter = adapter

        // ✅ Add selection listener for filtering
        spinnerLcv.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedLcvId = if (position > 0) lcvItems[position].hiddenId else null

                // ✅ Apply filters when LCV selection changes
                if (allChallanData.isNotEmpty()) {
                    applyFilters()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupDatePickers() {
        startDate.setOnClickListener {
            showDatePicker { date -> startDate.setText(date) }
        }

        endDate.setOnClickListener {
            showDatePicker { date -> endDate.setText(date) }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupGoButton() {
        btnGo.setOnClickListener { fetchChallanData() }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun fetchChallanData() {
        val startDateText = startDate.text.toString()
        val endDateText = endDate.text.toString()

        if (startDateText.isEmpty() || endDateText.isEmpty()) {
            Toast.makeText(requireContext(), "Please select both start and end dates", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)
        lifecycleScope.launch {
            try {
                val startDateApi = convertDateToApiFormat(startDateText)
                val endDateApi = convertDateToApiFormat(endDateText)

                val response = withContext(Dispatchers.IO) {
                    makeApiCall(startDateApi, endDateApi)
                }

                Log.d(TAG, "API Response: $response")

                if (response != null) {
                    parseChallanResponse(response)
                    applyFilters()  // ✅ Apply LCV filters
                } else {
                    showError("Failed to fetch data from server")
                }

            } catch (e: Exception) {
                showError("Error: ${e.message}")
            } finally {
                showLoading(false)
            }
        }
    }

    private fun convertDateToApiFormat(displayDate: String): String {
        return try {
            val date = dateFormat.parse(displayDate)
            apiDateFormat.format(date ?: Date())
        } catch (e: Exception) {
            apiDateFormat.format(Date())
        }
    }

    private suspend fun makeApiCall(startDate: String, endDate: String): String? {
        return try {
            val urlString = "https://www.cng-suvidha.in/CNGPortal/staging_test_dispenser/dispenser/API/challan_api.php?apicall=get_saved_challans"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.doInput = true

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
            e.printStackTrace()
            null
        }
    }

    private fun parseChallanResponse(response: String) {
        try {
            val jsonObject = JSONObject(response)
            if (!jsonObject.has("data")) {
                showError("No data field in response")
                return
            }

            val dataArray = jsonObject.getJSONArray("data")
            allChallanData.clear()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)

                // Extract serial number from challan_no
                val challanNo = item.optString("challan_no", "")
                val serialNumber = challanNo.split("/").lastOrNull() ?: ""

                val challanData = ChallanData(
                    sNo = serialNumber,
                    date = formatApiDate(item.optString("date_reading", "")),
                    time = formatApiTime(item.optString("date_reading", "")),
                    lcvNumber = item.optString("lcv_id", ""),
                    mgsStationName = item.optString("mgs_station_name", ""),
                    dbsStationName = item.optString("dbs_station_name", ""),
                    challanNumber = challanNo
                )

                allChallanData.add(challanData)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            showError("Error parsing server response")
        }
    }

    private fun formatApiDate(dateTime: String): String {
        return try {
            val apiFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = apiFormat.parse(dateTime)
            SimpleDateFormat("EEE MMM dd yyyy", Locale.getDefault()).format(date ?: Date())
        } catch (e: Exception) {
            dateTime.split(" ").firstOrNull() ?: ""
        }
    }

    private fun formatApiTime(dateTime: String): String {
        return try {
            val apiFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = apiFormat.parse(dateTime)
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(date ?: Date())
        } catch (e: Exception) {
            dateTime.split(" ").getOrNull(1)?.substring(0, 5) ?: ""
        }
    }

    // ✅ Updated Filter Logic for API-based LCV filtering
    @RequiresApi(Build.VERSION_CODES.M)
    private fun applyFilters() {
        filteredData.clear()

        for (challan in allChallanData) {
            var shouldInclude = true

            // ✅ Filter by selected LCV ID
            if (selectedLcvId != null && selectedLcvId!!.isNotEmpty()) {
                // Check if challan's LCV number matches selected LCV
                if (challan.lcvNumber != selectedLcvId) {
                    shouldInclude = false
                }
            }

            if (shouldInclude) {
                filteredData.add(challan)
            }
        }

        displayChallanData(filteredData)

        // ✅ Show meaningful message
        val selectedLcvName = if (selectedLcvId.isNullOrEmpty()) "All LCVs" else selectedLcvId
        Toast.makeText(
            requireContext(),
            "Found ${filteredData.size} records for $selectedLcvName",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Rest of the methods remain same (displayChallanData, createChallanCard, etc.)
    @RequiresApi(Build.VERSION_CODES.M)
    private fun displayChallanData(data: List<ChallanData>) {
        containerCards.removeAllViews()

        if (data.isEmpty()) {
            val noDataView = TextView(requireContext()).apply {
                text = "No challans found for the selected criteria"
                textSize = 16f
                gravity = android.view.Gravity.CENTER
                setPadding(16, 32, 16, 32)
                setTextColor(android.graphics.Color.GRAY)
            }
            containerCards.addView(noDataView)
            return
        }

        for (challan in data) {
            val cardView = createChallanCard(challan)
            containerCards.addView(cardView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createChallanCard(challan: ChallanData): View {
        val cardLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 20, 24, 20)
            background = resources.getDrawable(R.drawable.card_background, null)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 0, 0, 16) }
        }

        // Header row
        val headerRow = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 2f
            setPadding(0, 0, 0, 16)
        }

        val sNoBadge = TextView(requireContext()).apply {
            text = "S.No: ${challan.sNo}"
            setPadding(28, 10, 28, 10)
            setTextColor(android.graphics.Color.BLACK)
            textSize = 15f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            background = resources.getDrawable(R.drawable.badge_bg_green, null)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val dateTime = TextView(requireContext()).apply {
            text = "${challan.date} ${challan.time}"
            textSize = 13.5f
            setTextColor(android.graphics.Color.DKGRAY)
            gravity = android.view.Gravity.END
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        headerRow.addView(sNoBadge)
        headerRow.addView(dateTime)

        // Details with station names
        val lcvRow = createTextView("LCV: ${challan.lcvNumber}", textStyle = true, textSizeSp = 22f)
        val mgsRow = createTextView("MGS Station: ${challan.mgsStationName}", textSizeSp = 18f)
        val dbsRow = createTextView("DBS Station: ${challan.dbsStationName}", textSizeSp = 18f)

        // Challan button
        val challanBtn = TextView(requireContext()).apply {
            text = "📋 Challan: ${challan.challanNumber}"
            textSize = 16f
            setTextColor(android.graphics.Color.WHITE)
            gravity = android.view.Gravity.CENTER
            setPadding(20, 14, 20, 14)
            background = resources.getDrawable(R.drawable.round_button_bg, null)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, 16, 0, 0) }

            setOnClickListener {
                val bundle = Bundle().apply {
                    putString("challanNumber", challan.challanNumber)
                    putString("date", challan.date)
                    putString("time", challan.time)
                    putString("lcvNumber", challan.lcvNumber)
                    putString("mgsStation", challan.mgsStationName)
                    putString("dbsStation", challan.dbsStationName)
                }

                findNavController().navigate(
                    R.id.action_challanFragment_to_challanDetailFragment,
                    bundle
                )
            }
        }

        // Add views to card
        cardLayout.addView(headerRow)
        cardLayout.addView(lcvRow)
        cardLayout.addView(mgsRow)
        cardLayout.addView(dbsRow)
        cardLayout.addView(challanBtn)

        return cardLayout
    }

    private fun createTextView(
        text: String,
        weight: Float = 0f,
        textStyle: Boolean = false,
        textColor: String = "#000000",
        textSizeSp: Float = 14f
    ): TextView {
        return TextView(requireContext()).apply {
            this.text = text
            setPadding(6, 4, 6, 4)
            textSize = textSizeSp
            setTextColor(android.graphics.Color.parseColor(textColor))
            if (textStyle) setTypeface(typeface, android.graphics.Typeface.BOLD)

            if (weight > 0) {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight)
            } else {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 6, 0, 6) }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnGo.isEnabled = !show
        btnGo.text = if (show) "Loading..." else "📄 View Challan"
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        containerCards.removeAllViews()

        val errorView = TextView(requireContext()).apply {
            text = message
            textSize = 16f
            gravity = android.view.Gravity.CENTER
            setPadding(16, 32, 16, 32)
            setTextColor(android.graphics.Color.RED)
        }
        containerCards.addView(errorView)
    }
}

// Extension
fun String.capitalize(): String {
    return if (this.isNotEmpty()) {
        this.substring(0, 1).uppercase() + this.substring(1).lowercase()
    } else this
}