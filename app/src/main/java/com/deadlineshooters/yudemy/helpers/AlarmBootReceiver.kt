package com.deadlineshooters.yudemy.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class AlarmBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Log.d("AlarmBootReceiver", "Boot completed")
            val calendar = Calendar.getInstance()
            calendar.apply {
                set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY)
                set(Calendar.HOUR_OF_DAY, 15)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val alarmUtils = AlarmHelper(context)
            alarmUtils.initRepeatingAlarm(calendar, Calendar.THURSDAY, 15)
        }
    }
}