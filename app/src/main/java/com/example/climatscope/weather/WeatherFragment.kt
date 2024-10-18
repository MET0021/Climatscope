package com.example.climatscope.weather

import android.health.connect.datatypes.units.Pressure
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import com.example.climatscope.App
import com.example.climatscope.R
import com.example.climatscope.openweathermap.WeatherWrapper
import com.example.climatscope.openweathermap.mapOpenWeatherDataToWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherFragment : Fragment() {

    private val TAG = WeatherFragment::class.java.simpleName
    private lateinit var cityName : String

    private lateinit var weatherIcon: ImageView
    private lateinit var weatherDescription: TextView
    private lateinit var temperature: TextView
    private lateinit var humidity: TextView
    private lateinit var pressure: TextView
    private lateinit var city: TextView
    private lateinit var refreshLayout: SwipeRefreshLayout
    companion object {

        val EXTRA_CITY_NAME = "EXTRA_CITY"
        fun newInstance()= WeatherFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        refreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        city = view.findViewById(R.id.city)
        weatherIcon = view.findViewById(R.id.weather_icon)
        weatherDescription = view.findViewById(R.id.weather_description)
        temperature = view.findViewById(R.id.temperature)
        humidity = view.findViewById(R.id.humidity)
        pressure = view.findViewById(R.id.pressure)

        refreshLayout.setOnRefreshListener { refreshWeather() }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity?.intent!!.hasExtra(EXTRA_CITY_NAME)){
            requireActivity().intent.getStringExtra(EXTRA_CITY_NAME)
                ?.let { updateWeatherForCity(it) }
        }
    }

    fun updateWeatherForCity(cityName: String) {
        this.cityName = cityName
        val call= App.weatherService.getWeather("$cityName,fr")

        if (!refreshLayout.isRefreshing) {
            refreshLayout.isRefreshing = true
        }
        call.enqueue(object :Callback<WeatherWrapper>{
            override fun onResponse(
                call: Call<WeatherWrapper>,
                response: Response<WeatherWrapper>
            ) {
                response.body()?.let {
                    val weather = mapOpenWeatherDataToWeather(it)
                    city.text = cityName
                    updateWeatherUi(weather)
                    refreshLayout.isRefreshing = false
                    Log.i(TAG, "Weather response: $weather")
                }

            }

            override fun onFailure(call: Call<WeatherWrapper>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
                refreshLayout.isRefreshing = false
                Toast.makeText(context, "Error, Could not load City weather", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun updateWeatherUi(weather: Weather) {
        weatherDescription.text = weather.description
        temperature.text =
            getString(R.string.weather_temperature_value, weather.temperature.toInt())
        humidity.text = getString(R.string.weather_humidity_value, weather.humidity)
        pressure.text = getString(R.string.weather_pressure_value, weather.pressure)
        weatherIcon.load(weather.iconUrl) {
            placeholder(R.drawable.ic_launcher_foreground)
            fallback(R.drawable.ic_launcher_background)
        }
        //Log.e(TAG, "updateWeatherUi: ", )
    }

    private fun refreshWeather() {
        updateWeatherForCity(cityName)
    }

    fun clearUi() {
        weatherIcon.setImageResource(R.mipmap.ic_launcher)
        cityName = ""
        city.text = ""
        weatherDescription.text = ""
        temperature.text = ""
        humidity.text = ""
        pressure.text = ""

    }
}