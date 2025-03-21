package com.harelshaigal.aroundtw.ui.appbar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.harelshaigal.aroundtw.R
import com.harelshaigal.aroundtw.ui.login.LoginActivity
import com.harelshaigal.aroundtw.viewmodel.WeatherViewModel
import com.harelshaigal.aroundtw.databinding.FragmentAppbarBinding

class AppbarFragment : Fragment() {
    private var _binding: FragmentAppbarBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppbarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        weatherViewModel = ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutButton.setOnClickListener {
            LogoutDialog.createLogoutDialog(requireContext()) {
                navigateToLogin()
            }
        }

       weatherViewModel.weatherData.observe(viewLifecycleOwner) { weather ->
           weather?.let {
                val currentTemperature =
                    weather.properties.timeseries[0].data.instant.details.air_temperature
                val weatherLabel = context?.getString(R.string.currentWeather)
                binding.temperature.text = "${currentTemperature}°C"
           } ?: run {
                binding.temperature.text = context?.getString(R.string.noWeather)
            }
        }


        weatherViewModel.fetchWeather()
    }

    private fun navigateToLogin() {
        val loginIntent = Intent(context, LoginActivity::class.java)
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
        activity?.finish()
    }
}
