package ru.gb.weatherapprebuild.repository

import okhttp3.Callback
import ru.gb.weatherapprebuild.model.WeatherDTO

interface DetailsRepository {
    fun getWeatherDetailsFromServer(
        lat: Double,
        lon: Double,
        callback: retrofit2.Callback<WeatherDTO>
    )
}