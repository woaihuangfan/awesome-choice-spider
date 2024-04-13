package com.fan.service

import cn.hutool.core.thread.ThreadUtil
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.Result
import com.fan.db.repository.SearchByCodeSourceRepository
import com.fan.extractor.AccountCompanyAmountFilter.isValidAmount
import com.fan.extractor.AccountCompanyNameFilter.isValidAccountName
import com.fan.extractor.DefaultAccountingAmountExtractor.extractAccountingAmount
import com.fan.extractor.DefaultAccountingFirmNameExtractor.extractAccountingFirmName
import com.fan.filter.TitleFilter
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ContentAnalysisService(
    private val searchByCodeSourceRepository: SearchByCodeSourceRepository,
    private val titleFilter: TitleFilter,
    private val noticeService: NoticeService,
    private val analysisLogService: AnalysisLogService,
    private val resultService: ResultService,
    private val detailAnalysisErrorLogService: DetailAnalysisErrorLogService,
    private val noticeDetailService: NoticeDetailService,
) {

    @Transactional
    fun reAnalysisErrors() {
        val failedRecords = detailAnalysisErrorLogService.getFailedRecords()
        println("==========待分析错误公告数目:${failedRecords.size}=======")
        failedRecords.forEach { record ->
            val detail = noticeDetailService.getNoticeDetailByFailLog(record)
            detail?.let {
                val validResult = doAnalysisAndSaveResult(detail)
                if (validResult.first) {
                    detailAnalysisErrorLogService.removeErrorLog(record)
                }
            }
        }
        analysisLogService.saveAnalysisLog("分析错误数据", UUID.randomUUID().toString())
    }

    @Transactional
    fun reAnalysisDetail() {
        ThreadUtil.execAsync {
            println("==========开始分析数据库中存储的全部公告内容，不会重新从网上加载")
            analysisDetail()
            reviewResults()
            println("==========重新分析详情完成")
        }
        analysisLogService.saveAnalysisLog("分析已有公告详情", UUID.randomUUID().toString())

    }

    @Transactional
    fun reAnalysisAll() {
        val requestId = UUID.randomUUID().toString()
        ThreadUtil.execAsync {
            fetchDetail(requestId)
            analysisDetail()
            reAnalysisErrors()
            reviewResults()
            println("==========重新分析完成")
            analysisLogService.saveAnalysisLog("重新分析公告标题并分析详情数据", UUID.randomUUID().toString())
        }

    }

    private fun reviewResults() {
        val results = resultService.getAllResults()
        results.forEach {
            if (!validateTitleAndAccountCompanyName(it.title, it.accountCompanyName)) {
                resultService.removeResult(it)
                val detail = noticeDetailService.getNoticeDetailFromResult(it)!!
                detailAnalysisErrorLogService.logErrorRecord(detail, Pair(it.accountCompanyName, it.amount.orEmpty()))
            }
        }
    }


    private fun validateTitleAndAccountCompanyName(title: String, accountCompanyName: String): Boolean {
        return titleFilter.doFilter(title) && isValidAccountName(accountCompanyName)
    }

    private fun fetchDetail(requestId: String) {
        searchByCodeSourceRepository.findAll().forEach { notice ->
            try {
                println("==========开始分析过滤公司【${notice.companyName}-${notice.stock}】的公告标题：${notice.title}")
                if (titleFilter.doFilter(notice.title)) {
                    noticeService.saveOrUpdateNotice(notice, requestId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun analysisDetail() {
        val allDetails = noticeDetailService.getAllNoticeDetails()
        val results = resultService.getAllResults()
        val filteredResult = filterResult(results)
        val codes = filteredResult.map { it.code }
        val stocks = filteredResult.map { it.stock }
        val todo = allDetails.filter { !(codes.contains(it.code) && stocks.contains(it.stock)) }
        println("==========待分析公告数目:${todo.size}=======")
        todo.forEach {
            try {
                val analysisResult = doAnalysisAndSaveResult(it)
                if (analysisResult.first) {
                    detailAnalysisErrorLogService.removeErrorLogByDetail(it)
                } else {
                    detailAnalysisErrorLogService.logErrorRecord(it, analysisResult.second)
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

    @Transactional
    fun doAnalysisAndSaveResult(detail: NoticeDetail): Pair<Boolean, Pair<String, String>> {
        try {
            val code = detail.code
            val notice = noticeService.getNoticeFromDetail(detail)
            println("======== 开始分析公告 $code 标题：${detail.title}========")
            val accountCompanyName = extractAccountingFirmName(detail.content)
            val amount = extractAccountingAmount()
            println("======== 【${notice.securityFullName}】合作的事务所名称：${accountCompanyName}========,合同金额：${amount}(待补充）")
            if (!isValidAccountName(accountCompanyName)) {
                println("======== 该名称【${accountCompanyName}】看上去不符合条件，错误已记录========")
                resultService.removeFromResultIfAny(detail)
                return Pair(false, Pair(accountCompanyName, "未开始提取"))
            }

            if (!isValidAmount(amount)) {
                println("======== 该合同金额信息【${amount}】看上去不符合条件，错误已记录========")
                resultService.removeFromResultIfAny(detail)
                return Pair(false, Pair(accountCompanyName, amount))
            }
            resultService.attachToResult(notice, detail, accountCompanyName, amount)
            return Pair(true, Pair(accountCompanyName, ""))
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, Pair("内部异常：${e.message}", ""))
        }
    }


}