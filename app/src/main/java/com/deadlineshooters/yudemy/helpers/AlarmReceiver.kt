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
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7

        Log.d("Alarm", "Alarm received")
        val notificationUtils = NotificationHelper(context!!)
        notificationUtils.launchNotification()

//        calendar.add(Calendar.DATE, 7)
//        calendar.add(Calendar.MINUTE, 1)
//        alarmUtils.initRepeatingAlarm(calendar, Calendar.THURSDAY, 15, 51)

        val alarmUtils = AlarmHelper(context)
//        calendar.timeInMillis = System.currentTimeMillis() + 1000 * 15
        alarmUtils.initRepeatingAlarm(calendar, Calendar.THURSDAY, 15)
    }
}