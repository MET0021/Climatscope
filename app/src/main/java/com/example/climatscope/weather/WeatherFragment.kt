package com.example.climatscope.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
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
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity?.intent!!.hasExtra(EXTRA_CITY_NAME)){
            requireActivity().intent.getStringExtra(EXTRA_CITY_NAME)
                ?.let { updateWeatherForCity(it) }
        }
    }

    private fun updateWeatherForCity(cityName: String) {
        this.cityName = cityName
        val call= App.weatherService.getWeather("$cityName,fr")
        call.enqueue(object :Callback<WeatherWrapper>{
            override fun onResponse(
                call: Call<WeatherWrapper>,
                response: Response<WeatherWrapper>
            ) {
                response.body()?.let {
                    val weather = mapOpenWeatherDataToWeather(it)
                    Log.i(TAG, "Weather response: $weather")
                }

            }

            override fun onFailure(call: Call<WeatherWrapper>, t: Throwable) {
                Log.e(TAG, "onFailure: ", t)
                Toast.makeText(context, "Error, Could not load City weather", Toast.LENGTH_SHORT).show()
            }

        })
    }
}