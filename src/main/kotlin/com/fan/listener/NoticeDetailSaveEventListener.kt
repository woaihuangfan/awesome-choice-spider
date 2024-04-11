package com.fan.listener

import com.fan.db.repository.NoticeRepository
import com.fan.event.NoticeDetailSaveEvent
import com.fan.service.ContentAnalysisService
import jakarta.transaction.Transactional
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NoticeDetailSaveEventListener(
    private val noticeRepository: NoticeRepository,
    private val contentAnalysisService: ContentAnalysisService
) {

    @Transactional
    @EventListener
    fun handleEvent(event: NoticeDetailSaveEvent) {
        val detail = event.noticeDetail
        try {
            val notice = noticeRepository.findById(detail.noticeId)
            val analysisResult = contentAnalysisService.doAnalysis(detail)
            if (!(notice.isPresent && analysisResult.first)) {
                contentAnalysisService.logErrorRecord(detail, analysisResult.second)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}