package ru.gb.weatherapprebuild.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.gb.weatherapprebuild.App
import ru.gb.weatherapprebuild.repository.LocalRepositoryImpl

class HistoryViewModel(
    val historyLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val historyRepositoryImpl: LocalRepositoryImpl = LocalRepositoryImpl(App.getHistoryDao())
) : ViewModel() {

    fun getAllHistory() {
        historyLiveData.value = AppState.Loading
        historyLiveData.value = AppState.Success(historyRepositoryImpl.getAllHistory())
    }
}