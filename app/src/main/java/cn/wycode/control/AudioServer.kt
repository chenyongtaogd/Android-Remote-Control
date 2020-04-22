package cn.wycode.control

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.LocalServerSocket
import android.net.LocalSocket
import android.os.AsyncTask
import android.util.Log
import cn.wycode.control.common.AUDIO_SOCKET
import cn.wycode.control.common.LOG_TAG
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioServer : Thread() {

    private lateinit var audioSocket: LocalSocket
    private lateinit var outputStream: OutputStream

    override fun run(){
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO)
        val format = AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(ENCODING)
            .setChannelMask(CHANNEL)
            .build()
        val bufferSize =
            AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING)
        val serverSocket = LocalServerSocket(AUDIO_SOCKET)
        audioSocket = serverSocket.accept()
//        val inputStream = audioSocket.inputStream
        outputStream = audioSocket.outputStream
        outputStream.write(1)
        Log.d(LOG_TAG, "Audio client connected!")

        val record = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.DEFAULT)
            .setAudioFormat(format)
            .setBufferSizeInBytes(bufferSize)
            .build()

        record.startRecording()

        val buffer = FloatArray(bufferSize * 2)
        var num = 0
//        while (true) {
            num = record.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING)
            Log.d(LOG_TAG, "read num = $num")
            val bytes = ByteArray(buffer.size * 4)
            ByteBuffer.wrap(bytes).order(ByteOrder.nativeOrder()).asFloatBuffer().put(buffer)
        outputStream.write(bytes)
        Log.d(LOG_TAG, "write ${bytes.size} bytes ")
//        }

        outputStream.flush()
        outputStream.close()
        record.stop()
        record.release()
        Log.d(LOG_TAG, "clean up")
    }

    //    /**
//     * @param pcmLen pcm数据长度
//     * @param numChannels 声道设置, mono = 1, stereo = 2
//     * @param sampleRate 采样频率
//     * @param bitPerSample 单次数据长度, 例如8bits
//     * @return wav头部信息
//     */
//    private fun wavHeader(pcmLen: Int, numChannels: Int, sampleRate: Int, bitPerSample: Int): ByteArray {
//        val header = ByteArray(44)
//        // ChunkID, RIFF, 占4bytes
//        header[0] = 'R'.toByte()
//        header[1] = 'I'.toByte()
//        header[2] = 'F'.toByte()
//        header[3] = 'F'.toByte()
//        // ChunkSize, pcmLen + 36, 占4bytes
//        val chunkSize = pcmLen + 36.toLong()
//        header[4] = (chunkSize and 0xff).toByte()
//        header[5] = (chunkSize shr 8 and 0xff).toByte()
//        header[6] = (chunkSize shr 16 and 0xff).toByte()
//        header[7] = (chunkSize shr 24 and 0xff).toByte()
//        // Format, WAVE, 占4bytes
//        header[8] = 'W'.toByte()
//        header[9] = 'A'.toByte()
//        header[10] = 'V'.toByte()
//        header[11] = 'E'.toByte()
//        // Subchunk1ID, 'fmt ', 占4bytes
//        header[12] = 'f'.toByte()
//        header[13] = 'm'.toByte()
//        header[14] = 't'.toByte()
//        header[15] = ' '.toByte()
//        // Subchunk1Size, 16, 占4bytes
//        header[16] = 16
//        header[17] = 0
//        header[18] = 0
//        header[19] = 0
//        // AudioFormat, pcm = 1, 占2bytes
//        header[20] = 3
//        header[21] = 0
//        // NumChannels, mono = 1, stereo = 2, 占2bytes
//        header[22] = numChannels.toByte()
//        header[23] = 0
//        // SampleRate, 占4bytes
//        header[24] = (sampleRate and 0xff).toByte()
//        header[25] = (sampleRate shr 8 and 0xff).toByte()
//        header[26] = (sampleRate shr 16 and 0xff).toByte()
//        header[27] = (sampleRate shr 24 and 0xff).toByte()
//        // ByteRate = SampleRate * NumChannels * BitsPerSample / 8, 占4bytes
//        val byteRate = sampleRate * numChannels * bitPerSample / 8.toLong()
//        header[28] = (byteRate and 0xff).toByte()
//        header[29] = (byteRate shr 8 and 0xff).toByte()
//        header[30] = (byteRate shr 16 and 0xff).toByte()
//        header[31] = (byteRate shr 24 and 0xff).toByte()
//        // BlockAlign = NumChannels * BitsPerSample / 8, 占2bytes
//        header[32] = (numChannels * bitPerSample / 8).toByte()
//        header[33] = 0
//        // BitsPerSample, 占2bytes
//        header[34] = bitPerSample.toByte()
//        header[35] = 0
//        // Subhunk2ID, data, 占4bytes
//        header[36] = 'd'.toByte()
//        header[37] = 'a'.toByte()
//        header[38] = 't'.toByte()
//        header[39] = 'a'.toByte()
//        // Subchunk2Size, 占4bytes
//        header[40] = (pcmLen and 0xff).toByte()
//        header[41] = (pcmLen shr 8 and 0xff).toByte()
//        header[42] = (pcmLen shr 16 and 0xff).toByte()
//        header[43] = (pcmLen shr 24 and 0xff).toByte()
//        return header
//    }
}