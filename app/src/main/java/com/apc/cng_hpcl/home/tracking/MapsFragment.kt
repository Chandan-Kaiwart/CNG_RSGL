package com.apc.cng_hpcl.home.tracking

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.apc.cng_hpcl.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.infowindow.InfoWindow
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import android.widget.Button
import com.google.android.material.card.MaterialCardView

data class Vehicle(
    val VehicleNo: String,
    val Imei: String,
    val Location: String,
    val Date: String,
    val Tempr: String,
    val Ignition: String,
    val Lat: String,
    val Long: String,
    val Speed: String,
    val Angle: String
)

data class LocationData(
    val lat: Double,
    val lng: Double,
    val name: String
)

class MapsFragment : Fragment() {
    private lateinit var map: MapView
    private lateinit var slideInMenu: MaterialCardView
    private lateinit var menuOverlay: View
    private lateinit var hamburgerMenuBtn: Button
    private val viewModel: TrackingViewModel by activityViewModels()

    // LCV tracking variables - COMMENTED OUT
    // private val updateHandler = Handler(Looper.getMainLooper())
    // private val vehicleMarkers = mutableMapOf<String, Marker>()
    // private val vehiclePaths = mutableMapOf<String, Polyline>()
    // private val vehiclePathPoints = mutableMapOf<String, MutableList<GeoPoint>>()

    private var isMenuOpen = false
    // private var lastUpdateTime = System.currentTimeMillis()

    // Different colors for vehicles - COMMENTED OUT
    // private val vehicleColors = listOf(
    //     Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA,
    //     Color.CYAN, Color.YELLOW, Color.BLACK, Color.GRAY
    // )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        // Initialize OpenStreetMap configuration
        Configuration.getInstance().setUserAgentValue(requireContext().packageName)

        // Initialize views
        map = view.findViewById(R.id.osmMap)
        hamburgerMenuBtn = view.findViewById(R.id.hamburgerMenuBtn)
        slideInMenu = view.findViewById(R.id.slideInMenu)
        menuOverlay = view.findViewById(R.id.menuOverlay)

        setupMap()
        setupHamburgerMenu()

        // COMMENTED OUT LCV TRACKING
        // startLiveTracking()

        val button = view.findViewById<Button>(R.id.btnShowBottomSheet)
        button.setOnClickListener {
            BottomSheetFragment().show(parentFragmentManager, "BottomSheet")
        }

