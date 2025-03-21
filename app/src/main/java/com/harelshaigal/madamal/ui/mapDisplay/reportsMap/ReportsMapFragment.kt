package com.harelshaigal.aroundtw.ui.mapDisplay.reportsMap

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.harelshaigal.aroundtw.data.report.Report
import com.harelshaigal.aroundtw.data.report.ReportWithUser
import com.harelshaigal.aroundtw.databinding.FragmentReportsMapBinding
import com.harelshaigal.aroundtw.helpers.LocationHelper
import com.harelshaigal.aroundtw.ui.mapDisplay.reportMapDisplay.ReportMapDisplayFragment
import kotlin.math.*

class ReportsMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var viewModel: ReportsMapViewModel
    private var _binding: FragmentReportsMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private var googleMapRef: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ReportsMapViewModel::class.java]
        _binding = FragmentReportsMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        return root
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMapRef = googleMap

        googleMap.setOnMarkerClickListener(this)

        val location = LatLng(LocationHelper.lat, LocationHelper.lng)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))

        getUserLocation()
    }


    fun toRadians(degrees: Double): Double {
        return degrees * Math.PI / 180
    }

//    private fun observeReports() {
//        viewModel.reportList.observe(viewLifecycleOwner) { reports ->
//            updateMapMarkers(reports.map { it.report })
//        }
//    }

    private fun haversineDistance(lon1: Double, lat1: Double, lon2: Double, lat2: Double): Double {
        val R = 6371000.0 // Earth radius in meters
        val φ1 = toRadians(lat1)
        val φ2 = toRadians(lat2)
        val Δφ = toRadians(lat2 - lat1)
        val Δλ = toRadians(lon2 - lon1)

        val a = sin(Δφ / 2) * sin(Δφ / 2) +
                cos(φ1) * cos(φ2) *
                sin(Δλ / 2) * sin(Δλ / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c // Distance in meters
    }


    private fun updateMapMarkers(reports: List<ReportWithUser>, userLocation:LatLng) {
//        googleMapRef?.clear()
        Log.d("ReportsMapFragment", "Updating map markers with ${reports.size} reports")
        for (report in reports) {
            if (report.report.lat != null && report.report.lng != null) {
                val reportMarker = LatLng(report.report.lat, report.report.lng)
                val markerOptions = MarkerOptions().position(reportMarker).title(report.report.data)
                val distance = haversineDistance(
                    userLocation.longitude, userLocation.latitude,
                    report.report.lng, report.report.lat
                )
                Log.d("ReportsMapFragment", "Distance: $distance meters")
                if(distance <= 50000) {
                    Log.d("ReportsMapFragment", "adding location at: $distance meters")
                    googleMapRef?.addMarker(markerOptions)?.tag = report.report.id

                }
            }
        }

    }



    @SuppressLint("MissingPermission")
    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                viewModel.reportList.observe(viewLifecycleOwner) { reports ->
                    updateMapMarkers(reports,userLatLng)
                }

                val markerOptions = MarkerOptions()
                    .position(userLatLng)
                    .title("You are here")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                googleMapRef?.addMarker(markerOptions)?.tag = "USER_LOCATION"

                // Add a Circle with 5KM Radius
                googleMapRef?.addCircle(
                    CircleOptions()
                        .center(userLatLng)
                        .radius(50000.0) // 5 KM
                        .strokeWidth(2f)
                        .strokeColor(0xFF0000FF.toInt()) // Blue Border
                        .fillColor(0x220000FF) // Light Transparent Blue Fill
                )

                googleMapRef?.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12f))
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val tag = marker.tag
        if (tag == "USER_LOCATION") return false // Ignore user location marker

        if (tag is String) {
            ReportMapDisplayFragment.display(parentFragmentManager, tag)
            return true
        }
        return false
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
