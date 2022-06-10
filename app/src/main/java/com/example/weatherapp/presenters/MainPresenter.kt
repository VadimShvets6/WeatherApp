package com.example.weatherapp.presenters

import android.util.Log
import com.example.weatherapp.bussiness.ApiProvider
import com.example.weatherapp.bussiness.repos.MainRepository
import com.example.weatherapp.view.MainView

class MainPresenter : BasePresenter<MainView>(){

    private val repo = MainRepository(ApiProvider())

    override fun enable() {
        repo.dataEmitter
            .doAfterNext { viewState.setLoading(false) }
            .subscribe { response ->
            Log.d("MAINREPO", "presenter: $response")
            viewState.displayLocation(response.cityName)
            viewState.displayCurrentData(response.weatherData)
            viewState.displayDailyData(response.weatherData.daily)
            viewState.displayHourlyData(response.weatherData.hourly)
            response.error?.let{ viewState.displayError(response.error) }
        }
    }

    fun refresh( lat : String, lon : String) {
        viewState.setLoading(true)
        repo.reloadData(lat, lon)
    }
}