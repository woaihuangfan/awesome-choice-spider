package com.fan.listener

import com.fan.db.repository.NoticeRepository
import com.fan.event.NoticeDetailSaveEvent
import com.fan.service.ContentAnalysisService
import com.fan.service.DetailAnalysisErrorLogService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionalEventListener

@Component
class NoticeDetailSaveEventListener(
    private val noticeRepository: NoticeRepository,
    private val contentAnalysisService: ContentAnalysisService,
    private val detailAnalysisErrorLogService: DetailAnalysisErrorLogService,
) {
    @Transactional
    @TransactionalEventListener
    fun handleEvent(event: NoticeDetailSaveEvent) {
        val detail = event.noticeDetail
        try {
            val notice = noticeRepository.findById(detail.noticeId)
            val analysisResult = contentAnalysisService.doAnalysisDetailAndSaveResult(detail, detail.context)
            if (!(notice.isPresent && analysisResult.first)) {
                detailAnalysisErrorLogService.logErrorRecord(detail, analysisResult.second,  detail.context)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
