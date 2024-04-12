package com.fan.listener

import cn.hutool.core.thread.ThreadUtil
import com.fan.db.repository.NoticeRepository
import com.fan.event.ResultSaveEvent
import jakarta.transaction.Transactional
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.lang.Long.parseLong

@Component
class ResultSaveEventListener(
    private val noticeRepository: NoticeRepository
) {

    @Transactional
    @EventListener
    fun handleEvent(event: ResultSaveEvent) {
        try {
            ThreadUtil.sleep(100)
            event.result.noticeId.split(",").forEach {
                val notice = noticeRepository.findById(parseLong(it)).get()
                notice.status = "Done"
                noticeRepository.save(notice)
            }

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
}