package com.deadlineshooters.yudemy.models
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @DocumentId
    var id: String = "",
    var fullName: String = "",
//    var avatar: Image = Image(),
    var courseList: ArrayList<String> = arrayListOf(),
    var wishList: ArrayList<String> = arrayListOf(),
    var favoriteCourses: ArrayList<String> = arrayListOf(),
    var reminderDays: ArrayList<Int> = arrayListOf(),
    var reminderTimes: ArrayList<Int> = arrayListOf(),
    var reminderNotification: Boolean = false,
    var certificateList: ArrayList<String> = arrayListOf(),
    var fcmToken: String = "",
    var instructor: Instructor? = null
) : Parcelable

