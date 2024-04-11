package com.fan.service

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.NoticeDetailFailLog
import com.fan.db.entity.Result
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.NoticeDetailRepository
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.ResultRepository
import com.fan.db.repository.SearchByCodeSourceRepository
import com.fan.extractor.AccountCompanyAmountFilter.isValidAmount
import com.fan.extractor.AccountCompanyNameFilter.isValidAccountName
import com.fan.extractor.DefaultAccountingAmountExtractor.extractAccountingAmount
import com.fan.extractor.DefaultAccountingFirmNameExtractor.extractAccountingFirmName
import com.fan.filter.TitleFilter
import org.springframework.stereotype.Component

@Component
class ContentAnalysisService(
    private val noticeDetailRepository: NoticeDetailRepository,
    private val noticeRepository: NoticeRepository,
    private val resultRepository: ResultRepository,
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository,
    private val searchByCodeSourceRepository: SearchByCodeSourceRepository,
    private val titleFilter: TitleFilter,
    private val noticeService: NoticeService,
) {


    fun reAnalysis() {
        val year = DateUtil.thisYear().toString()
        val failedRecords = noticeDetailFailLogRepository.findAllByYear(year)
        failedRecords.forEach { record ->
            val detail = noticeDetailRepository.findByStockAndCode(record.stock, record.code)
            detail?.let {
                val analysisResult = doAnalysis(detail)
                if (analysisResult.first) {
                    noticeDetailFailLogRepository.delete(record)
                }
            }
        }
    }

    fun reAnalysisDetail() {
        ThreadUtil.execAsync {
            println("==========开始分析数据库中存储的公告内容，不会重新从网上加载")
            analysisDetail()
            println("==========重新分析详情完成")
        }

    }

    fun reAnalysisAll() {
        ThreadUtil.execAsync {
            fetchDetail()
            analysisDetail()
            println("==========重新分析完成")
        }

    }

    private fun fetchDetail() {
        searchByCodeSourceRepository.findAll().forEach { notice ->
            try {
                println("==========开始分析过滤公司【${notice.companyName}-${notice.stock}】的公告标题：${notice.title}")
                if (titleFilter.doFilter(notice.title)) {
                    noticeService.saveOrUpdateNotice(notice)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun analysisDetail() {
        val allDetails = noticeDetailRepository.findAll()
        val results = resultRepository.findAll()
        val filteredResult = filterResult(results)
        val codes = filteredResult.map { it.code }
        val stocks = filteredResult.map { it.stock }
        val todo = allDetails.filter { !(codes.contains(it.code) && stocks.contains(it.stock)) }
        todo.forEach {
            try {
                println("==========开始分析公告内容(${it.stock}) - ${it.title}")
                val analysisResult = doAnalysis(it)
                if (analysisResult.first) {
                    noticeDetailFailLogRepository.deleteByCodeAndStock(it.code, it.stock)
                } else {
                    logErrorRecord(it, analysisResult.second)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun filterResult(results: List<Result>): List<Result> {
        return results.filter {
            isValidAccountName(it.accountCompanyName)
        }
    }


    fun doAnalysis(detail: NoticeDetail): Pair<Boolean, Pair<String, String>> {
        try {
            val code = detail.code
            val notice = noticeRepository.findById(detail.noticeId).get()
            println("======== 开始分析公告内容 ${code} 标题：${detail.title}========")
            val accountCompanyName = extractAccountingFirmName(detail.content)
            val noticeYear = DateUtil.parseDate(notice.date).year().toString()
            val amount = extractAccountingAmount()
            println("======== 【${notice.securityFullName}】${noticeYear} 年度合作的事务所名称：${accountCompanyName}========,合同金额：${amount}(待补充）")
            if (!isValidAccountName(accountCompanyName)) {
                println("======== 该名称【${accountCompanyName}】看上去不符合条件，错误已记录========")
                return Pair(false, Pair(accountCompanyName, "为开始提取"))
            }

            if (!isValidAmount(amount)) {
                println("======== 该合同金额信息【${amount}】看上去不符合条件，错误已记录========")
                return Pair(false, Pair(accountCompanyName, amount))
            }
            val exist = resultRepository.findByStockAndYearAndCode(notice.stock, noticeYear, code)
            val result = Result(
                noticeId = detail.noticeId,
                name = notice.securityFullName,
                stock = detail.stock,
                date = notice.date.substring(
                    0, 10
                ),
                accountCompanyName = accountCompanyName,
                amount = amount,
                code = code,
                year = noticeYear
            )
            exist?.let {
                result.id = exist.id
            }
            resultRepository.save(result)
            return Pair(true, Pair(accountCompanyName, ""))
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, Pair("内部异常：${e.message}", ""))
        }
    }

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
}