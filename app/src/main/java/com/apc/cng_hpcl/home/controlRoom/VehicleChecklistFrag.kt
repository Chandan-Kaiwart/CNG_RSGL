package com.apc.cng_hpcl.home.controlRoom

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VehicleChecklistFrag : Fragment(){
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    VehicleInspectionForm()
                }
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)
    }


    @Composable
    fun VehicleInspectionForm() {
        val vehicleInspectionItems = arrayOf(
            "Tyre – Tread Depth",
            "Tyre – Inflation Pressure",
            "Tyre – Cracks and Cuts",
            "Engine – Oil Level",
            "Engine – Coolant Level",
            "Engine – Brake Fluid Level",
            "Engine – Clutch Fluid Level",
            "Engine – Battery Water Level",
            "Engine – Steering Fluid",
            "Light – Interior",
            "Light – Turn",
            "Light – Reverse",
            "Light – Tail",
            "Light – Emergency",
            "Accessory – Tape/Radio",
            "Control – Horn",
            "Control – Engine Start",
            "Control – Central Lock",
            "Control – Power Window",
            "Control – Heater/AC",
            "Control – AT/MT Operation",
            "Control – Brake Operation",
            "Control – Wipers/washers",
            "Control – Steering Operation",
            "Tool – Jack and Wheel Spanner",
            "Tool – First Aid Kit"
        )

        // Use LazyColumn to make the form scrollable
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            items(vehicleInspectionItems.size) { index ->
                InspectionItem(vehicleInspectionItems[index])
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    @Composable
    fun InspectionItem(question: String) {
        var selectedOption by remember { mutableStateOf<String?>(null) }
        var remarks by remember { mutableStateOf(TextFieldValue("")) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = question, style = MaterialTheme.typography.bodyLarge)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedOption == "Yes",
                        onClick = { selectedOption = "Yes" }
                    )
                    Text(text = "Yes")
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedOption == "No",
                        onClick = { selectedOption = "No" }
                    )
                    Text(text = "No")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Remarks") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewVehicleInspectionForm() {
        VehicleInspectionForm()
    }
}