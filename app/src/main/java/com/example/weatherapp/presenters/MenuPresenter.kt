package com.example.weatherapp.presenters

import android.util.Log
import com.example.weatherapp.bussiness.ApiProvider
import com.example.weatherapp.bussiness.model.GeoCodeModel
import com.example.weatherapp.bussiness.repos.MenuRepository
import com.example.weatherapp.bussiness.repos.SAVED
import com.example.weatherapp.view.MenuView

class MenuPresenter : BasePresenter<MenuView>() {

    private val repo = MenuRepository(ApiProvider())

    override fun enable() {
        repo.dataEmitter.subscribe{
            viewState.setLoading(false)
            if(it.purpose == SAVED){
                Log.d("123", "enable: Saved ${it.data}")
                viewState.fillFavoriteList(it.data)
            } else {
                Log.d("123", "enable: Current ${it.data}")
                viewState.fillPredictionList(it.data)
            }
        }
    }

    fun searchFor(str: String){
        repo.getCities(str)
    }

    fun removeLocation(data: GeoCodeModel){
        repo.remove(data)
    }

    fun saveLocation(data : GeoCodeModel){
        repo.add(data)
    }

    fun getFavouriteList(){
        repo.updateFavourite()
    }

}