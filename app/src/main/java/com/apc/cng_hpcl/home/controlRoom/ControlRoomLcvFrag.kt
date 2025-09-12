package com.apc.cng_hpcl.home.controlRoom

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.BuildConfig
import com.apc.cng_hpcl.BuildConfig.BASE_URL
import com.apc.cng_hpcl.R
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject

@AndroidEntryPoint
class ControlRoomLcvFrag : Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private val vm: ControlRoomViewModel by activityViewModels()
    private var type:Int=-1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ControlRoomLcvForm(vm)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val args = ControlRoomLcvFragArgs.fromBundle(requireArguments())

        vm.lcvNum.value = args.lcv.toString()
        vm.stationId.value = args.station.toString()
        vm.latlng.value = args.latlng.toString()
        vm.raisedBy.value = "ope" + args.station.toString()
        type=args.type
    //    navController.navigate(R.id.action_controlRoomLcvFrag_to_vehicleChecklistFrag)

        getIssues()

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ControlRoomLcvForm(vm: ControlRoomViewModel) {
        // val issues by vm.issues.collectAsState()
        val names by vm.issueNames.collectAsState()
        //   val issueId by vm.issueId.observeAsState()
        val stationId by vm.stationId.observeAsState()
        //  val raisedBy by vm.raisedBy.observeAsState()
        val lcvPressureReading by vm.lcvPressureReading.observeAsState()
        val stationPressureReading by vm.stationPressureReading.observeAsState()

        val remarks by vm.remarks.observeAsState()
        //    val pressureImgUrl by vm.pressureImgUrl.observeAsState()
        //    val ticketStatus by vm.ticketStatus.observeAsState()
        val lcvNum by vm.lcvNum.observeAsState()
       // var selectedTabIndex by remember { mutableIntStateOf(0) }
         val selectedTabIndex = type

        val tabs = names.toList()

        // State variables
        var dropdownExpanded by remember { mutableStateOf(false) }
        val dropdownOptions = names.toList()
        var selectedOption by remember { mutableStateOf(dropdownOptions[0]) }
    //       var selectedOption = type

        //   var textField1 by remember { mutableStateOf("") }
        //   var textField2 by remember { mutableStateOf("") }
        //   var textField3 by remember { mutableStateOf("") }

        // UI Layout
        Scaffold(
            topBar = {
                         TopAppBar(title = { Text(if(type==0) "LCV Breakdown" else if(type==1) "Emergency Demand" else "No Power at Station") })
             /*   TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color(0xFF02603E),
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color.White,
                            height = 4.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF02603E))
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                selectedTabIndex = index
                                onTabSelected(index) // Perform action on tab click
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            selectedContentColor = Color.White,
                            unselectedContentColor = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
*/
                // Content displayed based on selected tab
                when (selectedTabIndex) {
                    0 -> Log.d("SEL>>", "0")
                    1 -> Log.d("SEL>>", "1")
                    2 -> Log.d("SEL>>", "2")
                }

            }
        ) { innerPadding ->
            Column(
                modifier = Modifier

                        .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()), // Enable scrolling
                verticalArrangement = Arrangement.Top,
            ) {

                // Dropdown Menu

                if (selectedTabIndex != 0 && selectedTabIndex != 1 && selectedTabIndex != 2) {
                    ExposedDropdownMenuBox(

                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                    ) {
                        TextField(
                            value = selectedOption,
                            onValueChange = {
                                vm.issueName.value = it
                                vm.issueId.value = getIssueId(it)
                            },
                            readOnly = true,
                            label = { Text("Select issue") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor() // For proper positioning
                                .fillMaxWidth(),
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                        ) {
                            dropdownOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedOption = option
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
                OutlinedTextField(
                    value = stationId.toString(),
                    onValueChange = { vm.stationId.value = it },
                    label = { Text("Enter Station Id") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(8.dp))
                // Text Fields
                if (selectedTabIndex == 0) {
                    OutlinedTextField(
                        value = lcvNum.toString(),
                        onValueChange = { vm.lcvNum.value = it },
                        label = { Text("LCV Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (selectedTabIndex == 0 || selectedTabIndex == 2) {

                    OutlinedTextField(
                        value = lcvPressureReading.toString(),
                        onValueChange = { vm.lcvPressureReading.value = it },
                        label = { Text("LCV Pressure Gauge Reading") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp), // Padding around the row
                    horizontalAlignment = Alignment.CenterHorizontally, // Space between the cards
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Align items in the center vertically
                ) {

                    if (selectedTabIndex == 0 || selectedTabIndex == 2) {
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            border = BorderStroke(1.dp, Color.Black),
                            modifier = Modifier.wrapContentSize() // Ensure the card wraps content
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp), // Padding inside the card
                                horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
                                verticalArrangement = Arrangement.spacedBy(8.dp) // Space between items
                            ) {
                                Text(text = "LCV Pressure Gauge")
                                Image(
                                    painter = painterResource(id = R.drawable.mp),
                                    contentDescription = "content description",
                                    //         colorFilter = ColorFilter.tint(Color.Black),
                                    modifier = Modifier.size(64.dp) // Set a fixed size for the image
                                )
                                Button(
                                    onClick = {
                                        navController.navigate(
                                            ControlRoomLcvFragDirections.actionControlRoomLcvFragToControlRoomPhotoFrag(
                                                1
                                            )
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF02603E)
                                    ),
                                    modifier = Modifier.wrapContentWidth() // Wrap button to its content width
                                ) {
                                    Text("Capture")
                                }
                            }
                        }
                    }

                    if (selectedTabIndex == 1 || selectedTabIndex == 2) {
                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedCard(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                            border = BorderStroke(1.dp, Color.Black),
                            modifier = Modifier.wrapContentSize() // Ensure the card wraps content
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp), // Padding inside the card
                                horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
                                verticalArrangement = Arrangement.spacedBy(8.dp) // Space between items
                            ) {
                                Text(text = "Station Pressure Gauge")
                                Image(
                                    painter = painterResource(id = R.drawable.mp),
                                    contentDescription = "content description",
                                    //        colorFilter = ColorFilter.tint(Color.Black),
                                    modifier = Modifier.size(64.dp) // Set a fixed size for the image
                                )
                                Button(
                                    onClick = {
                                        navController.navigate(
                                            ControlRoomLcvFragDirections.actionControlRoomLcvFragToControlRoomPhotoFrag(
                                                2
                                            )
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF02603E)
                                    ),
                                    modifier = Modifier.wrapContentWidth() // Wrap button to its content width
                                ) {
                                    Text("Capture")
                                }
                            }
                        }
                    }
                }
                if (selectedTabIndex == 1 || selectedTabIndex == 2) {

                    OutlinedTextField(
                        value = stationPressureReading.toString(),
                        onValueChange = { vm.stationPressureReading.value = it },
                        label = { Text("Station Pressure Gauge Reading") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                }

                OutlinedTextField(
                    value = remarks.toString(),
                    onValueChange = { vm.remarks.value = it },
                    label = { Text("Remarks") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
                )


                Spacer(modifier = Modifier.height(24.dp))





                // Submit Button
                Button(
                    onClick = {
                        // Handle button click, e.g., show a Toast
                        raiseTicket()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF02603E)),

                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit")
                }


            }
        }
    }

    private fun getIssues() {
        val s = BASE_URL+"control_room.php?apicall=readIssueReference"
        Log.d("reqs>>>", s)
        val jsonObjectRequest: StringRequest = object : StringRequest(
            Method.GET, s,
            Response.Listener { resp ->
                Log.d("Res>>>", resp!!)
                val list = mutableListOf<IssueDataModel>()
                val names = mutableListOf<String>()

                try {
                    val response = JSONObject(resp)
                    Log.d("Req>>>", response.toString())
                    if (response.getBoolean("error")) {

                    } else {
                        val arr = response.getJSONArray("data")
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            val dm = IssueDataModel(
                                obj.getString("create_date"),
                                obj.getString("issue_id"),
                                obj.getString("description"),
                                obj.getString("sno")
                            )
                            list.add(dm)
                            names.add(obj.getString("description"))
                        }

                        vm.issues.value = list
                        vm.issueNames.value = names

                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
            2,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.setShouldCache(false)
        Volley.newRequestQueue(mContext).add(jsonObjectRequest)
    }

    private fun raiseTicket() {
        val s = BASE_URL+"control_room.php?apicall=raiseTicket"
        Log.d("reqs>>>", s)
        val jsonObjectRequest: StringRequest = object : StringRequest(
            Method.POST, s,
            Response.Listener { resp ->
                Log.d("Res>>>", resp!!)
                val list = mutableListOf<IssueDataModel>()
                val names = mutableListOf<String>()

                try {
                    val response = JSONObject(resp)
                    Log.d("Req>>>", response.toString())
                    if (response.getBoolean("error")) {

                    } else {


                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["issue_id"] = vm.issueId.value.toString()
                params["station_id"] = vm.stationId.value.toString()
                params["raised_by"] = vm.raisedBy.value.toString()
                params["ticket_status"] = "0"
                params["pressure_gauge_reading"] = vm.lcvPressureReading.value.toString()
                params["lcv_pressure_gauge_reading"] = vm.lcvPressureReading.value.toString()
                params["station_pressure_gauge_reading"] =
                    vm.stationPressureReading.value.toString()

                params["remarks"] = vm.remarks.value.toString()
                params["lat_long"] = vm.latlng.value.toString()
                params["pressure_gauge_image"] = vm.lcvPressureImgUrl.value.toString()
                params["lcv_pressure_gauge_image"] = vm.lcvPressureImgUrl.value.toString()
                params["station_pressure_gauge_image"] = vm.stationPressureImgUrl.value.toString()

                params["lcv_num"] = vm.lcvNum.value.toString()
                Log.d("PARAMS>>", params.toString())
                return params
            }
        }
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
            2,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        jsonObjectRequest.setShouldCache(false)
        Volley.newRequestQueue(mContext).add(jsonObjectRequest)
    }

    private fun getIssueId(issueName: String): String {
        vm.issues.value.forEach {
            if (it.description == issueName) {
                return it.issue_id
            }
        }
        return "-1"
    }

    fun onTabSelected(index: Int) {
        when (index) {
            0 -> {
                // Action for Home tab
                println("Home Tab Selected")
            }

            1 -> {
                // Action for Search tab
                println("Search Tab Selected")
            }

            2 -> {
                // Action for Profile tab
                println("Profile Tab Selected")
            }
        }

    }
}