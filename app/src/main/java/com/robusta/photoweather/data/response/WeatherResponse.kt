package com.robusta.photoweather.data.response

data class WeatherResponse (

    val cod : Int,
    val id : Int,
    val name : String,
    val cord : Cordinates,
    val main : Main,
    val dt : Int,
    val wind : Wind,
    val sys : Sys,
    val clouds : Clouds,
    val weather : List<WeatherItem>,
    val visibility : Double

)