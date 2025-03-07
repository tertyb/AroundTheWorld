package com.harelshaigal.madamal.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

object LocationHelper {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var lat: Double = 0.0
    var lng: Double = 0.0

    fun initialize(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (checkLocationPermission(context)) {
            requestLocation(context)
        }
    }

    private fun checkLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocation(context: Context) {
        if (checkLocationPermission(context)) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        lat = location.latitude
                        lng = location.longitude
                    }
                }
        }
    }

    // Method to request permissions should be called from an Activity
    fun requestLocationPermission(activity: androidx.appcompat.app.AppCompatActivity) {
        if (!checkLocationPermission(activity)) {
            androidx.core.app.ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
}
