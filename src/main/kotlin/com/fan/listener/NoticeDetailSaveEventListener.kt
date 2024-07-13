package com.fan.listener

import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.db.repository.NoticeRepository
import com.fan.event.NoticeDetailSaveEvent
import com.fan.service.ContentAnalysisService
import com.fan.service.DetailAnalysisErrorLogService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NoticeDetailSaveEventListener(
    private val noticeRepository: NoticeRepository,
    private val contentAnalysisService: ContentAnalysisService,
    private val detailAnalysisErrorLogService: DetailAnalysisErrorLogService,
) {
    @EventListener
    fun handleEvent(event: NoticeDetailSaveEvent) {
        letPeopleKnow("公告详情下载成功事件：${event.noticeDetail.title}")
        val detail = event.noticeDetail
        try {
            val notice = noticeRepository.findById(detail.noticeId)
            val analysisResult = contentAnalysisService.doAnalysisDetailAndSaveResult(detail, detail.context)
            if (!(notice.isPresent && analysisResult.first)) {
                detailAnalysisErrorLogService.logErrorRecord(detail, analysisResult.second, detail.context)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
