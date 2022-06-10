package com.example.weatherapp.bussiness.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WeatherDataEntity::class, GeoCodeEntity::class], exportSchema = false, version = 19)
abstract class OpenWeatherDatabase : RoomDatabase(){

    abstract fun getWeatherDao() : WeatherDao

    abstract fun getGeoCodeDao() : GeoCodeDao
}