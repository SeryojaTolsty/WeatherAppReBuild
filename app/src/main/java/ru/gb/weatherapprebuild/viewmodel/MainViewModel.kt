package ru.gb.weatherapprebuild.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.gb.weatherapprebuild.model.Repo
import ru.gb.weatherapprebuild.model.RepoImpl
import java.lang.Thread.sleep

class MainViewModel(
    private val liveDataToObserve : MutableLiveData<AppState> = MutableLiveData(),
    private val repositoryImpl: Repo = RepoImpl()
)
    : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getWeatherFromRemoteSource() = getDataFromLocalSource(isRussian = true)

    fun getWeatherFromLocalSourceRus() = getDataFromLocalSource(isRussian = true)
    fun getWeatherFromLocalSourceWorld()  = getDataFromLocalSource(isRussian = false)

    private fun getDataFromLocalSource(isRussian : Boolean) {
        liveDataToObserve.value = AppState.Loading
        Thread {
            sleep(2000)
            liveDataToObserve.postValue(AppState.Success(
                if (isRussian)
                    repositoryImpl.getWeatherFromLocalStorageRus()
                else
                    repositoryImpl.getWeatherFromLocalStorageWorld()))
        }.start()
    }
}