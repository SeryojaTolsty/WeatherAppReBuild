package ru.gb.weatherapprebuild.repository

import ru.gb.weatherapprebuild.model.Weather
import ru.gb.weatherapprebuild.model.room.HistoryDao
import ru.gb.weatherapprebuild.utils.convertHistoryEntityToWeather
import ru.gb.weatherapprebuild.utils.convertWeatherToEntity

class LocalRepositoryImpl(private val localDataSource: HistoryDao) : LocalRepository {
    override fun getAllHistory(): List<Weather> {
        return convertHistoryEntityToWeather(localDataSource.all())
    }

    override fun saveEntity(weather: Weather) {
        localDataSource.insert(convertWeatherToEntity(weather))
    }
}