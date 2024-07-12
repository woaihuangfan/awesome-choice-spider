package com.fan.listener

import com.fan.event.NoticeSaveEvent
import com.fan.service.NoticeDetailService
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class NoticeSaveEventListener(
    private val noticeDetailService: NoticeDetailService,
) {
    @TransactionalEventListener
    fun handleEvent(event: NoticeSaveEvent) {
        noticeDetailService.fetchOrUpdateNoticeDetail(event.notice, event.notice.context)
    }
}
