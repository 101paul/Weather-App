package com.example.weatherapp

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("kolkata")
        SearchCity()
    }

    private fun SearchCity() {
        val searchview = binding.searchView
        searchview.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true;
            }

        })
    }

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherData(cityName, "2c6f202f815dcd065a0360709f19da0a", "metric")
        response.enqueue(object : Callback<weatherApp> {

            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    val localeTime = responseBody.timezone.toString()
                    val feelslike = responseBody.main.feels_like

//                    Log.d("TAG","onResponse : $temperature")
                    binding.temp.text = "$temperature°C"
                    binding.weather.text = condition
//                    binding.maxTemp.text = "Max Temp $maxTemp °C"
//                    binding.minTemp.text = "Min Temp $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.wind.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sealevel.text = "$seaLevel hPa"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.cityName.text = "$cityName"
                    binding.date.text = date()
                    binding.feellike.text = "feels like $feelslike°C"
                    binding.xyz.text = "$condition"
                    changeImagesAccordingtoWeather(condition)
                }

            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {
            }

        })
    }

    private fun changeImagesAccordingtoWeather(condition: String) {
        when (condition) {
            "Haze" -> {
                binding.weatherIcon.setBackgroundResource(R.drawable.cloudy2)
            }

            "Sunny", "Clear", "Clear sky" -> {
                binding.weatherIcon.setBackgroundResource(R.drawable.sun)
            }

            "Windy", "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.weatherIcon.setBackgroundResource(R.drawable.windy)
            }

            "Rainy", "Light Rain", "Moderate Rain", "Drizzle" -> {
                binding.weatherIcon.setBackgroundResource(R.drawable.rain1)
            }

            "Storm", "Heavy Rain", "Showers" -> {
                binding.weatherIcon.setBackgroundResource(R.drawable.storm)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.weatherIcon.setBackgroundResource(R.drawable.snow)
            }

        }
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM YYYY", Locale.getDefault())
        return sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp * 1000)))
    }
}