        return view
    }

    private fun setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(true)
        map.controller.setZoom(12.0) // Good zoom level to see stations clearly

        // Center map on your stations area (Kota region)
        map.controller.setCenter(GeoPoint(25.16, 75.87))

        map.setHorizontalMapRepetitionEnabled(false)
        map.setVerticalMapRepetitionEnabled(false)

        // Add only custom filling stations
        addCustomFillingStations()
    }

    // Add custom filling stations function
    private fun addCustomFillingStations() {
        Log.d("MapDebug", "Starting to add filling stations")

        // MGS icon locations
        val mgsLocations = listOf(
            LocationData(25.129452, 75.865469, "RIICO"),
            LocationData(25.154788, 75.874223, "Shaheed Hemraj Meena CNG Filling Station")
        )

        // DBS icon locations
        val dbsLocations = listOf(
            LocationData(25.1825, 75.8605, "Sainik Filling"),
            LocationData(25.1850, 75.8700, "Jay Chambal"),
            LocationData(25.1900, 75.8800, "Shiv Filling")
        )

        Log.d("MapDebug", "Adding ${mgsLocations.size} MGS markers")
        // Add MGS markers
        mgsLocations.forEach { location ->
            addCustomMarker(location, "MGS")
        }

        Log.d("MapDebug", "Adding ${dbsLocations.size} DBS markers")
        // Add DBS markers
        dbsLocations.forEach { location ->
            addCustomMarker(location, "DBS")
        }

        Log.d("MapDebug", "Finished adding stations. Total overlays: ${map.overlays.size}")
        map.invalidate() // Force refresh
    }


    private fun addCustomMarker(location: LocationData, iconType: String) {
        Log.d("MapDebug", "Adding marker: ${location.name} at ${location.lat}, ${location.lng}")

        val marker = Marker(map)
        marker.position = GeoPoint(location.lat, location.lng)
        marker.title = location.name
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Set custom large icon based on type
        val iconRes = when (iconType) {
            "MGS" -> R.drawable.ic_mgs_icon
            "DBS" -> R.drawable.ic_dbs_icon
            else -> {
                Log.e("MapDebug", "Unknown icon type: $iconType")
                return // Skip if unknown icon type
            }
        }

        try {
            val drawable = ContextCompat.getDrawable(requireContext(), iconRes)
            if (drawable != null) {
                marker.icon = drawable
                Log.d("MapDebug", "Icon set successfully for ${location.name}")
            } else {
                Log.e("MapDebug", "Drawable is null for ${location.name}")
                return
            }
        } catch (e: Exception) {
            Log.e("MapDebug", "Error setting icon for ${location.name}: ${e.message}")
            e.printStackTrace()
            return
        }

        marker.setOnMarkerClickListener { clickedMarker, mapView ->
            Toast.makeText(requireContext(), clickedMarker.title, Toast.LENGTH_SHORT).show()
            true
        }

        map.overlays.add(marker)
        Log.d("MapDebug", "Marker added to map: ${location.name}")
    }


    private fun setupHamburgerMenu() {
        hamburgerMenuBtn.setOnClickListener { toggleMenu() }
        menuOverlay.setOnClickListener { toggleMenu() }
    }

    private fun toggleMenu() {
        slideInMenu.post {
            val targetX = if (isMenuOpen) -slideInMenu.width.toFloat() else 0f
            slideInMenu.animate()
                .translationX(targetX)
                .setDuration(300)
                .start()
            menuOverlay.visibility = if (!isMenuOpen) View.VISIBLE else View.GONE
            isMenuOpen = !isMenuOpen
        }
    }

    // ALL LCV TRACKING FUNCTIONS COMMENTED OUT BELOW:

    /*
    private fun startLiveTracking() {
        val runnable = object : Runnable {
            override fun run() {
                fetchAllLCVData { vehicles ->
                    if (vehicles.isNotEmpty()) {
                        updateMapWithVehicles(vehicles)
                        lastUpdateTime = System.currentTimeMillis()
                    }
                }
                // Refresh every 15 seconds for more real-time updates
                updateHandler.postDelayed(this, 15000)
            }
        }
        updateHandler.post(runnable)
    }

    private fun fetchAllLCVData(onResult: (List<Vehicle>) -> Unit) {
        val url = "http://www.ctyf.co.in/api/companyvehiclelatestinfo?token=84F2A9BCF2&group=HPCL0123"
        val queue = Volley.newRequestQueue(requireContext())

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val vehiclesJson = response.getJSONArray("Vehicle")
                    val vehicles = mutableListOf<Vehicle>()

                    for (i in 0 until vehiclesJson.length()) {
                        val v = vehiclesJson.getJSONObject(i)
                        vehicles.add(
                            Vehicle(
                                v.getString("VehicleNo"),
                                v.getString("Imei"),
                                v.getString("Location"),
                                v.getString("Date"),
                                v.getString("Tempr"),
                                v.getString("Ignition"),
                                v.getString("Lat"),
                                v.getString("Long"),
                                v.getString("Speed"),
                                v.getString("Angle")
                            )
                        )
                    }
                    onResult(vehicles)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), "Error parsing vehicle data", Toast.LENGTH_SHORT).show()
                    onResult(emptyList())
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(requireContext(), "Error fetching vehicle data: ${error.message}", Toast.LENGTH_SHORT).show()
                onResult(emptyList())
            }
        )
        queue.add(request)
    }

    private fun updateMapWithVehicles(vehicles: List<Vehicle>) {
        var validVehicleCount = 0

        for ((index, vehicle) in vehicles.withIndex()) {
            try {
                val lat = vehicle.Lat.toDoubleOrNull()
                val lon = vehicle.Long.toDoubleOrNull()

                if (lat == null || lon == null || lat == 0.0 || lon == 0.0) {
                    continue // Skip invalid coordinates
                }

                val point = GeoPoint(lat, lon)
                validVehicleCount++

                // Create or update marker
                val marker = vehicleMarkers[vehicle.VehicleNo] ?: createVehicleMarker(vehicle.VehicleNo, index)

                // Update marker position
                marker.position = point
                marker.title = vehicle.VehicleNo

                // Create detailed info for marker
                val ignitionStatus = if (vehicle.Ignition == "1") "ON" else "OFF"
                val speedKmh = "${vehicle.Speed} km/h"
                val temperature = "${vehicle.Tempr}°C"
                val lastUpdate = formatDate(vehicle.Date)

                marker.snippet = buildString {
                    append("Speed: $speedKmh\n")
                    append("Ignition: $ignitionStatus\n")
                    append("Temp: $temperature\n")
                    append("Location: ${vehicle.Location}\n")
                    append("Last Update: $lastUpdate")
                }

                // Update path for moving vehicles
                updateVehiclePath(vehicle, point, index)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Remove markers for vehicles that are no longer in the response
        val currentVehicleNos = vehicles.map { it.VehicleNo }.toSet()
        val markersToRemove = vehicleMarkers.keys.filter { it !in currentVehicleNos }

        for (vehicleNo in markersToRemove) {
            vehicleMarkers[vehicleNo]?.let { marker ->
                map.overlays.remove(marker)
            }
            vehicleMarkers.remove(vehicleNo)

            vehiclePaths[vehicleNo]?.let { path ->
                map.overlays.remove(path)
            }
            vehiclePaths.remove(vehicleNo)
            vehiclePathPoints.remove(vehicleNo)
        }

        map.invalidate()

        // Show status in toast (optional)
        if (validVehicleCount > 0) {
            Toast.makeText(requireContext(), "Updated $validVehicleCount vehicles", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createVehicleMarker(vehicleNo: String, colorIndex: Int): Marker {
        val marker = Marker(map)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        // Set different colors for different vehicles
        val color = vehicleColors[colorIndex % vehicleColors.size]

        // You can customize marker icons here
        // marker.icon = createCustomMarkerIcon(color)

        map.overlays.add(marker)
        vehicleMarkers[vehicleNo] = marker
        return marker
    }

    private fun updateVehiclePath(vehicle: Vehicle, currentPoint: GeoPoint, colorIndex: Int) {
        // Only create paths for moving vehicles or those with ignition on
        val isMoving = vehicle.Speed.toIntOrNull() ?: 0 > 0
        val ignitionOn = vehicle.Ignition == "1"

        if (!isMoving && !ignitionOn) return

        // Get or create path points list
        val pathPointsList = vehiclePathPoints.getOrPut(vehicle.VehicleNo) { mutableListOf() }

        // Add new point if it's significantly different from the last point
        if (pathPointsList.isEmpty() ||
            pathPointsList.last().distanceToAsDouble(currentPoint) > 10) { // 10 meter threshold
            pathPointsList.add(currentPoint)

            // Limit path points to avoid memory issues (keep last 100 points)
            if (pathPointsList.size > 100) {
                pathPointsList.removeAt(0)
            }
        }

        // Create or update polyline
        if (pathPointsList.size > 1) {
            val polyline = vehiclePaths[vehicle.VehicleNo] ?: Polyline().apply {
                color = vehicleColors[colorIndex % vehicleColors.size]
                width = 4f
                map.overlays.add(this)
                vehiclePaths[vehicle.VehicleNo] = this
            }

            polyline.setPoints(pathPointsList.toList())
        }
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
    */

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Clean up handlers to prevent memory leaks - COMMENTED OUT
        // updateHandler.removeCallbacksAndMessages(null)
        map.onDetach()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // Clear old path points to free memory - COMMENTED OUT
        // vehiclePathPoints.values.forEach { points ->
        //     if (points.size > 50) {
        //         points.subList(0, points.size - 50).clear()
        //     }
        // }
    }
}
