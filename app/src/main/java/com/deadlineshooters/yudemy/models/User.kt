package com.deadlineshooters.yudemy.models
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val avatar: Image,
    val courseList: ArrayList<String> = arrayListOf(),
    val wishList: ArrayList<String> = arrayListOf(),
    val favoriteCourses: ArrayList<String> = arrayListOf(),
    val reminderDays: ArrayList<Int> = arrayListOf(),
    val reminderTimes: ArrayList<Int> = arrayListOf(),
    val reminderNotification: Boolean = false,
    val certificateList: ArrayList<String> = arrayListOf(),
    val fcmToken: String = ""
) : Parcelable