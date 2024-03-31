package com.deadlineshooters.yudemy.helpers

class StringUtils {
    companion object {
        fun trimDecimalZero(input: String): String {
            return if (input.endsWith(".0")) {
                input.dropLast(2)
            } else {
                input
            }
        }
    }
}
