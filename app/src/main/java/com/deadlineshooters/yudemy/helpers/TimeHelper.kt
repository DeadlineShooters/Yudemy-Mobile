package com.deadlineshooters.yudemy.helpers

class TimeHelper {
    fun convertDurationToString(duration: Double): String {
        val minutes = (duration / 60).toInt()
        val seconds = (duration % 60).toInt()
        return String.format("%02d:%02d", minutes, seconds)
    }
}