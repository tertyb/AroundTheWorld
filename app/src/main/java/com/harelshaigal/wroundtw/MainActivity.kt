package com.harelshaigal.aroundtw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.harelshaigal.aroundtw.data.report.ReportRepository
import com.harelshaigal.aroundtw.data.user.UserRepository
import com.harelshaigal.aroundtw.helpers.LocationHelper
import com.harelshaigal.aroundtw.R
import com.harelshaigal.aroundtw.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val userRepository: UserRepository = UserRepository()
    private val reportRepository: ReportRepository = ReportRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)
        userRepository.startUserFetching(Firebase.auth.currentUser?.uid)
//        reportRepository.startReportsFetching()

        // Request location permission
        LocationHelper.requestLocationPermission(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        userRepository.endUserFetching()
        reportRepository.endReportsFetching()
    }
}