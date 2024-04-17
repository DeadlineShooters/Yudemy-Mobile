package com.deadlineshooters.yudemy.helpers

import NotificationHelper
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context?, intent: Intent?) {
//        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
//        Log.d("Alarm", "Alarm received with message: $message")
//        context?.let { ctx ->
//            val notificationManager =
//                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            val builder = NotificationCompat.Builder(ctx, Constants.CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setContentTitle("Yudemy")
//                .setContentText("It's time to learn! $message")
////                .setContentText("It's time to learn!")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            notificationManager.notify(NOTIFICATION_ID, builder.build())
//        }
//    }

    override fun onReceive(context: Context?, mIntent: Intent?) {
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7

        val day = mIntent?.getIntExtra("day", 0) ?: 0
        val hour = mIntent?.getIntExtra("hour", 0) ?: 0
        Log.d("Alarm", "Alarm received with day: $day and hour: $hour")

        val notificationUtils = NotificationHelper(context!!)
        notificationUtils.launchNotification()

        val alarmUtils = AlarmHelper(context)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, day)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        alarmUtils.initRepeatingAlarm(calendar, day, hour)
    }
}