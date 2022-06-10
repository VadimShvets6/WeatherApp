package com.example.weatherapp.bussiness

import io.reactivex.rxjava3.plugins.RxJavaPlugins
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiProvider {

    /*companion object{
        private const val BASE_URL = "https://api.openweathermap.org/"
    }*/

    private val openWeatherMap : Retrofit by lazy { initApi() }

    private fun initApi() = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .baseUrl("https://api.openweathermap.org/")
        .build()

    fun provideWeatherApi() : WeatherApi = openWeatherMap.create(WeatherApi::class.java)
    fun provideGeoCodeApi() : GeoCodingApi = openWeatherMap.create(GeoCodingApi::class.java)
}