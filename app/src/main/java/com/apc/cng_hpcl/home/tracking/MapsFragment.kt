package com.apc.cng_hpcl.home.tracking

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.apc.cng_hpcl.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class VehicleData(
    val vehicleId: String,
    val regNo: String,
    val latitude: Double,
    val longitude: Double,
    val speed: Int,
    val address: String,
    val lastSeen: String,
    val ignitionStatus: String,
    val status: String,
    val vehicleType: String,
    val customMarker: String,
    val direction: String,
    val altitude: String?
)

data class PersistentTrackPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val speed: Int
)

class MapsFragment : Fragment() {
    private lateinit var map: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var userLocationMarker: Marker? = null
    private var vehicleMarker: Marker? = null
    private var trackingPolyline: Polyline? = null

    // UI Components
    private lateinit var speedIndicator: TextView
    private lateinit var speedUnit: TextView

    // Enhanced tracking variables
    private val vehicleTrackingPoints = mutableListOf<GeoPoint>()
    private val persistentTrackingPoints = mutableListOf<PersistentTrackPoint>()
    private val updateHandler = Handler(Looper.getMainLooper())
    private var trackingRunnable: Runnable? = null
    private val updateInterval = 3000L // Reduced to 3 seconds for real-time feel

    // SharedPreferences for persistent storage
    private lateinit var trackingPrefs: SharedPreferences
    private val gson = Gson()

    // Vamosys API configuration
    private val baseApiUrl = "https://api.vamosys.com/mobile/getGrpDataForTrustedClients"
    private val providerName = "SUMITGUPTA"
    private val fcode = "wom"
    private val vehicleId = "UP16BC1531"

    // Route snapping variables
    private var currentSpeed = 0
    private var lastKnownLocation: GeoPoint? = null

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        Configuration.getInstance().userAgentValue = requireContext().packageName

        // Initialize components
        map = view.findViewById(R.id.osmMap)
        speedIndicator = view.findViewById(R.id.speedIndicator)
        speedUnit = view.findViewById(R.id.speedUnit)

        // Initialize SharedPreferences
        trackingPrefs = requireContext().getSharedPreferences("vehicle_tracking", Context.MODE_PRIVATE)

        setupMap()
        setupSpeedIndicator()
        loadPersistentTrackingData()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getUserLocation()
        startVehicleTracking()

