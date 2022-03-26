package ru.gb.weatherapprebuild.view.details


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*
import okhttp3.*
import ru.gb.weatherapprebuild.BuildConfig
import ru.gb.weatherapprebuild.R
import ru.gb.weatherapprebuild.databinding.FragmentDetailsBinding
import ru.gb.weatherapprebuild.model.City
import ru.gb.weatherapprebuild.model.FactDTO
import ru.gb.weatherapprebuild.model.Weather
import ru.gb.weatherapprebuild.model.WeatherDTO
import ru.gb.weatherapprebuild.utils.showSnackBar
import ru.gb.weatherapprebuild.viewmodel.AppState
import ru.gb.weatherapprebuild.viewmodel.DetailsViewModel
import java.io.IOException

const val DETAILS_INTENT_FILTER = "DETAILS INTENT FILTER"
const val DETAILS_LOAD_RESULT_EXTRA = "LOAD RESULT"
const val DETAILS_INTENT_EMPTY_EXTRA = "INTENT IS EMPTY"
const val DETAILS_DATA_EMPTY_EXTRA = "DATA IS EMPTY"
const val DETAILS_RESPONSE_EMPTY_EXTRA = "RESPONSE IS EMPTY"
const val DETAILS_REQUEST_ERROR_EXTRA = "REQUEST ERROR"
const val DETAILS_REQUEST_ERROR_MESSAGE_EXTRA = "REQUEST ERROR MESSAGE"
const val DETAILS_URL_MALFORMED_EXTRA = "URL MALFORMED"
const val DETAILS_RESPONSE_SUCCESS_EXTRA = "RESPONSE SUCCESS"
const val DETAILS_TEMP_EXTRA = "TEMPERATURE"
const val DETAILS_FEELS_LIKE_EXTRA = "FEELS LIKE"
const val DETAILS_CONDITION_EXTRA = "CONDITION"
private const val TEMP_INVALID = -100
private const val FEELS_LIKE_INVALID = -100

class DetailsFragment : Fragment(R.layout.main_fragment) {
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var weatherBundle: Weather

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this).get(DetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherBundle = arguments?.getParcelable<Weather>(BUNDLE_EXTRA) ?: Weather()

        viewModel.detailsLiveData.observe(viewLifecycleOwner, Observer { renderData(it) })
        requestWeather()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.mainView.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.GONE
                setWeather(appState.weatherData[0])
            }
            is AppState.Loading -> {
                binding.mainView.visibility = View.GONE
                binding.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.mainView.visibility = View.VISIBLE
                binding.loadingLayout.visibility = View.GONE
                binding.mainView.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    {
                        requestWeather()
                    })
            }
        }
    }

    fun requestWeather() {
        viewModel.getWeatherFromRemoteSource(weatherBundle.city.lat, weatherBundle.city.lon)
    }

    private fun setWeather(weather: Weather) {
        with(binding) {
            val city = weatherBundle.city

            saveCity(city, weather)

            cityName.text = city.city
            cityCoordinates.text = String.format(
                getString(R.string.city_coordinates),
                city.lat.toString(),
                city.lon.toString()
            )
            temperatureValue.text = weather.temperature.toString()
            feelsLikeValue.text = weather.feelsLike.toString()
            weatherCondition.text = weather.condition

            Picasso
                .get()
                .load("https://freepngimg.com/thumb/city/36275-3-city-hd.png")
                .into(headerIcon)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveCity(
        city: City,
        weather: Weather
    ) {
        viewModel.saveCityToDB(Weather(
            city,
            weather.temperature,
            weather.feelsLike,
            weather.condition)
        )
    }

    companion object {
        const val BUNDLE_EXTRA = "weather"

        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}