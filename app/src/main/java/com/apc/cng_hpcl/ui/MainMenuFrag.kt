package com.apc.cng_hpcl.ui
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.apc.cng_hpcl.R
import com.apc.cng_hpcl.home.newTrans.TransViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainMenuFrag:Fragment() {
    private lateinit var mContext: Context
    private lateinit var navController: NavController
    private val vm: TransViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme {
                    ExampleGrid()
                }
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)
    }

    @Composable
    fun GridScreen(items: List<GridItem>) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.img_1),
                contentDescription = null,
                alpha = 0.4f,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            items(items.size) { index ->
                GridItemView(item = items[index])
            }
        }
    }
    }

    @Composable
    fun GridItemView(item: GridItem) {
        Card(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(),
            onClick = {
                val bundle = Bundle()
                val station = vm.stationId.value!!
                bundle.putString("username", station)
                bundle.putString("station", station)

                bundle.putBoolean("isSch", item.isSch)
                bundle.putBoolean("isRep", item.isRep)
                bundle.putInt("type",item.ctrlId)


                //val action=CascadeFragDirections.actionCascadeFragToNewTransActivity()
                navController.navigate(item.navId,bundle)
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE9C006))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.topText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.bottomText,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(vertical = 2.dp)
                          .size(64.dp) ,
                    colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.5f)),

                )
                Text(
                    text = item.bottomText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }



    data class GridItem(val navId: Int, val topText: String, val imageRes: Int, val bottomText: String, val isSch:Boolean, val isRep:Boolean, val ctrlId:Int)

    // Usage example
    @Composable
    fun ExampleGrid() {
        val items = listOf(
            GridItem(R.id.action_mainMenuFrag_to_suvidhaActivity,"Dispenser Reading", R.drawable.dbs, " डिस्पेंसर रीडिंग", isSch = false, isRep = false,-1),
            GridItem(R.id.action_mainMenuFrag_to_driverFragment,"LCV Transaction", R.drawable.ic_tanker_red, "एलसीवी ट्रांजैक्शन", isSch = false, isRep = false,-1),
            GridItem(R.id.action_mainMenuFrag_to_newTransActivity,"Cascade Reading", R.drawable.temp, " कैस्केड रीडिंग", isSch = true, isRep = false,-1),
            GridItem(R.id.action_mainMenuFrag_to_controlRoomActivity,"LCV Breakdown", R.drawable.tow_truck, "एलसीवी ब्रेकडाउन", isSch = false, isRep = false,0),
            GridItem(R.id.action_mainMenuFrag_to_controlRoomActivity,"Emergency Demand", R.drawable.alarm, "इमरजेंसी डिमांड", isSch = false, isRep = false,1),
            GridItem(R.id.action_mainMenuFrag_to_newTransActivity,"Scheduling Report", R.drawable.analytics, " एलसीवी स्टेटस", isSch = false, isRep = true,-1),
            GridItem(R.id.action_mainMenuFrag_to_controlRoomActivity,"No Power at Station", R.drawable.no_power, "नो पावर एट स्टेशन", isSch = false, isRep = false,2),
            GridItem(R.id.action_mainMenuFrag_to_vehicleChecklistFrag, "Vehicle Checklist", R.drawable.scheduling, "वीहिकल चेकलिस्ट", isSch = false, isRep = false,-1),
        //    GridItem(R.id.action_mainMenuFrag_to_suvidhaActivity,"Vehicle Tracking", R.drawable.tracking, "एलसीवीट् रैकिंग", isSch = false, isRep = false,-1),
        //    GridItem(R.id.action_mainMenuFrag_to_suvidhaActivity,"Logout", R.drawable.ic_baseline_logout_24, "लॉगआउट", isSch = false, isRep = false,-1),

            //   GridItem("Top 9", R.drawable.disp, "Bottom 9"),

            // Add more items up to 10
        )

        GridScreen(items = items)
    }

}