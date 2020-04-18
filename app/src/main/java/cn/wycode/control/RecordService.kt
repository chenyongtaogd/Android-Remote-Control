package cn.wycode.control

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioPlaybackCaptureConfiguration
import android.media.AudioRecord
import android.media.projection.MediaProjectionManager
import android.os.Environment
import android.os.IBinder
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @SuppressLint("NewApi")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        val data = intent.getParcelableExtra("data") as Intent
        val mediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, data)
        AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        val format = AudioFormat.Builder()
            .setSampleRate(44100)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_IN_STEREO)
            .build()
        val record = AudioRecord.Builder()
            .setAudioFormat(format)
            .setAudioPlaybackCaptureConfig(config)
            .build()
        record.startRecording()

        Thread(Runnable {
            try {
                val path = getExternalFilesDir(Environment.DIRECTORY_MUSIC)!!.absolutePath+"/voice.pcm"
                Log.d(ContentValues.TAG, "path->$path")
                val file = File(path)
                if(file.exists()){
                    file.delete()
                }else{
                    file.createNewFile()
                }
                val os = FileOutputStream(file)
                val buffer = ByteArray(2048)
                var num = 0
                var i = 0
                while (i < 500) {
                    num = record.read(buffer, 0, 2048)
                    Log.d(ContentValues.TAG, "buffer = $buffer, num = $num")
                    os.write(buffer, 0, num)
                    i++
                }
                Log.d(ContentValues.TAG, "exit loop")
                os.close()
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e(ContentValues.TAG, "Dump PCM to file failed")
            }
            record.stop()
            record.release()
            Log.d(ContentValues.TAG, "clean up")
        }).start()

        return super.onStartCommand(intent, flags, startId)
    }
}
