package com.fan.controller

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil.sleep
import cn.hutool.log.Log
import cn.hutool.log.level.Level
import jakarta.websocket.OnClose
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.ServerEndpoint
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicBoolean

@RestController
@ServerEndpoint("/log")
class WebSocketController {

    private val isRunning = AtomicBoolean(false)


    @OnOpen
    fun onOpen(session: Session) {
        try {
            isRunning.set(true)
            Thread {
                startSendingText(session)
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OnClose
    fun onClose(session: Session) {
        isRunning.set(false)
    }

    private fun startSendingText(session: Session) {
        while (isRunning.get()) {
            try {
                sleep(500)
                val message = queue.take()
                session.basicRemote.sendText(message)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    companion object {
        private val queue = LinkedBlockingDeque<String>()
        private val logger = Log.get()
        fun letPeopleKnow(message: String) {
            logger.log(Level.INFO, message)
            if (queue.size > 100000) {
                queue.pollFirst()
            }
            queue.put(withTimePrefix(message))

        }

        private fun withTimePrefix(message: String): String {
            return "【 🚀 %s】 %s".format(DateUtil.now(), message)
        }
    }
}

