package com.deadlineshooters.yudemy.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.deadlineshooters.yudemy.activities.BaseActivity
import com.deadlineshooters.yudemy.repositories.UserRepository
import java.util.Calendar

class AlarmBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Log.d("AlarmBootReceiver", "Boot completed")
            val alarmUtils = AlarmHelper(context)

            UserRepository().getReminderDays {
                UserRepository().getReminderTimes { times ->
                    for (day in it) {
                        for (time in times) {
                            val tmpDay =
                                (context as BaseActivity).getDayFromIdx(Math.toIntExact(day.toLong()))
                            val tmpHour = context.getHourFromIdx(Math.toIntExact(time.toLong()))
                            val calendar = Calendar.getInstance()
                            calendar.apply {
                                set(Calendar.DAY_OF_WEEK, tmpDay)
                                set(Calendar.HOUR_OF_DAY, tmpHour)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            alarmUtils.initRepeatingAlarm(calendar, tmpDay, tmpHour)
                        }
                    }
                }
            }
        }
    }
}