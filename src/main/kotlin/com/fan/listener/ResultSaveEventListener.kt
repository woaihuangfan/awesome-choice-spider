package com.fan.listener

import cn.hutool.core.thread.ThreadUtil
import com.fan.db.repository.NoticeRepository
import com.fan.event.ResultSaveEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ResultSaveEventListener(
    private val noticeRepository: NoticeRepository,
) {
    @EventListener
    fun handleEvent(event: ResultSaveEvent) {
        try {
            ThreadUtil.sleep(100)
            val notice = noticeRepository.findById(event.result.noticeId).get()
            notice.status = "Done"
            notice.context = event.result.context
            noticeRepository.save(notice)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
