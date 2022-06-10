package com.example.weatherapp.bussiness.model

data class GeoCodeModel(
    val country: String,
    val local_names: LocalNames,
    val lat: Double,
    val lon: Double,
    val name: String,
    val state: String?,
    var isFavourite: Boolean = false // TODO Будет применяться при добавлении городов в любимые в MenuActivity городов в любимые в MenuActivity
)