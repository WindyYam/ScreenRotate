package com.example.screenrotate

import android.app.*
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast

class MyService : Service() {
    private var TAG = "ServiceActivity"
    private lateinit var orientationChanger: LinearLayout
    private lateinit var orientationLayout: WindowManager.LayoutParams
    private lateinit var wm: WindowManager
    var flag = false
    private lateinit var pendingIntent: PendingIntent
    var rotationallow = 0
    var rotationval = 0

    inner class MyBinder : Binder() {
        val service: MyService
            get() = this@MyService
    }

    private val myBinder = MyBinder()
    override fun onBind(intent: Intent): IBinder? {
        // TODO Auto-generated method stub
        return myBinder
    }

    override fun onCreate() {
        // TODO Auto-generated method stub
        super.onCreate()
        val notificationIntent = Intent(this, MainActivity::class.java)
        pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        Log.e("TAG", "service oncreate")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startMyOwnForeground() else {
            val notification = Notification.Builder(this).setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setTicker(resources.getString(R.string.start))
                .setWhen(System.currentTimeMillis())
                .setContentTitle(resources.getString(R.string.app_name))
                .setContentText(resources.getString(R.string.service))
                .build()
            startForeground(1, notification)
        }
        rotationval = Settings.System.getInt(this.contentResolver, Settings.System.USER_ROTATION, 1)
        rotationallow =
            Settings.System.getInt(this.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 1)
        orientationChanger = LinearLayout(this)
        val LAYOUT_FLAG: Int
        LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        orientationLayout = WindowManager.LayoutParams(
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.RGBA_8888
        )
        wm = this.getSystemService(WINDOW_SERVICE) as WindowManager
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show()
    }

    fun setRotation(rotationstr: String) {
        //Settings.System.putInt(this.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0)
        setScreenRotation(rotationstr)

        // Using TYPE_SYSTEM_OVERLAY is crucial to make your window appear on top
        // You'll need the permission android.permission.SYSTEM_ALERT_WINDOW

        // Use whatever constant you need for your desired rotation
        orientationLayout.screenOrientation = rotationStr2int(rotationstr)
        if (flag == true) {
            wm.removeView(orientationChanger)
            orientationChanger.setVisibility(View.GONE)
        }
        wm.addView(orientationChanger, orientationLayout)
        orientationChanger.setVisibility(View.VISIBLE)
        flag = true
    }

    fun cancelRotation() {
        /*Settings.System.putInt(
            this.contentResolver,
            Settings.System.USER_ROTATION,
            Surface.ROTATION_0
        )
        Settings.System.putInt(this.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0)*/
        if (flag == true) {
            wm.removeView(orientationChanger)
            orientationChanger?.setVisibility(View.GONE)
            flag = false
        }
    }

    private fun setScreenRotation(rotationStr: String) {
        var rotation = 0 // Surface.ROTATION_90; 
        if (rotationStr == "0") {
            rotation = Surface.ROTATION_0
        } else if (rotationStr == "90") {
            rotation = Surface.ROTATION_90
        } else if (rotationStr == "180") {
            rotation = Surface.ROTATION_180
        } else if (rotationStr == "270") {
            rotation = Surface.ROTATION_270
        }
        //Settings.System.putInt(this.contentResolver, Settings.System.USER_ROTATION, rotation)
    }

    private fun rotationStr2int(rotationStr: String): Int {
        var rotation = 0
        if (rotationStr == "0") {
            rotation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } else if (rotationStr == "90") {
            rotation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else if (rotationStr == "180") {
            rotation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        } else if (rotationStr == "270") {
            rotation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
        return rotation
    }

    override fun onStartCommand(intent: Intent, flag: Int, startId: Int): Int {
        // TODO Auto-generated method stub
        super.onStartCommand(intent, flag, startId)
        Toast.makeText(this, resources.getString(R.string.startservice), Toast.LENGTH_LONG).show()
        intent.getExtras()?.getString("Rotation")?.let { setRotation(it) }
        return START_STICKY_COMPATIBILITY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {}
    override fun onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy()
        Toast.makeText(this, resources.getString(R.string.stopservice), Toast.LENGTH_LONG).show()
        Log.v(TAG, "ServiconDestroy")
        cancelRotation()
    }

    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "com.example.simpleapp"
        val channelName = "My Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.setLightColor(Color.BLUE)
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE)
        val manager: NotificationManager =
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)!!
        manager.createNotificationChannel(chan)
        val notificationBuilder: Notification.Builder =
            Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentIntent(pendingIntent).setSmallIcon(R.drawable.ic_launcher)
            .setTicker(resources.getString(R.string.start))
            .setWhen(System.currentTimeMillis())
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(resources.getString(R.string.service))
            .build()
        startForeground(2, notification)
    }
}