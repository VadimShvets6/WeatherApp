package com.example.weatherapp.bussiness

import com.example.weatherapp.bussiness.model.GeoCodeModel
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoCodingApi {

    @GET("geo/1.0/direct?")
    fun getCityByName(
        @Query("q") name : String,
        @Query("limit") limit : String = "10",
        @Query("appid") appId : String = "bc3316b6c8b7a6b5b366cd609aea30cb"
    ) : Observable<List<GeoCodeModel>>

    @GET("geo/1.0/reverse?")
    fun getCityByCoord(
        @Query("lat") lat : String,
        @Query("lon") lon : String ,
        @Query("limit") limit : String = "10",
        @Query("appid") appId : String = "bc3316b6c8b7a6b5b366cd609aea30cb"
    ) : Observable<List<GeoCodeModel>>
}