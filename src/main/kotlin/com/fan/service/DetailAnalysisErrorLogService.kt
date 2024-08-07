package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.NoticeDetailFailLog
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.NoticeRepository
import com.fan.service.RequestContext.Key.getRequestId
import org.springframework.stereotype.Service

@Service
class DetailAnalysisErrorLogService(
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository,
    private val noticeRepository: NoticeRepository,
) {

    fun logErrorRecord(
        detail: NoticeDetail,
        errResult: Pair<String, String>,
        context: RequestContext,
    ) {
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
                    amount = errResult.second,
                    requestId = getRequestId(context),
                ),
            )
        }
    }

    fun getFailedRecords(): List<NoticeDetailFailLog> = noticeDetailFailLogRepository.findAll()


    fun removeErrorLog(record: NoticeDetailFailLog) {
        noticeDetailFailLogRepository.delete(record)
    }


    fun removeErrorLogByDetail(detail: NoticeDetail) {
        noticeDetailFailLogRepository.findByCodeAndStock(detail.code, detail.stock)?.let {
            noticeDetailFailLogRepository.delete(it)
        }
    }


    fun ignoreErrorLog(id: Long) {
        val detailFailLogOptional = noticeDetailFailLogRepository.findById(id)
        detailFailLogOptional.ifPresent {
            val errorLog = detailFailLogOptional.get()
            errorLog.ignored = true
            noticeDetailFailLogRepository.save(errorLog)
        }
    }

    fun getFailLogById(id: Long) = noticeDetailFailLogRepository.findById(id)
}
