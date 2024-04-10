package com.fan.listener

import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.ResultRepository
import com.fan.event.NoticeDetailSaveEvent
import com.fan.service.ContentAnalysisService
import jakarta.transaction.Transactional
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NoticeDetailSaveEventListener(
    private val resultRepository: ResultRepository,
    private val noticeRepository: NoticeRepository,
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository,
    private val contentAnalysisService: ContentAnalysisService
) {

    @Transactional
    @EventListener
    fun handleEvent(event: NoticeDetailSaveEvent) {
        val detail = event.noticeDetail
        try {
            val notice = noticeRepository.findById(detail.noticeId)
            if (!(notice.isPresent && contentAnalysisService.doAnalysis(detail))) {
                contentAnalysisService.logErrorRecord(detail)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}