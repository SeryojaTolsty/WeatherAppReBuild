package ru.gb.weatherapprebuild.utils

import ru.gb.weatherapprebuild.model.FactDTO
import ru.gb.weatherapprebuild.model.Weather
import ru.gb.weatherapprebuild.model.WeatherDTO
import ru.gb.weatherapprebuild.model.getDefaultCity

fun convertDtoToModel(weatherDTO: WeatherDTO): List<Weather> {
    val fact: FactDTO = weatherDTO.fact!! // 100 проц гарантии(!!) не давать
    return listOf(Weather(getDefaultCity(), fact.temp!!, fact.feels_like!!, fact.condition!!))
}