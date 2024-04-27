package com.deadlineshooters.yudemy

import android.app.Application
import com.cloudinary.android.MediaManager
import com.deadlineshooters.yudemy.activities.BaseActivity
import java.util.HashMap

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Create global variable
        val config: HashMap<String, String> = HashMap()
        config["cloud_name"] = BuildConfig.CLOUD_NAME

        // Initialize MediaManager
        MediaManager.init(this, config)
    }
}
