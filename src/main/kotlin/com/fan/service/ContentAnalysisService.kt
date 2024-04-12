package com.fan.service

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil
import com.fan.client.NoticeDetailClient.getDetailPageUrl
import com.fan.db.entity.Notice
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
import java.util.UUID

@Component
class ContentAnalysisService(
    private val noticeDetailRepository: NoticeDetailRepository,
    private val noticeRepository: NoticeRepository,
    private val resultRepository: ResultRepository,
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository,
    private val searchByCodeSourceRepository: SearchByCodeSourceRepository,
    private val titleFilter: TitleFilter,
    private val noticeService: NoticeService,
    private val analysisLogService: AnalysisLogService
) {


    fun reAnalysisErrors() {
        val failedRecords = noticeDetailFailLogRepository.findAll()
        println("==========待分析错误公告数目:${failedRecords.size}=======")
        failedRecords.forEach { record ->
            val detail = noticeDetailRepository.findByStockAndCode(record.stock, record.code)
            detail?.let {
                val validResult = doAnalysis(detail)
                if (validResult.first) {
                    noticeDetailFailLogRepository.delete(record)
                }
            }
        }
        analysisLogService.saveAnalysisLog("分析错误数据", UUID.randomUUID().toString())
    }

    fun reAnalysisDetail() {
        ThreadUtil.execAsync {
            println("==========开始分析数据库中存储的全部公告内容，不会重新从网上加载")
            analysisDetail()
            reviewResults()
            println("==========重新分析详情完成")
        }
        analysisLogService.saveAnalysisLog("分析已有公告详情", UUID.randomUUID().toString())

    }

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
        val results = resultRepository.findAll()
        results.forEach {
            if (!validateTitleAndAccountCompanyName(it.title, it.accountCompanyName)) {
                resultRepository.delete(it)
                val detail = noticeDetailRepository.findByStockAndCode(it.stock, it.code)!!
                logErrorRecord(detail, Pair(it.accountCompanyName, it.amount.orEmpty()))
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
        val allDetails = noticeDetailRepository.findAll()
        val results = resultRepository.findAll()
        val filteredResult = filterResult(results)
        val codes = filteredResult.map { it.code }
        val stocks = filteredResult.map { it.stock }
        val todo = allDetails.filter { !(codes.contains(it.code) && stocks.contains(it.stock)) }
        println("==========待分析公告数目:${todo.size}=======")
        todo.forEach {
            try {
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
            println("======== 开始分析公告 $code 标题：${detail.title}========")
            val accountCompanyName = extractAccountingFirmName(detail.content)
            val noticeYear = DateUtil.parseDate(notice.date).year().toString()
            val amount = extractAccountingAmount()
            println("======== 【${notice.securityFullName}】${noticeYear} 年度合作的事务所名称：${accountCompanyName}========,合同金额：${amount}(待补充）")
            if (!isValidAccountName(accountCompanyName)) {
                println("======== 该名称【${accountCompanyName}】看上去不符合条件，错误已记录========")
                removeFromResultIfAny(detail)
                return Pair(false, Pair(accountCompanyName, "未开始提取"))
            }

            if (!isValidAmount(amount)) {
                println("======== 该合同金额信息【${amount}】看上去不符合条件，错误已记录========")
                removeFromResultIfAny(detail)
                return Pair(false, Pair(accountCompanyName, amount))
            }
            attachToResult(notice, code, detail, accountCompanyName, amount, noticeYear)
            return Pair(true, Pair(accountCompanyName, ""))
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(false, Pair("内部异常：${e.message}", ""))
        }
    }

    private fun attachToResult(
        notice: Notice,
        code: String,
        detail: NoticeDetail,
        accountCompanyName: String,
        amount: String,
        noticeYear: String
    ) {
        val exist = resultRepository.findByStockAndCode(notice.stock, code)
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
            year = noticeYear,
            title = encodeTitle(notice, code),
        )
        exist?.let {
            println("========更新分析结果======")
            result.id = exist.id
        }
        resultRepository.save(result)
    }

    private fun removeFromResultIfAny(detail: NoticeDetail) {
        resultRepository.findByStockAndCode(detail.stock, detail.code)?.let {
            println("======== 从汇总结果中删除【${detail.stock} - ${detail.title} - ${detail.code}】========")
            resultRepository.delete(it)
        }

    }

    private fun encodeTitle(notice: Notice, code: String) =
        "<a target='_blank' style='color: blue; text-decoration: none; font-weight: bold;' href='%s'>《%s》</a>".format(
            getDetailPageUrl(notice.stock, code), notice.title
        )

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