package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.db.entity.AnalysisLog
import com.fan.db.repository.AnalysisLogRepository
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.NoticeDetailRepository
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.ResultRepository
import com.fan.db.repository.SearchByCodeSourceRepository
import com.fan.service.RequestContext.Key.getRequestId
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class AnalysisLogService(
    private val noticeRepository: NoticeRepository,
    private val noticeDetailRepository: NoticeDetailRepository,
    private val resultRepository: ResultRepository,
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository,
    private val analysisLogRepository: AnalysisLogRepository,
    private val searchByCodeSourceRepository: SearchByCodeSourceRepository,
) {
    @Transactional
    fun saveAnalysisLog(
        type: String,
        context: RequestContext,
    ) {
        val thisYear = DateUtil.thisYear()
        val validTitles = noticeRepository.count()
        val detailsCounts = noticeDetailRepository.count()
        val validAccountNameCounts = resultRepository.count()
        val failedAccounts = noticeDetailFailLogRepository.countByYear(thisYear.toString())
        analysisLogRepository.save(
            AnalysisLog(
                date = DateUtil.now(),
                type = type,
                new = getCollectedCount(context),
                validTitles = validTitles,
                validDetails = detailsCounts,
                successAccounts = validAccountNameCounts,
                failedAccounts = failedAccounts,
            ),
        )
    }

    private fun getCollectedCount(context: RequestContext): Int = searchByCodeSourceRepository.countByRequestId(getRequestId(context))
}
