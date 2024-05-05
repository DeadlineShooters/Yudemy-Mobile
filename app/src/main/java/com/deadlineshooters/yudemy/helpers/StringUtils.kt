package com.deadlineshooters.yudemy.helpers

import kotlin.math.roundToInt

class StringUtils {
    companion object {
        fun trimDecimalZero(input: String): String {
            return if (input.endsWith(".0")) {
                input.dropLast(2)
            } else {
                input
            }
        }

        fun secondsToMinuteSecondFormat(seconds: Double): String {
            val minutes = (seconds / 60).toInt()
            val remainingSeconds = (seconds % 60).toInt()
            return String.format("%d:%02d", minutes, remainingSeconds)
        }

        fun roundToOneDecimalPlace(avgRating: Double): Double {
            return (avgRating * 10.0).roundToInt() / 10.0
        }

    }
}
