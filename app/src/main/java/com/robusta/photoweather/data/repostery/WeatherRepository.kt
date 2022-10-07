package com.robusta.photoweather.data.repostery

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import com.robusta.photoweather.api.RetrofitService
import com.robusta.photoweather.api.WeatherApi
import com.robusta.photoweather.data.response.WeatherResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class WeatherRepository {

    private val weatherService: WeatherApi = RetrofitService.createService(WeatherApi::class.java)

    fun getWeather(lat: Double,long: Double): MutableLiveData<WeatherResponse> {
        val weatherData = MutableLiveData<WeatherResponse>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = weatherService.getWeather(lat,long)
                try {
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                Log.d("WeatherRepository",it.toString())

                                weatherData.postValue(it)
                            }
                        } else {
                            weatherData.postValue(null)
                            Log.e("WeatherRepository", "Exception")
                        }
                    }
                } catch (e: HttpException) {
                    weatherData.postValue(null)
                    Log.e("WeatherRepository", "Exception ${e.message}")
                } catch (e: Throwable) {
                    weatherData.postValue(null)
                    Log.e("WeatherRepository", "Ooops: Something else went wrong")
                }
            }
            catch (e: Throwable) {
                weatherData.postValue(null)
                Log.i("REQUEST",e.localizedMessage)
            }
        }
        return weatherData
    }

    companion object {
        private var weatherRepository: WeatherRepository? = null

        val instance: WeatherRepository
            get() {
                if (weatherRepository == null) {
                    weatherRepository =
                        WeatherRepository()
                }
                return weatherRepository as WeatherRepository
            }
    }


}