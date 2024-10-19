package com.myapp.climatscope.city

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.myapp.climatscope.R
import com.myapp.climatscope.weather.WeatherActivity
import com.myapp.climatscope.weather.WeatherFragment

class CityActivity : AppCompatActivity(), CityFragment.CityFragmentListener {

    private lateinit var cityFragment: CityFragment
    private var weatherFragment: WeatherFragment? = null
    private var currentCity: City? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)
        cityFragment = supportFragmentManager.findFragmentById(R.id.fragment_city) as CityFragment
        cityFragment.listener = this
        weatherFragment =
            supportFragmentManager.findFragmentById(R.id.weather_fragment) as WeatherFragment?
    }

    override fun onCitySelected(city: City) {
        currentCity = city
        if (isHandSetLayout()) {
            startWeatherActivity(city)
        } else {
            weatherFragment?.updateWeatherForCity(city.name)
        }

    }

    override fun onEmptyCities() {
        weatherFragment?.clearUi()
    }

    private fun isHandSetLayout(): Boolean = weatherFragment == null

    private fun startWeatherActivity(city: City) {
        val intent = Intent(this, WeatherActivity::class.java)
        intent.putExtra(WeatherFragment.EXTRA_CITY_NAME, city.name)
        startActivity(intent)
        Log.e("YASSER3.0", "onCreate: ${intent}", )
    }
}