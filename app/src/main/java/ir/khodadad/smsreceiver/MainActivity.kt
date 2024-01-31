package ir.khodadad.smsreceiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ir.khodadad.smsreceiver.databinding.ActivityMainBinding
import ir.khodadad.smsreceiver.utils.MyNotificationUtils


class MainActivity : AppCompatActivity() {



    private var mReceiver: BroadcastReceiver? = null
    private lateinit var binding: ActivityMainBinding

    private val smsPermissionRequestCode = 100
    private val notificationPermissionRequestCode = 200

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        requestSmsPermission()

        MyNotificationUtils.requestNotificationPermission(this, notificationPermissionRequestCode)

        binding.apply {
            send.setOnClickListener {

                val number = number.text.toString()
                val message = message.text.toString()

                if (number.isEmpty())
                    Toast.makeText(
                        this@MainActivity,
                        "Please Enter a Phone Number",
                        Toast.LENGTH_LONG
                    ).show()
                else
                    sendSMS(number, message)
            }
        }

    }

    private fun sendSMS(phoneNumber: String, smsMessage: String) {
        val smsManager = getSystemService(SmsManager::class.java)
         smsManager.sendTextMessage(phoneNumber, null, smsMessage, null, null)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            notificationPermissionRequestCode -> {
                if (MyNotificationUtils.isNotificationPermissionGranted(grantResults)) {
                    val notification = MyNotificationUtils.Builder(this)
                        .setTitle("Title")
                        .setMessage("This is a notification message.")
                        .setChannelId("Test")
                        .build()
                    notification.showNotification()
                } else {
                    // Handle permission not granted
                }
            }


            smsPermissionRequestCode -> {
                var allPermissionsGranted = true

                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false
                        break
                    }
                }

                if (allPermissionsGranted) {
                    // Permission granted, handle SMS functionality
                } else {
                    // Permission denied, handle accordingly
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun requestSmsPermission() {
        val sendSmsPermission = Manifest.permission.SEND_SMS
        val receiveSmsPermission = Manifest.permission.RECEIVE_SMS

        val permissions = arrayOf(sendSmsPermission, receiveSmsPermission)

        val permissionsToRequest = mutableListOf<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                smsPermissionRequestCode
            )
        }

    }


    override fun onResume() {
        super.onResume()

        val intentFilter = IntentFilter("android.intent.action.SMSRECEBIDO")

        mReceiver = object : BroadcastReceiver() {

            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onReceive(context: Context, intent: Intent) {
                val number = intent.getStringExtra("number")
                val message = intent.getStringExtra("message")

                if (MyNotificationUtils.hasNotificationPermission(this@MainActivity)){
                    val notification = MyNotificationUtils.Builder(this@MainActivity)
                        .setTitle("$number")
                        .setMessage("$message")
                        .setChannelId("SMS")
                        .build()
                    notification.showNotification()
                }
                binding.tvMessage.text = "$number => $message"
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mReceiver, intentFilter, RECEIVER_EXPORTED)
        } else registerReceiver(mReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(mReceiver)
    }

}