        return view
    }

    private fun setupMap() {
        map.apply {
            setMultiTouchControls(true)
            controller.setZoom(16.0)
            isClickable = true
            setBuiltInZoomControls(true)
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setCenter(GeoPoint(28.6139, 77.2090))
        }
    }

    private fun setupSpeedIndicator() {
        speedIndicator.apply {
            text = "0"
            textSize = 24f
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        }
        speedUnit.apply {
            text = "km/h"
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        }
        updateSpeedIndicator(0)
    }

    private fun updateSpeedIndicator(speed: Int) {
        currentSpeed = speed
        speedIndicator.text = speed.toString()

        // Change color based on speed
        val color = when {
            speed == 0 -> ContextCompat.getColor(requireContext(), android.R.color.darker_gray)
            speed <= 40 -> ContextCompat.getColor(requireContext(), android.R.color.holo_green_light)
            speed <= 80 -> ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light)
            else -> ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
        }
        speedIndicator.setTextColor(color)
    }

    // Load persistent tracking data from SharedPreferences
    private fun loadPersistentTrackingData() {
        val trackingDataJson = trackingPrefs.getString("tracking_points", "[]")
        val type = object : TypeToken<List<PersistentTrackPoint>>() {}.type
        val loadedPoints: List<PersistentTrackPoint> = gson.fromJson(trackingDataJson, type) ?: emptyList()

        persistentTrackingPoints.clear()
        persistentTrackingPoints.addAll(loadedPoints)

        // Convert to GeoPoints and update map
        vehicleTrackingPoints.clear()
        persistentTrackingPoints.forEach { point ->
            vehicleTrackingPoints.add(GeoPoint(point.latitude, point.longitude))
        }

        if (vehicleTrackingPoints.isNotEmpty()) {
            updateTrackingPolyline()
        }

        Log.d("MapsFragment", "Loaded ${persistentTrackingPoints.size} persistent tracking points")
    }

    // Save tracking data to SharedPreferences
    private fun savePersistentTrackingData() {
        val trackingDataJson = gson.toJson(persistentTrackingPoints)
        trackingPrefs.edit().putString("tracking_points", trackingDataJson).apply()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun getUserLocation() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000 // Reduced to 10 seconds
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest, object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        updateUserLocationMarker(location.latitude, location.longitude)
                    }
                }
            }, Looper.getMainLooper()
        )
    }

    private fun startVehicleTracking() {
        trackingRunnable = object : Runnable {
            override fun run() {
                fetchVehicleData()
                updateHandler.postDelayed(this, updateInterval)
            }
        }
        updateHandler.post(trackingRunnable!!)
    }

    private fun fetchVehicleData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val vehicleData = makeApiCall()
                withContext(Dispatchers.Main) {
                    vehicleData?.let {
                        updateVehicleMarker(it)
                        updateSpeedIndicator(it.speed)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("MapsFragment", "Error fetching vehicle data", e)
                    Toast.makeText(context, "Failed to fetch vehicle data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun makeApiCall(): VehicleData? {
        return withContext(Dispatchers.IO) {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val currentDate = dateFormat.format(Date())

                val apiUrl = "$baseApiUrl?" +
                        "providerName=$providerName&" +
                        "fcode=$fcode&" +
                        "vehicleId=$vehicleId&" +
                        "fromDate=$currentDate&" +
                        "fromTime=00:00:00&" +
                        "toDate=$currentDate&" +
                        "toTime=23:59:00"

                val url = URL(apiUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 10000 // Reduced timeout
                    readTimeout = 10000
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "VehicleTrackingApp/1.0")
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()
                    parseVehicleData(response)
                } else {
                    Log.e("MapsFragment", "API call failed with response code: $responseCode")
                    null
                }
            } catch (e: Exception) {
                Log.e("MapsFragment", "Network error", e)
                null
            }
        }
    }

    private fun parseVehicleData(jsonResponse: String): VehicleData? {
        return try {
            val jsonArray = JSONArray(jsonResponse)
            if (jsonArray.length() > 0) {
                val vehicleJson = jsonArray.getJSONObject(jsonArray.length() - 1)
                VehicleData(
                    vehicleId = vehicleJson.optString("vehicleId", ""),
                    regNo = vehicleJson.optString("regNo", ""),
                    latitude = vehicleJson.optDouble("lat", vehicleJson.optDouble("latitude", 0.0)),
                    longitude = vehicleJson.optDouble("lng", vehicleJson.optDouble("longitude", 0.0)),
                    speed = vehicleJson.optInt("speed", 0),
                    address = vehicleJson.optString("address", "Unknown location"),
                    lastSeen = vehicleJson.optString("lastSeen", ""),
                    ignitionStatus = vehicleJson.optString("ignitionStatus", "UNKNOWN"),
                    status = vehicleJson.optString("status", "UNKNOWN"),
                    vehicleType = vehicleJson.optString("vehicleType", "Vehicle"),
                    customMarker = vehicleJson.optString("customMarker", "car"),
                    direction = vehicleJson.optString("direction", "N"),
                    altitude = vehicleJson.optString("altitude", null)
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MapsFragment", "Error parsing vehicle data", e)
            null
        }
    }

    private fun updateUserLocationMarker(lat: Double, lng: Double) {
        val geoPoint = GeoPoint(lat, lng)
        if (userLocationMarker == null) {
            userLocationMarker = Marker(map).apply {
                position = geoPoint
                title = "Your Location"
                snippet = "You are here"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_my_location)
            }
            map.overlays.add(userLocationMarker)
        } else {
            userLocationMarker!!.position = geoPoint
        }
        map.invalidate()
    }

    private fun updateVehicleMarker(vehicleData: VehicleData) {
        val geoPoint = GeoPoint(vehicleData.latitude, vehicleData.longitude)

        // Add to persistent tracking with timestamp
        val trackPoint = PersistentTrackPoint(
            vehicleData.latitude,
            vehicleData.longitude,
            System.currentTimeMillis(),
            vehicleData.speed
        )
        persistentTrackingPoints.add(trackPoint)

        // Route snapping logic
        val snappedPoint = if (lastKnownLocation != null) {
            snapToRoute(lastKnownLocation!!, geoPoint)
        } else {
            geoPoint
        }

        vehicleTrackingPoints.add(snappedPoint)
        lastKnownLocation = snappedPoint

        // Save persistent data
        savePersistentTrackingData()

        if (vehicleMarker == null) {
            vehicleMarker = Marker(map).apply {
                position = snappedPoint
                title = "${vehicleData.regNo} (${vehicleData.vehicleType})"
                snippet = buildVehicleInfo(vehicleData)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = getVehicleIcon(vehicleData)
            }
            map.overlays.add(vehicleMarker)
            map.controller.animateTo(snappedPoint)
        } else {
            vehicleMarker!!.position = snappedPoint
            vehicleMarker!!.snippet = buildVehicleInfo(vehicleData)
            vehicleMarker!!.icon = getVehicleIcon(vehicleData)
        }

        updateTrackingPolyline()
        map.invalidate()
    }

    // Simple route snapping algorithm (can be enhanced with actual routing API)
    private fun snapToRoute(fromPoint: GeoPoint, toPoint: GeoPoint): GeoPoint {
        // For now, return the original point
        // In production, you would call a routing service like OSRM or GraphHopper
        // to get the route between points and snap to roads

        // Example of how you could implement route snapping:
        // 1. Call routing API between fromPoint and toPoint
        // 2. Get the route waypoints
        // 3. Return the closest waypoint to toPoint

        return toPoint // Placeholder implementation
    }

    private fun buildVehicleInfo(data: VehicleData): String {
        return """
            Vehicle: ${data.regNo}
            Speed: ${data.speed} km/h
            Status: ${data.status}
            Ignition: ${data.ignitionStatus}
            Direction: ${data.direction}
            Last Update: ${data.lastSeen}
            ${if (data.address.isNotEmpty()) "Location: ${data.address}" else ""}
        """.trimIndent().replace("\n\n", "\n")
    }

    private fun getVehicleIcon(vehicleData: VehicleData): Drawable? {
        return when {
            vehicleData.status == "OFF" -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_vehicle_off)
            vehicleData.speed > 0 -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_vehicle_moving)
            else -> ContextCompat.getDrawable(requireContext(), R.drawable.ic_vehicle_idle)
        }
    }

    private fun updateTrackingPolyline() {
        if (vehicleTrackingPoints.size < 2) return

        trackingPolyline?.let { map.overlays.remove(it) }

        // Create enhanced polyline with all persistent points (uneraseable)
        trackingPolyline = Polyline().apply {
            setPoints(vehicleTrackingPoints) // Use all points, not just last 50
            color = Color.BLUE
            width = 8f
        }

        map.overlays.add(trackingPolyline)
    }

    // Enhanced functions
    fun centerOnVehicle() {
        vehicleMarker?.let { marker ->
            map.controller.animateTo(marker.position)
        }
    }

    fun centerOnUser() {
        userLocationMarker?.let { marker ->
            map.controller.animateTo(marker.position)
        }
    }

    fun toggleTrackingPath(show: Boolean) {
        trackingPolyline?.let { polyline ->
            if (show) {
                if (!map.overlays.contains(polyline)) {
                    map.overlays.add(polyline)
                }
                else {}
            } else {
                map.overlays.remove(polyline)
            }
        }
        map.invalidate()
    }

    // Modified to not clear persistent data
    fun clearTrackingHistory() {
        // Only clear from UI, not from persistent storage
        vehicleTrackingPoints.clear()
        trackingPolyline?.let { map.overlays.remove(it) }
        trackingPolyline = null
        map.invalidate()

        Log.d("MapsFragment", "Cleared UI tracking history but kept persistent data")
    }

    // New function to permanently delete tracking history
    fun permanentlyDeleteTrackingHistory() {
        vehicleTrackingPoints.clear()
        persistentTrackingPoints.clear()
        savePersistentTrackingData()
        trackingPolyline?.let { map.overlays.remove(it) }
        trackingPolyline = null
        map.invalidate()

        Toast.makeText(context, "All tracking history permanently deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        startVehicleTracking()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        stopVehicleTracking()
        savePersistentTrackingData()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVehicleTracking()
        savePersistentTrackingData()
    }

    private fun stopVehicleTracking() {
        trackingRunnable?.let { updateHandler.removeCallbacks(it) }
    }
}
