package com.fan.controller

import cn.hutool.core.date.DateUtil
import jakarta.websocket.OnClose
import jakarta.websocket.OnOpen
import jakarta.websocket.Session
import jakarta.websocket.server.PathParam
import jakarta.websocket.server.ServerEndpoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicBoolean

@RestController
@ServerEndpoint("/log/{sessionId}")
class WebSocketController {
    private val isRunning = AtomicBoolean(false)

    @OnOpen
    fun onOpen(
        session: Session,
        @PathParam("sessionId") sessionId: String,
    ) {
        try {
            sessions[sessionId] = session
            if (sessionId == SESSION_ID_COLLECT) {
                isRunning.set(true)
                startSendingTextCoroutine(session)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    @OnClose
    fun onClose(
        session: Session,
        @PathParam("sessionId") sessionId: String,
    ) {
        sessions.remove(sessionId)
        if (sessionId == SESSION_ID_COLLECT) {
            isRunning.set(false)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun startSendingTextCoroutine(session: Session) {
        GlobalScope.launch {
            while (isRunning.get()) {
                try {
                    if (queue.isNotEmpty() && session.isOpen) {
                        val message = queue.take()
                        session.basicRemote.sendText(message)
                    }
                    delay(100) // 添加延迟，避免CPU过载
                } catch (e: Exception) {
                    handleError(e)
                }
            }
        }
    }

    companion object {
        private const val SESSION_ID_COLLECT = "collect"
        private val sessions = mutableMapOf<String, Session>()
        private val queue = LinkedBlockingDeque<String>()
        private val logger = LoggerFactory.getLogger(WebSocketController::class.java)

        fun letPeopleKnow(message: String) {
            logger.info(message)
            queue.put(withTimePrefix(message))
        }

        fun notifyClientJobIsDone() {
            try {
                sessions.values.forEach {
                    it.basicRemote.sendText("job is done")
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }

        fun notifyClientJobCanceled() {
            try {
                sessions.values.forEach {
                    if (it.isOpen) {
                        it.basicRemote.sendText("canceled")
                    }
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }

        fun clearLog() {
            queue.clear()
        }

        private fun withTimePrefix(message: String): String = "【 🚀 ${DateUtil.now()}】 $message"

        private fun handleError(e: Exception) {
            e.printStackTrace()
        }
    }
}
