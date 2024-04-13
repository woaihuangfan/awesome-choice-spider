package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.NoticeDetailFailLog
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.NoticeRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class DetailAnalysisErrorLogService(
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository,
    private val noticeRepository: NoticeRepository
) {

    @Transactional
    fun logErrorRecord(detail: NoticeDetail, errResult: Pair<String, String>) {
        val exist = noticeDetailFailLogRepository.findByCodeAndStock(detail.code, detail.stock)
        if (exist == null) {
            val notice = noticeRepository.findById(detail.noticeId)
            val year = DateUtil.parseDate(notice.get().date).year().toString()
            noticeDetailFailLogRepository.save(
                NoticeDetailFailLog(
                    code = detail.code,
                    content = detail.content,
                    stock = detail.stock,
                    title = detail.title,
                    noticeId = detail.noticeId,
                    year = year,
                    accountCompanyName = errResult.first,
                    amount = errResult.second
                )
            )
        }

    }

    fun getFailedRecords(): List<NoticeDetailFailLog> = noticeDetailFailLogRepository.findAll()

    @Transactional
    fun removeErrorLog(record: NoticeDetailFailLog) {
        noticeDetailFailLogRepository.delete(record)
    }

    @Transactional
    fun removeErrorLogByDetail(detail: NoticeDetail) {
        noticeDetailFailLogRepository.deleteByCodeAndStock(detail.code, detail.stock)
    }

    fun getFailLogById(id: Long) = noticeDetailFailLogRepository.findById(id)

}