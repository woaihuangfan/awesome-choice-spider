package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.NoticeDetailFailLog
import com.fan.db.entity.Result
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.NoticeDetailRepository
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.ResultRepository
import com.fan.extractor.DefaultAccountingFirmNameExtractor.extractAccountingFirmName
import org.springframework.stereotype.Component

@Component
class ContentAnalysisService(
    private val noticeDetailRepository: NoticeDetailRepository,
    private val noticeRepository: NoticeRepository,
    private val resultRepository: ResultRepository,
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository
) {


    fun reAnalysis() {
        val year = DateUtil.thisYear().toString()
        val failedRecords = noticeDetailFailLogRepository.findAllByYear(year)
        failedRecords.forEach { record ->
            val detail = noticeDetailRepository.findByCode(record.code)
            if (doAnalysis(detail)) {
                noticeDetailFailLogRepository.delete(record)
            }
        }
    }

    fun reAnalysisAll() {

        val allDetails = noticeDetailRepository.findAll()
        val results = resultRepository.findAll()
        val codes = results.map { it.code }
        val todo = allDetails.filter { !codes.contains(it.code) }
        todo.forEach {
            if (doAnalysis(it)) {
                noticeDetailFailLogRepository.deleteByCode(it.code)
            } else {
                logErrorRecord(it)
            }
        }
    }


    fun doAnalysis(detail: NoticeDetail): Boolean {
        try {

            val code = detail.code
            val notice = noticeRepository.findById(detail.noticeId).get()
            val accountCompanyName = extractAccountingFirmName(detail.content)
            val noticeYear = DateUtil.parseDate(notice.date).year().toString()
            if (accountCompanyName.isNotBlank()) {
                val exist = resultRepository.findByCode(code)
                if (exist == null) {
                    val result = Result(
                        noticeId = detail.noticeId,
                        name = notice.securityFullName,
                        stock = detail.stock,
                        date = notice.date.substring(
                            0,
                            10
                        ),
                        accountCompanyName = accountCompanyName,
                        code = code,
                        year = noticeYear
                    )
                    resultRepository.save(result)
                }
                return true
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    fun logErrorRecord(detail: NoticeDetail) {
        val exist = noticeDetailFailLogRepository.findByCode(detail.code)
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
                    year = year
                )
            )
        }

    }
}