package com.fan.listener

import cn.hutool.core.thread.ThreadUtil
import com.fan.db.repository.NoticeRepository
import com.fan.event.ResultSaveEvent
import jakarta.transaction.Transactional
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class ResultSaveEventListener(
    private val noticeRepository: NoticeRepository
) {

    @Transactional
    @EventListener
    fun handleEvent(event: ResultSaveEvent) {
        try {
            ThreadUtil.sleep(1000)
            val code = event.result.code
            val notice = noticeRepository.findByCode(code)?.first()
            notice!!.status = "Done"
            noticeRepository.save(notice)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
}