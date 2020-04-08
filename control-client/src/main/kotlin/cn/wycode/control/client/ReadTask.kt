package cn.wycode.control.client

import javafx.concurrent.Task
import java.net.Socket
import java.nio.ByteBuffer

class ReadTask(private val mouseSocket: Socket, private val screenInfo: ScreenInfo) : Task<ScreenInfo>() {

    override fun call(): ScreenInfo {
        val inputStream = mouseSocket.getInputStream()
        val buffer = ByteArray(8)
        while (true) {
            if (inputStream.read(buffer) > 0) {
                screenInfo.width = ByteBuffer.wrap(buffer).getInt(0)
                screenInfo.height = ByteBuffer.wrap(buffer).getInt(4)
                println("ReadTask::$screenInfo")
                updateValue(screenInfo)
            }
        }
    }

}

data class ScreenInfo(var width: Int, var height: Int)