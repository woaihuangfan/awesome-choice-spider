package com.fan.service

import cn.hutool.core.date.DateTime
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.PageUtil
import com.fan.client.SearchClient
import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.controller.WebSocketController.Companion.notifyClientJobCanceled
import com.fan.controller.WebSocketController.Companion.notifyClientJobIsDone
import com.fan.db.entity.Company
import com.fan.db.entity.NoticeSearchHistory
import com.fan.db.entity.SearchByCodeSource
import com.fan.db.repository.CompanyRepository
import com.fan.db.repository.NoticeSearchHistoryRepository
import com.fan.db.repository.SearchByCodeSourceRepository
import com.fan.enums.SearchType
import com.fan.filter.SearchFilterChain
import com.fan.po.DataCollectParam
import com.fan.response.Item
import com.fan.response.SearchByCodeResponse
import com.fan.service.RequestContext.Key.getRequestId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.commons.collections4.CollectionUtils
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.time.Duration
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

private const val ROWS = 50

@Component
class SearchByCodeCollector(
    private val noticeService: NoticeService,
    private val searchFilterChain: SearchFilterChain,
    private val searchByCodeSourceRepository: SearchByCodeSourceRepository,
    private val companyRepository: CompanyRepository,
    private val noticeSearchHistoryRepository: NoticeSearchHistoryRepository,
    analysisLogService: AnalysisLogService,
    collectLogService: CollectLogService,
) : AbstractDataCollector(searchByCodeSourceRepository, analysisLogService, collectLogService) {
    private val isInterrupted = AtomicBoolean(false)

    fun getStatus(): Status =
        if (lock.get() == 1) {
            Status.RUNNING
        } else {
            Status.READY
        }

    fun cancel() {
        if (getStatus() == Status.RUNNING) {
            isInterrupted.set(true)
            letPeopleKnow("操作将被取消，等待进行中任务完成")
            notifyClientJobCanceled()
        }
    }

    fun hasFinished(): Boolean = !isInterrupted.get()


    override fun doCollect(
        param: DataCollectParam,
        type: SearchType,
        context: RequestContext,
    ) {
        val codeEntities = companyRepository.findAll()
        val counter = AtomicInteger(0)
        val startTime = Instant.now()
        runBlocking {
            val jobs =
                codeEntities.map { entity ->
                    convertToJob(entity, param, counter, context)
                }
            jobs.forEach {
                if (!isInterrupted.get()) {
                    it.join()
                }
            }
            val endTime = Instant.now()
            val duration = Duration.between(startTime, endTime)
            letPeopleKnow("所有任务已完成，成功爬取 ${counter.get()} 家公司在【${param.tillDate} - ${DateUtil.today()} 】期间的公告数据。")
            letPeopleKnow("总计用时：${duration.toMinutes()} 分钟 ${duration.seconds % 60} 秒")
            if (!isInterrupted.get()) {
                notifyClientJobIsDone()
            }
            isInterrupted.set(false)
        }
    }

    private fun CoroutineScope.convertToJob(
        entity: Company,
        param: DataCollectParam,
        counter: AtomicInteger,
        context: RequestContext,
    ): Job =
        launch(context) {
            if (!isInterrupted.get()) {
                val stock = entity.stock
                val tillDate = negotiateTillDate(stock, param)
                letPeopleKnow(
                    "==========将采集【${entity.stock}】$tillDate -${DateUtil.date()} 期间的公告数据==========",
                )
                val totalPages = getTotalPages(stock)
                // 单个股票的爬取
                for (i in 1..totalPages) {
                    if (!isInterrupted.get()) {
                        letPeopleKnow("==========开始爬取第 $i 页, 共【$totalPages】页==========")
                        if (successfullyCollectDataByPages(stock, i, tillDate, context)) break
                    }
                }
                // 增加计数器
                counter.incrementAndGet()
            }
        }

    private fun negotiateTillDate(
        stock: String,
        param: DataCollectParam,
    ): DateTime {
        val userPreferDate = DateUtil.parseDate(param.tillDate)
        val histories = noticeSearchHistoryRepository.findByStockOrderByDateDesc(stock)
        if (histories.isEmpty()) {
            return userPreferDate
        }
        val lastTillDate = DateUtil.parseDate(histories.first().tillDate)
        val lastUpdatedDate = DateUtil.parseDate(histories.first().date)

        if (userPreferDate > lastTillDate && userPreferDate < lastUpdatedDate) {
            letPeopleKnow(
                "======== $lastTillDate -${lastUpdatedDate}之间的数据已采集，将自动修正起始日期为${
                    DateUtil.format(
                        lastUpdatedDate,
                        "yyyy年MM月dd日 ",
                    )
                }===== ",
            )
            return lastUpdatedDate
        }
        return userPreferDate
    }

    private fun successfullyCollectDataByPages(
        stock: String,
        i: Int,
        tillDate: DateTime,
        context: RequestContext,
    ): Boolean {
        try {
            val searchByCodeResponse = SearchClient.searchByCode(stock, i, ROWS)
            filterAndSave(searchByCodeResponse, stock, tillDate, context)
        } catch (e: Exception) {
            if (e.message == "0") {
                val requestId = getRequestId(context)
                val count = searchByCodeSourceRepository.countByStockAndRequestId(stock, requestId)
                noticeSearchHistoryRepository.save(
                    NoticeSearchHistory(
                        stock = stock,
                        tillDate = tillDate.toString(),
                        count = count,
                        requestId = requestId,
                        date = DateUtil.now(),
                    ),
                )
                letPeopleKnow("==========证券代码为【$stock】的公司 $tillDate - ${DateUtil.date()}期间的公告爬取完成(新采集${count}条记录)，将停止爬取==========")
                return true
            } else {
                e.printStackTrace()
            }
        }
        return false
    }

    private fun getTotalPages(code: String): Int {
        val searchByCodeResponse = SearchClient.searchByCode(code, 1, 1)
        val totalHits = searchByCodeResponse.data.total_hits
        letPeopleKnow("==========证券代码为【$code】的公司查询到的公告总数为:$totalHits==========")
        return PageUtil.totalPage(totalHits, 50)
    }

    private fun filterAndSave(
        searchByCodeResponse: SearchByCodeResponse,
        stock: String,
        tillDate: DateTime,
        context: RequestContext,
    ) {
        if (searchByCodeResponse.success == 1) {
            searchByCodeResponse.data.list.forEach { item ->
                val noticeDate = DateUtil.parseDate(item.notice_date)
                if (noticeDate < tillDate) {
                    throw RuntimeException("0")
                }
                updateCompanyName(item, stock)
                saveOriginalData(item, context)
                saveValidNoticeFilteredByTitleRule(item, context)
            }
        }
    }

    private fun saveValidNoticeFilteredByTitleRule(
        item: Item,
        context: RequestContext,
    ) {
        val logMessage = "【${item.codes.first().short_name}】${item.title}"
        if (searchFilterChain.doFilter(item)) {
            letPeopleKnow("======== $logMessage 符合标题筛选条件，将进入待分析列表========")
            noticeService.saveOrUpdateNotice(item, context)
        } else {
            letPeopleKnow("======== $logMessage 不符合标题筛选条件========")
        }
    }

    private fun updateCompanyName(
        item: Item,
        stock: String,
    ) {
        val companyName = item.codes.first().short_name
        val company = companyRepository.findByStock(stock)
        company?.let {
            if (StringUtils.hasText(company.companyName) && company.companyName != companyName) {
                letPeopleKnow("==========证券代码为【$stock】的公司名称发生变化，原公司名称为【${company.companyName}】，新公司名称为【$companyName】==========")
            }
            it.companyName = companyName
            companyRepository.save(it)
        }
    }

    private fun saveOriginalData(
        item: Item,
        context: RequestContext,
    ) {
        val code = item.art_code
        val title = item.title
        val date = item.notice_date.substring(0, 10)
        val logMessage = "【${item.codes.first().short_name}】$title"
        if (CollectionUtils.isNotEmpty(
                searchByCodeSourceRepository.findByStockAndTitleAndCode(
                    item.codes.first().stock_code,
                    title,
                    code,
                ),
            )
        ) {
            letPeopleKnow("======== $logMessage 已存在 ========")
            return
        }
        val column = if (item.columns.isNotEmpty()) item.columns.first() else null
        val source =
            SearchByCodeSource(
                stock = item.codes.first().stock_code,
                code = code,
                columnCode = column?.column_code ?: "",
                columnName = column?.column_name ?: "",
                title = title,
                date = date,
                requestId = getRequestId(context),
                year = DateUtil.thisYear().toString(),
                companyName = item.codes.first().short_name,
                createDate = DateUtil.now(),
            )
        searchByCodeSourceRepository.save(source)
        letPeopleKnow("======== 新收录 $logMessage ========")
    }
}
