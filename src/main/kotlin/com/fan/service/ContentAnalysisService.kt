package com.fan.service

import cn.hutool.core.thread.ThreadUtil
import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.Result
import com.fan.db.repository.SearchByCodeSourceRepository
import com.fan.extractor.AccountCompanyAmountFilter.isValidAmount
import com.fan.extractor.AccountCompanyNameFilter.isValidAccountName
import com.fan.extractor.DefaultAccountingAmountExtractor.extractAccountingAmount
import com.fan.extractor.DefaultAccountingFirmNameExtractor.extractAccountingFirmName
import com.fan.filter.TitleFilter
import jakarta.transaction.Transactional
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component

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
    fun reAnalysisErrors(context: RequestContext) {
        analysisErrors(context)
        analysisLogService.saveAnalysisLog("分析错误数据", context)
    }

    @Transactional
    fun reAnalysisDetail(context: RequestContext) {
        letPeopleKnow("==========开始分析数据库中存储的全部公告内容，不会重新从网上加载")
        analysisDetail(context)
        reviewResults(context)
        letPeopleKnow("==========重新分析详情完成")
        analysisLogService.saveAnalysisLog("分析已有公告详情", context)
    }

    @Transactional
    fun reAnalysisAll(context: RequestContext) {
        ThreadUtil.execAsync {
            fetchDetail(context)
            analysisDetail(context)
            analysisErrors(context)
            reviewResults(context)
            letPeopleKnow("==========重新分析完成")
            analysisLogService.saveAnalysisLog("重新分析公告标题并分析详情数据", context)
        }
    }

    private fun analysisErrors(context: RequestContext) {
        val failedRecords = detailAnalysisErrorLogService.getFailedRecords()
        letPeopleKnow("==========开始分析！待分析错误公告数目:${failedRecords.size}=======")
        failedRecords.forEach { record ->

            val detail = noticeDetailService.getNoticeDetailByFailLog(record)
            detail?.let {
                if (!isTitleCompliant(detail.title)) {
                    letPeopleKnow("========公告${detail.title}【${detail.stock}】 标题不符合要求 ===========")
                    return@let
                }
                val validResult = doAnalysisDetailAndSaveResult(detail, context)
                if (validResult.first) {
                    detailAnalysisErrorLogService.removeErrorLog(record)
                }
            }
        }
    }

    private fun reviewResults(context: RequestContext) {
        val results = resultService.getAllResults()
        results.forEach {
            if (!validateTitleAndAccountCompanyName(it.title, it.accountCompanyName)) {
                resultService.removeResult(it)
                val detail = noticeDetailService.getNoticeDetailFromResult(it)!!
                detailAnalysisErrorLogService.logErrorRecord(
                    detail,
                    Pair(it.accountCompanyName, it.amount.orEmpty()),
                    context,
                )
            }
        }
    }

    private fun validateTitleAndAccountCompanyName(
        title: String,
        accountCompanyName: String,
    ): Boolean = isTitleCompliant(title) && isValidAccountName(accountCompanyName)

    private fun fetchDetail(context: RequestContext) {
        searchByCodeSourceRepository.findAll().forEach { notice ->
            try {
                letPeopleKnow("==========开始分析过滤公司【${notice.companyName}-${notice.stock}】的公告标题：${notice.title}")
                if (isTitleCompliant(notice.title)) {
                    // 会触发加载详情
                    noticeService.saveOrUpdateNotice(notice, context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun isTitleCompliant(title: String) = titleFilter.doFilter(title)

    private fun analysisDetail(context: RequestContext) {
        val allDetails = noticeDetailService.getAllNoticeDetails()
        val results = resultService.getAllResults()
        val filteredResult = filterResult(results)
        val codes = filteredResult.map { it.code }
        val stocks = filteredResult.map { it.stock }
        val todo = allDetails.filter { !(codes.contains(it.code) && stocks.contains(it.stock)) }
        letPeopleKnow("==========待分析公告数目:${todo.size}=======")
        runBlocking {
            val jobs =
                todo.map { detail ->
                    convertToJob(detail, context)
                }
            jobs.forEach {
                it.join()
            }
        }
    }

    private fun CoroutineScope.convertToJob(
        detail: NoticeDetail,
        context: RequestContext,
    ) = launch(context) {
        try {
            val analysisResult = doAnalysisDetailAndSaveResult(detail, context)
            if (analysisResult.first) {
                detailAnalysisErrorLogService.removeErrorLogByDetail(detail)
            } else {
                detailAnalysisErrorLogService.logErrorRecord(detail, analysisResult.second, context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun filterResult(results: List<Result>): List<Result> =
        results.filter {
            isValidAccountName(it.accountCompanyName) && isTitleCompliant(it.title)
        }

    fun doAnalysisDetailAndSaveResult(
        detail: NoticeDetail,
        context: RequestContext,
    ): Pair<Boolean, Pair<String, String>> {
        try {
            val code = detail.code
            val notice = noticeService.getNoticeFromDetail(detail)
            letPeopleKnow("======== 开始分析公告 $code 标题：${detail.title}========")
            val accountCompanyName = extractAccountingFirmName(detail.content)
            val amount = extractAccountingAmount()
            letPeopleKnow("======== 【${notice.securityFullName}】合作的事务所名称：$accountCompanyName========,合同金额：$amount(待补充）")
            if (!isValidAccountName(accountCompanyName)) {
                letPeopleKnow("======== 该名称【$accountCompanyName】看上去不符合条件，错误已记录========")
                resultService.removeFromResultIfAny(detail)
                return Pair(false, Pair(accountCompanyName, "未开始提取"))
            }

            if (!isValidAmount(amount)) {
                letPeopleKnow("======== 该合同金额信息【$amount】看上去不符合条件，错误已记录========")
                resultService.removeFromResultIfAny(detail)
                return Pair(false, Pair(accountCompanyName, amount))
            }
            resultService.attachToResult(notice, detail, accountCompanyName, amount, context)
            return Pair(true, Pair(accountCompanyName, ""))
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, Pair("内部异常：${e.message}", ""))
        }
    }
}
