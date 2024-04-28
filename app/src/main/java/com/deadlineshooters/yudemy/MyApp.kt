package com.deadlineshooters.yudemy

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.cloudinary.android.MediaManager
import com.deadlineshooters.yudemy.utils.Constants
import java.util.HashMap

class MyApp : Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        // Create global variable
        val config: HashMap<String, String> = HashMap()
        config["cloud_name"] = BuildConfig.CLOUD_NAME

        // Initialize MediaManager
        MediaManager.init(this, config)

        // Initialize notification channel
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            Constants.CHANNEL_ID,
            Constants.CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }
}
