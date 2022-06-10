package com.example.weatherapp.bussiness.room

import androidx.room.*

@Dao
interface GeoCodeDao {

    @Query("SELECT * FROM GeoCode")
    fun getAll() : List<GeoCodeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(item: GeoCodeEntity)

    @Delete
    fun remove(item: GeoCodeEntity)
}