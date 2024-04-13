package com.fan.listener

import com.fan.db.repository.NoticeRepository
import com.fan.event.NoticeDetailSaveEvent
import com.fan.service.ContentAnalysisService
import com.fan.service.DetailAnalysisErrorLogService
import jakarta.transaction.Transactional
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NoticeDetailSaveEventListener(
    private val noticeRepository: NoticeRepository,
    private val contentAnalysisService: ContentAnalysisService,
    private val detailAnalysisErrorLogService: DetailAnalysisErrorLogService
) {

    @Transactional
    @EventListener
    fun handleEvent(event: NoticeDetailSaveEvent) {
        val detail = event.noticeDetail
        try {
            val notice = noticeRepository.findById(detail.noticeId)
            val analysisResult = contentAnalysisService.doAnalysisDetailAndSaveResult(detail)
            if (!(notice.isPresent && analysisResult.first)) {
                detailAnalysisErrorLogService.logErrorRecord(detail, analysisResult.second)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}