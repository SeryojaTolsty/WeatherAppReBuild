package ru.gb.weatherapprebuild.repository

import ru.gb.weatherapprebuild.model.Weather

interface LocalRepository {
    fun getAllHistory(): List<Weather>
    fun saveEntity(weather: Weather)
}