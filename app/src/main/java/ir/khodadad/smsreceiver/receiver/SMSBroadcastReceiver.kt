package ir.khodadad.smsreceiver.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import ir.khodadad.smsreceiver.MainActivity
import ir.khodadad.smsreceiver.R

class SMSBroadcastReceiver : BroadcastReceiver() {

    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras

        try {
            if (bundle != null) {
                val pdusObj = bundle.get("pdus") as Array<*>
                for (i in pdusObj.indices) {

                    val currentMessage: SmsMessage

                    val format = intent.getStringExtra("format")
                    currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray, format)

                    val number = currentMessage.displayOriginatingAddress

                    val message = currentMessage.displayMessageBody
                    Log.i("SmsReceiver", "senderNum: $number; message: $message")

                    val intent2 = Intent("android.intent.action.SMSRECEBIDO")
                        .putExtra("number", number)
                        .putExtra("message", message)
                    context.sendBroadcast(intent2)
                    showToast(context, number, message)
                    showNotification(context, number, message)
                }
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver " + e)
        }

    }

    private fun showToast(context: Context, numeroTelefone: String, mensagem: String) {
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(context,
            "senderNum: $numeroTelefone, message: $mensagem", duration)
        toast.show()
    }

//    private fun showNotification(context: Context, numeroTelefone: String, mensagem: String) {
//        val mBuilder = NotificationCompat.Builder(context)
//        mBuilder.setSmallIcon(R.drawable.ic_launcher_foreground)
//        mBuilder.color = 15869459
//        mBuilder.setContentTitle("Mensagem de: " + numeroTelefone)
//        mBuilder.setContentText(mensagem)
//
//
//        val resultIntent = Intent(context, MainActivity::class.java)
//
//        resultIntent
//            .putExtra("remetente", numeroTelefone)
//            .putExtra("mensagem", mensagem)
//
//        val stackBuilder = TaskStackBuilder.create(context)
//        stackBuilder.addParentStack(MainActivity::class.java)
//
//
//        stackBuilder.addNextIntent(resultIntent)
//        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//        mBuilder.setContentIntent(resultPendingIntent)
//
//        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//        // notificationID allows you to update the notification later on.
//        mNotificationManager.notify(1, mBuilder.build())
//    }
    private fun showNotification(context: Context, number: String, message: String) {

    notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val intent = Intent(context, MainActivity::class.java)

    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)

        builder = Notification.Builder(context, channelId)
            .setContentTitle(number)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                R.drawable.ic_launcher_background
            ))
            .setContentIntent(pendingIntent)
    } else {

        builder = Notification.Builder(context)
            .setContentTitle(number)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                R.drawable.ic_launcher_background
            ))
            .setContentIntent(pendingIntent)
    }
    notificationManager.notify(1234, builder.build())
    }
}