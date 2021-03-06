package cn.wycode.control.client

import cn.wycode.control.common.*
import javafx.event.EventHandler
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent

class MouseHandler(private val connections: Connections) : EventHandler<MouseEvent> {

    var mouseConnected = false
    var controlConnected = false

    override fun handle(event: MouseEvent) {
        when (event.eventType) {
            MouseEvent.MOUSE_PRESSED -> onMousePressed(event)
            MouseEvent.MOUSE_MOVED -> onMouseMoved(event)
            MouseEvent.MOUSE_RELEASED -> onMouseReleased(event)
            MouseEvent.MOUSE_DRAGGED -> onMouseDragged(event)
        }
    }

    private fun onMouseDragged(event: MouseEvent) {
        if (connections.mouseVisible && mouseConnected) {
            connections.sendMouseMove((event.x * RATIO).toInt(), (event.y * RATIO).toInt())
        }
        if (controlConnected) {
            if (connections.mouseVisible) {
                connections.sendTouch(
                    HEAD_TOUCH_MOVE,
                    TOUCH_ID_MOUSE,
                    (event.x * RATIO).toInt(),
                    (event.y * RATIO).toInt(),
                    false
                )
            }
        }
        connections.checkReachEdge(event.x, event.y)
    }

    private fun onMouseMoved(event: MouseEvent) {
        if (connections.mouseVisible && mouseConnected) {
            connections.sendMouseMove((event.x * RATIO).toInt(), (event.y * RATIO).toInt())
        }
        connections.checkReachEdge(event.x, event.y)
    }

    private fun onMouseReleased(event: MouseEvent) {
        if (!controlConnected) return
        if (connections.mouseVisible) {
            connections.sendTouch(
                HEAD_TOUCH_UP,
                TOUCH_ID_MOUSE,
                (event.x * RATIO).toInt(),
                (event.y * RATIO).toInt(),
                false
            )
        }
    }

    private fun onMousePressed(event: MouseEvent) {
//        println("${(event.x * RATIO).toInt()},${(event.y * RATIO).toInt()}")
        if (!controlConnected) return
        // mouse touch down
        if (connections.mouseVisible) {
            connections.sendTouch(
                HEAD_TOUCH_DOWN,
                TOUCH_ID_MOUSE,
                (event.x * RATIO).toInt(),
                (event.y * RATIO).toInt(),
                false
            )
        }
    }
}