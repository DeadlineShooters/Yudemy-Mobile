package com.deadlineshooters.yudemy.helpers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.Calendar

class AlarmHelper(context: Context) {
    private var mContext = context
    private var alarmMgr: AlarmManager? = null

    init {
        alarmMgr = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @SuppressLint("ScheduleExactAlarm")
    fun initRepeatingAlarm(calendar: Calendar, day: Int, hour: Int) {
        val alarmIntent = Intent(mContext, AlarmReceiver::class.java).apply {
            putExtra("day", day)
            putExtra("hour", hour)
        }
            .let { mIntent ->
            PendingIntent.getBroadcast(mContext, day*100 + hour, mIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        calendar.apply {
            set(Calendar.DAY_OF_WEEK, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        Log.d("AlarmHelper", "initRepeatingAlarm: ${calendar.timeInMillis} intent $alarmIntent")
        alarmMgr?.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
    }

    fun cancelAlarm(day: Int, hour: Int) {
        val alarmIntent = Intent(mContext, AlarmReceiver::class.java).let { mIntent ->
            PendingIntent.getBroadcast(mContext, day*100+hour, mIntent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        } ?: return
        Log.d("AlarmHelper", "cancelAlarm: day $day hour $hour with intent $alarmIntent")
        alarmMgr?.cancel(alarmIntent)
        alarmIntent.cancel()
    }

    fun cancelAllAlarms(days: List<Int>, hours: List<Int>) {
        for(day in days) {
            for(hour in hours) {
                cancelAlarm(day, hour)
            }
        }
    }

    fun checkIfActive(day: Int, hour: Int): Boolean {
        val alarmIntent = Intent(mContext, AlarmReceiver::class.java).let { mIntent ->
            PendingIntent.getBroadcast(mContext, day*100+hour, mIntent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        }
        return alarmIntent != null
    }
}