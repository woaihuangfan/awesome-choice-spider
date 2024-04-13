package com.fan.listener

import com.fan.event.NoticeSaveEvent
import com.fan.service.NoticeDetailService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NoticeSaveEventListener(
    private val noticeDetailService: NoticeDetailService
) {

    @EventListener
    fun handleEvent(event: NoticeSaveEvent) {
        noticeDetailService.fetchOrUpdateNoticeDetail(event.notice)
    }
}