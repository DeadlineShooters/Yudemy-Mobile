package com.deadlineshooters.yudemy.helpers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class AlarmHelper(context: Context) {
    private var mContext = context
    private var alarmMgr: AlarmManager? = null
    private var alarmIntent: PendingIntent

    init {
        alarmMgr = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(mContext, AlarmReceiver::class.java).let { mIntent ->
            // if you want more than one notification use different requestCode
            // every notification need different requestCode
            PendingIntent.getBroadcast(mContext, 100, mIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    fun initRepeatingAlarm(calendar: Calendar, day: Int, time: Int) {
        calendar.apply {
            set(Calendar.DAY_OF_WEEK, day)
            set(Calendar.HOUR_OF_DAY, time)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        Log.d("Alarm", "Alarm set for ${calendar.time} - ${calendar.timeInMillis}")

        alarmMgr?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
    }

    fun cancelAlarm() {
        alarmMgr?.cancel(alarmIntent)
    }
}