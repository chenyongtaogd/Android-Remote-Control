package cn.wycode.control.client

import javafx.application.Platform
import javafx.concurrent.Task
import java.net.Socket
import java.nio.ByteBuffer

class ReadTask(private val mouseSocket: Socket) : Task<Int>() {

    var value = 0

    override fun call(): Int {
        val inputStream = mouseSocket.getInputStream()
        val buffer = ByteArray(8)
        while (!isCancelled) {
            if (inputStream.read(buffer) > 0) {
                SCREEN.x = ByteBuffer.wrap(buffer).getInt(0)
                SCREEN.y = ByteBuffer.wrap(buffer).getInt(4)
                println("ReadTask::$SCREEN")
                updateValue(value++)
            } else {
                println("mouse server closed")
                cancel()
                Platform.exit()
            }
        }
        return 0
    }

}