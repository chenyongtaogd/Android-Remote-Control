package cn.wycode.control

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast


const val OVERLAY_PERMISSION_REQUEST_CODE = 300
const val AUDIO_PERMISSION_REQUEST_CODE = 301

class MainActivity : Activity() {

    private lateinit var content: View

    private lateinit var mediaProjectionManager: MediaProjectionManager

    private var permissionToRecordAccepted = false
    private var permissions: Array<String> =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = LayoutInflater.from(this).inflate(R.layout.activity_main, null)
        setContentView(content)
        content.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                )
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        }

        requestPermissions(permissions, AUDIO_PERMISSION_REQUEST_CODE)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == AUDIO_PERMISSION_REQUEST_CODE) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (permissionToRecordAccepted) {
            startForegroundService(Intent(this, RecordService::class.java))
        } else {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "没有权限！", Toast.LENGTH_SHORT).show()
        } else {
            startForegroundService(Intent(this, MouseService::class.java))
        }
    }
}
