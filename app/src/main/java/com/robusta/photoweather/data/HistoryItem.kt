package com.robusta.photoweather.data

import android.graphics.Bitmap

data class HistoryItem (
    val thumbnail : Bitmap,
    val date : String,
    val absolutePath: String
)
