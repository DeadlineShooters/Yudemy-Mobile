package com.deadlineshooters.yudemy.helpers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.checkerframework.checker.units.qual.min
import java.util.Calendar

class AlarmHelper(context: Context) {
    private var mContext = context
    private var alarmMgr: AlarmManager? = null

    init {
        alarmMgr = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @SuppressLint("ScheduleExactAlarm")
    fun initRepeatingAlarm(calendar: Calendar, day: Int, hour: Int) {
        val alarmIntent = Intent(mContext, AlarmReceiver::class.java).let { mIntent ->
            PendingIntent.getBroadcast(mContext, day*100 + hour, mIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        alarmMgr?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
    }

    fun cancelAlarm(day: Int, hour: Int) {
        val alarmIntent = Intent(mContext, AlarmReceiver::class.java).let { mIntent ->
            PendingIntent.getBroadcast(mContext, day*100+hour, mIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        alarmMgr?.cancel(alarmIntent)
        alarmIntent.cancel()
    }

    fun cancelAllAlarms(days: List<Int>, hours: List<Int>) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            alarmMgr?.cancelAll()
        }
        else {
            for(day in days) {
                for(hour in hours) {
                    cancelAlarm(day, hour)
                }
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