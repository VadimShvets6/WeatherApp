package com.example.weatherapp.bussiness

import com.example.weatherapp.bussiness.model.GeoCodeModel
import com.example.weatherapp.bussiness.room.GeoCodeEntity

fun GeoCodeModel.mapToEntity() = GeoCodeEntity(
    this.name,
    this.local_names,
    this.lat,
    this.lon,
    this.country,
    this.state ?:"",
    this.isFavourite
)

fun GeoCodeEntity.mapToModel() = GeoCodeModel(
    this.name,
    this.local_names,
    this.lat,
    this.lon,
    this.country,
    this.state,
    this.isFavourite
)