package com.example.weatherapp.bussiness

import com.example.weatherapp.bussiness.model.WeatherDataModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/onecall?")
    fun getWeatherForecast(
        @Query("lat") lat : String,
        @Query("lon") lon : String,
        @Query("exclude") exclude : String = "minutely,alerts",
        @Query("appid") appId : String = "bc3316b6c8b7a6b5b366cd609aea30cb",
        @Query("lang") lang : String = "ru"
    ) : Observable<WeatherDataModel>
}