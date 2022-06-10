package com.example.weatherapp

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.room.Room
import com.example.weatherapp.bussiness.room.OpenWeatherDatabase
import com.example.weatherapp.view.SettingsHolder

const val APP_SETTINGS = "App settings"
const val IS_STARED_UP = "Is started up"
class App() : Application() {

    companion object{
        lateinit var db : OpenWeatherDatabase
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(this, OpenWeatherDatabase::class.java,"OpenWeatherDB")
            .fallbackToDestructiveMigration()
            .build()

        //db = OpenWeatherDatabase.getInstance(this)
        // TODO убрать к релизу fallbackToDestructiveMigration()

        val preferences = getSharedPreferences(APP_SETTINGS, MODE_PRIVATE)

        SettingsHolder.onCreate(preferences)

        val flag = preferences.contains(IS_STARED_UP)

        if(!flag){
            val editor = preferences.edit()
            editor.putBoolean(IS_STARED_UP, true)
            editor.apply()
            val intent = Intent(this, InitialActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}