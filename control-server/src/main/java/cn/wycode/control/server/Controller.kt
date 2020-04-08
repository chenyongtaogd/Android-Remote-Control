package cn.wycode.control.server

import android.os.SystemClock
import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyCharacterMap
import android.view.KeyEvent
import cn.wycode.control.common.*
import cn.wycode.control.server.utils.Ln
import cn.wycode.control.server.wrappers.InputManager.INJECT_INPUT_EVENT_MODE_ASYNC
import cn.wycode.control.server.wrappers.ServiceManager
import java.io.InputStream
import java.nio.ByteBuffer


class Controller(private val inputStream: InputStream) : Thread() {

    private val event: Event = Event(0, 0, 0, 0, 0)
    private val pointBuffer = ByteBuffer.allocate(8)
    private val serviceManager = ServiceManager()
    private val touchConverter = TouchConverter()

    override fun run() {
        while (true) {
            readEvent()
            injectEvent()
        }
    }

    private fun injectEvent() {
        when (event.type) {
            HEAD_KEY -> injectKey()
            else -> injectTouch()
        }
    }

    private fun injectTouch() {
        val motionEvent = touchConverter.convert(this.event)
        injectEvent(motionEvent)
        motionEvent.recycle()
    }


    private fun injectKey() {
        val keycode = when (event.key) {
            KEY_HOME -> KeyEvent.KEYCODE_HOME
            KEY_BACK -> KeyEvent.KEYCODE_BACK
            KEY_VOLUME_UP -> KeyEvent.KEYCODE_VOLUME_UP
            KEY_VOLUME_DOWN -> KeyEvent.KEYCODE_VOLUME_DOWN
            else -> KeyEvent.KEYCODE_UNKNOWN
        }
        if (injectKeyEvent(KeyEvent.ACTION_DOWN, keycode))
            injectKeyEvent(KeyEvent.ACTION_UP, keycode)
    }


    private fun injectKeyEvent(action: Int, keyCode: Int): Boolean {
        val now = SystemClock.uptimeMillis()
        val event = KeyEvent(
            now, now, action, keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0, 0,
            InputDevice.SOURCE_KEYBOARD
        )
        return injectEvent(event)
    }

    private fun injectEvent(event: InputEvent): Boolean {
        Ln.d("eventReceived->${this.event},\neventInjected->$event")
        return serviceManager.inputManager.injectInputEvent(event, INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    private fun readEvent() {
        event.type = inputStream.read().toByte()
        if (event.type == HEAD_KEY) {
            event.key = inputStream.read().toByte()
        } else {
            inputStream.read(pointBuffer.array())
            event.x = pointBuffer.getInt(0)
            event.y = pointBuffer.getInt(4)
        }
    }
}

data class Event(var type: Byte, var index: Byte, var x: Int, var y: Int, var key: Byte)