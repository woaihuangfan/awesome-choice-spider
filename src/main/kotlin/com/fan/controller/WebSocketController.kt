package com.fan.controller

import cn.hutool.core.date.DateUtil
import jakarta.websocket.OnClose
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicBoolean

@RestController
@ServerEndpoint("/log/{sessionId}")
class WebSocketController {

    private val isRunning = AtomicBoolean(false)


    @OnOpen
    fun onOpen(session: Session, @PathParam("sessionId") sessionId: String) {
        try {
            sessions[sessionId] = session
            if (sessionId == "collect") {
                isRunning.set(true)
                Thread {
                    startSendingText(session)
                }.start()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OnClose
    fun onClose(session: Session, @PathParam("sessionId") sessionId: String) {
        sessions.remove(sessionId)
        if (sessionId == "collect") {
            isRunning.set(false)
        }
    }

    private fun startSendingText(session: Session) {

        while (isRunning.get()) {
            try {
                if (queue.isNotEmpty()) {
                    val message = queue.take()
                    session.basicRemote.sendText(message)
                }
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    companion object {
        private val sessions = mutableMapOf<String, Session>()
        private val queue = LinkedBlockingDeque<String>()
        fun letPeopleKnow(message: String) {
            println(message)
            if (queue.size > 1000000) {
                queue.pollFirst()
            }
            queue.put(withTimePrefix(message))

        }

        fun notifyClientJobIsDone() {
            sessions.values.forEach {
                it.basicRemote.sendText("job is done")
            }

        }

        private fun withTimePrefix(message: String): String {
            return "【 🚀 %s】 %s".format(DateUtil.now(), message)
        }
    }
}


