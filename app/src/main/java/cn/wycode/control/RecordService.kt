package cn.wycode.control

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.os.IBinder

const val SAMPLE_RATE = 44100
const val ENCODING = AudioFormat.ENCODING_PCM_FLOAT
const val CHANNEL = AudioFormat.CHANNEL_IN_STEREO

class RecordService : Service() {

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            CHANNEL_ID,
            "control channel",
            NotificationManager.IMPORTANCE_HIGH
        )

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Android Controller")
            .setContentText("running").build()

        startForeground(2, notification)

        AudioServer().start()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}
