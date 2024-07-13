package com.fan.listener

import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.event.NoticeSaveEvent
import com.fan.service.NoticeDetailService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NoticeSaveEventListener(
    private val noticeDetailService: NoticeDetailService,
) {
    @EventListener
    fun handleEvent(event: NoticeSaveEvent) {
        letPeopleKnow("公告标记事件：${event.notice.title}")
        noticeDetailService.fetchOrUpdateNoticeDetail(event.notice, event.notice.context)
    }
}
