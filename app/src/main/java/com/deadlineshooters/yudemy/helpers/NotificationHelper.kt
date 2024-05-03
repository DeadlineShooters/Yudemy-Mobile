import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.deadlineshooters.yudemy.R
import com.deadlineshooters.yudemy.activities.SplashActivity
import com.deadlineshooters.yudemy.activities.StudentMainActivity
import com.deadlineshooters.yudemy.helpers.AlarmReceiver
import com.deadlineshooters.yudemy.utils.Constants
import java.time.LocalDateTime
import java.time.ZoneId

//class NotificationHelper(private val context: Context) {
//    private var alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
////    private var alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
////        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
////    }
//
//    @SuppressLint("ScheduleExactAlarm")
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun schedule(day: Int, time: Int) {
//        val intent = Intent(context, AlarmReceiver::class.java)
//            .apply {
//            putExtra("EXTRA_MESSAGE", "$day $time")
//        }
//
//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.DAY_OF_WEEK, day)
//            set(Calendar.HOUR_OF_DAY, time)
//            set(Calendar.MINUTE, 0)
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }
//
////        val alarmTime = time.atZone(ZoneId.systemDefault()).toEpochSecond()*1000L
////        alarmManager.setExact(
////            AlarmManager.RTC_WAKEUP,
////            alarmTime,
////            PendingIntent.getBroadcast(
////                context,
////                time.hashCode(),
////                intent,
////                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
////            )
////        )
////        alarmManager.setRepeating(
////            AlarmManager.RTC_WAKEUP,
////            calendar.timeInMillis,
////            24*7*60*60*1000L,
////            PendingIntent.getBroadcast(
////                context,
////                day.hashCode() + time.hashCode(),
////                intent,
////                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
////            )
////        )
//        Log.d("Alarm", "${day.hashCode() + time.hashCode()}")
//
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            24*7*60*60*1000L,
////            60000,
//            PendingIntent.getBroadcast(
//                context,
//                day.hashCode() + time.hashCode(),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        )
//
//        Log.d("Alarm", "Alarm scheduled at $day $time ${calendar.timeInMillis}")
//    }
//
//    fun cancel(day: Int, time: Int) {
//        Log.d("Alarm", "${day.hashCode() + time.hashCode()}")
//        alarmManager.cancel(
//            PendingIntent.getBroadcast(
//                context,
//                day.hashCode() + time.hashCode(),
//                Intent(context, AlarmReceiver::class.java),
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        )
//    }
//}

class NotificationHelper(context:Context) {

    private var mContext = context
    private lateinit var notificationBuilder: NotificationCompat.Builder
    val notificationManager = NotificationManagerCompat.from(mContext)

    init {
//        createNotificationChannel()
        initNotificationBuilder()
    }

    fun launchNotification(){
        with(NotificationManagerCompat.from(mContext)) {
            // notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(
                    mContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notificationManager.notify(0, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = Constants.CHANNEL_NAME
            val descriptionText = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notiManager: NotificationManager =
                mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notiManager.createNotificationChannel(channel)
        }
    }


    private fun initNotificationBuilder() {

        // Create an explicit intent for an Activity in your app
        val sampleIntent = Intent(mContext, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(mContext, 0, sampleIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        /***
         * Notice that the NotificationCompat.Builder constructor requires that you provide a channel ID.
         * This is required for compatibility with Android 8.0 (API level 26) and higher,
         * but is ignored by older versions.
         */
        notificationBuilder = NotificationCompat.Builder(mContext, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Yudemy")
            .setContentText("It's time to learn!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            // Automatically removes the notification when the user taps it.
            .setAutoCancel(true)
    }
}