package com.example.screenrotate

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MainActivity : Activity(), View.OnClickListener {
    private lateinit var r0: RadioButton
    private lateinit var r90: RadioButton
    private lateinit var r180: RadioButton
    private lateinit var r270: RadioButton
    private lateinit var buttonLock: Button
    private lateinit var buttonUnlock: Button
    private lateinit var buttonPermission: Button
    private lateinit var buttonMore: Button
    private var rotation = 0
    private var rotateallow = 0
    private var flag = false
    private lateinit var appname: String
    private lateinit var bindservice: MyService
    private val conn: ServiceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName) {
            // TODO Auto-generated method stub
        }

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // TODO Auto-generated method stub
            val binder = service as MyService.MyBinder
            bindservice = binder.service
            flag = true
        }
    }

    protected override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("TAG", "on Create")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //bindService(new Intent(this,MyService.class), conn, Context.BIND_AUTO_CREATE);
        r0 = findViewById<View>(R.id.radio0) as RadioButton
        r90 = findViewById<View>(R.id.radio90) as RadioButton
        r180 = findViewById<View>(R.id.radio180) as RadioButton
        r270 = findViewById<View>(R.id.radio270) as RadioButton
        buttonLock = findViewById<View>(R.id.button1) as Button
        buttonUnlock = findViewById<View>(R.id.button2) as Button
        buttonPermission = findViewById<View>(R.id.permission) as Button
        buttonMore = findViewById<View>(R.id.more) as Button
        buttonLock.setOnClickListener {
            Log.e("TAG", "start service")
            if (!canDrawOverlayViews()) {
                Toast.makeText(
                    this@MainActivity,
                    "Permission denied, required to grant permission for$appname",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                startService(
                    Intent(this@MainActivity, MyService::class.java).putExtra(
                        "Rotation",
                        selectedRadioBtn
                    )
                )
            }
            Log.e("TAG", "start service over")
        }
        buttonUnlock.setOnClickListener { //bindservice.cancelRotation();
            if (!canDrawOverlayViews()) {
                Toast.makeText(
                    this@MainActivity,
                    "Permission denied, required to grant permission for$appname",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                stopService(Intent(this@MainActivity, MyService::class.java))
            }
        }
        buttonPermission.setOnClickListener(this)
        buttonMore.setOnClickListener {
            val intent = Intent()
            intent.setData(Uri.parse("http://www.fywskj.com"))
            intent.setAction(Intent.ACTION_VIEW)
            this@MainActivity.startActivity(intent)
        }
        appname = getString(R.string.app_name)
    }

    private val selectedRadioBtn: String
        private get() = if (r0.isChecked()) "0" else if (r90.isChecked()) "90" else if (r180.isChecked()) "180" else if (r270.isChecked()) "270" else "0"

    fun canDrawOverlayViews(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return true
        } else {
            try {
                return Settings.canDrawOverlays(this)
            } catch (e: NoSuchMethodError) {
                e.printStackTrace()
            }
        }
        return true
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        Log.e("test", "onResume")
        /*if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_SETTINGS)) {
			//has permission, do operation directly
		} else {
			//do not have permission
			Log.i("test", "user do not have this permission!");



				// No explanation needed, we can request the permission.
				Log.i("test", "==request the permission==");

				ActivityCompat.requestPermissions(MainActivity.this,
						new String[]{Manifest.permission.WRITE_SETTINGS},
						1234);

				// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
				// app-defined int constant. The callback method gets the
				// result of the request.

		}*/
    }

    override fun onDestroy() {
        super.onDestroy()
        if (flag == true) {
            //unbindService(conn);
            flag = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)
    }

    private fun requestAlertWindowPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.setData(Uri.parse("package:" + getPackageName()))
        startActivityForResult(intent, REQUEST_CODE_ALERT)
    }

    protected override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ALERT) {
            requestWriteSettings()
        }
    }

    private fun requestWriteSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.setData(Uri.parse("package:" + getPackageName()))
        startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS)
    }

    override fun onClick(v: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //requestWriteSettings();
            requestAlertWindowPermission()
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_SETTINGS
                ),
                123
            )
        }
    }

    companion object {
        private const val REQUEST_CODE_ALERT = 1
        private const val REQUEST_CODE_WRITE_SETTINGS = 2
    }
}