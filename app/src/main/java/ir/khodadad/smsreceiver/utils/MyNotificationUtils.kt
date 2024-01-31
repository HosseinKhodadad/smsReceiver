package ir.khodadad.smsreceiver.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ir.khodadad.smsreceiver.R

class MyNotificationUtils private constructor(
    private val context: Context,
    private val title: String?,
    private val message: String?,
    private val channelId: String
) {

    @SuppressLint("MissingPermission")
    fun showNotification() {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    class Builder(private val context: Context) {
        private var title: String? = null
        private var message: String? = null
        private var channelId: String = DEFAULT_CHANNEL_ID

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setMessage(message: String): Builder {
            this.message = message
            return this
        }

        fun setChannelId(channelId: String): Builder {
            this.channelId = channelId
            return this
        }

        fun build(): MyNotificationUtils {
            return MyNotificationUtils(context, title, message, channelId)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val DEFAULT_CHANNEL_ID = "default_channel"
        private const val CHANNEL_NAME = "SMS_Khodadadi"
        private const val CHANNEL_DESCRIPTION = "Channel Description"

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun requestNotificationPermission(activity: Activity, requestCode: Int) {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), requestCode
            )
        }

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun hasNotificationPermission(context: Context): Boolean {
            return ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }

        fun isNotificationPermissionGranted(grantResults: IntArray): Boolean {
            return grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }
}