package com.example.weatherapp.bussiness.repos

import android.util.Log
import com.example.weatherapp.bussiness.ApiProvider
import com.example.weatherapp.bussiness.model.WeatherDataModel
import com.example.weatherapp.bussiness.room.WeatherDataEntity
import com.google.gson.Gson
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainRepository(api : ApiProvider) : BaseRepository<MainRepository.ServerResponse>(api) {

    private val gson = Gson()
    private val  dbAccess = db.getWeatherDao()

    fun reloadData(lat : String, lon : String){
        Observable.zip(
            api.provideWeatherApi().getWeatherForecast(lat, lon),
            api.provideGeoCodeApi().getCityByCoord(lat, lon).map{
                it.asSequence()
                    .map { model -> model.name }
                    .toList()
                        // TODO настроить локализацию проекта
                    .filterNotNull()
                    .first()
                                                                },
            { weatherData, geoCod -> ServerResponse(geoCod, weatherData) }
        )
            .subscribeOn(Schedulers.io())
            .doOnNext {
                dbAccess.insertWeatherData( WeatherDataEntity(
                    data = gson.toJson(it.weatherData),
                    city = it.cityName)
                )

            }
            .onErrorResumeNext {
                Observable.just(ServerResponse(
                    dbAccess.getWeatherData().city,
                    gson.fromJson(dbAccess.getWeatherData().data, WeatherDataModel::class.java),
                    it
                ))
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dataEmitter.onNext(it)
            }, {
                Log.d("MAINREPO", "reloadData: $it")
            })
    }

    data class ServerResponse(val cityName : String, val weatherData : WeatherDataModel, val error : Throwable? = null)
}