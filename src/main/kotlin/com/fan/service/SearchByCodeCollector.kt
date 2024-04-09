package com.fan.service

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil.sleep
import cn.hutool.core.util.PageUtil
import com.fan.client.SearchClient
import com.fan.db.entity.Notice
import com.fan.db.entity.SearchByCodeSource
import com.fan.db.repository.CodeRepository
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.SearchByCodeSourceRepository
import com.fan.db.repository.SearchLogRepository
import com.fan.enums.SearchType
import com.fan.filter.SummaryFilterChain
import com.fan.response.Item
import com.fan.response.SearchByCodeResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

private const val ROWS = 50

@Component
class SearchByCodeCollector(
    private val noticeRepository: NoticeRepository,
    private val summaryFilterChain: SummaryFilterChain,
    private val searchByCodeSourceRepository: SearchByCodeSourceRepository,
    private val codeRepository: CodeRepository,
    searchLogRepository: SearchLogRepository

) : AbstractDataCollector(searchByCodeSourceRepository, searchLogRepository) {


    @Transactional
    override fun doCollect(param: String, type: SearchType, requestId: String) {
        val codeEntities = codeRepository.findAll()
        for (entity in codeEntities) {
            sleep(1000)
            println("==========开始爬取证券代码为【${entity.code}】的公司的当年度的公告==========")
            val totalPages = getTotalPages(entity.code)
            for (i in 1..totalPages) {
                sleep(1000)
                println("==========开始爬取第 $i 页, 共 $totalPages==========")
                try {
                    val searchByCodeResponse = SearchClient.searchByCode(param, i, ROWS)
                    filterAndSave(searchByCodeResponse, requestId)
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (e.message == "0") {
                        println("==========当年度公告爬取完成，将停止爬取==========")
                        break
                    }
                }
            }
        }

    }

    private fun getTotalPages(code: String): Int {
        val searchByCodeResponse = SearchClient.searchByCode(code, 1, 1)
        return PageUtil.totalPage(searchByCodeResponse.data.total_hits, 50)
    }


    private fun filterAndSave(
        searchByCodeResponse: SearchByCodeResponse, requestId: String
    ) {
        if (searchByCodeResponse.success == 1) {
            searchByCodeResponse.data.list.forEach { item ->
                val year = DateUtil.parseDate(item.notice_date).year()
                val currentYear = DateUtil.tomorrow().year()
                if (year < currentYear) {
                    throw RuntimeException("0")
                }
                saveOriginalData(item, requestId)
                if (summaryFilterChain.doFilter(item)) {
                    saveOrUpdateNotice(item)
                }
            }
        }
    }

    private fun saveOrUpdateNotice(noticeItem: Item) {
        val codes = noticeItem.codes.first()
        val notice = Notice(
            stock = codes.stock_code,
            code = noticeItem.art_code,
            columnCode = noticeItem.columns.first().column_code,
            title = noticeItem.title,
            date = noticeItem.notice_date.substring(0, 10),
            securityFullName = codes.short_name,
        )
        noticeRepository.save(
            notice
        )
    }

    private fun saveOriginalData(item: Item, requestId: String) {
        val source = SearchByCodeSource(
            code = item.art_code,
            columnCode = item.columns.first().column_code,
            columnName = item.columns.first().column_name,
            title = item.title,
            date = item.notice_date.substring(0, 10),
            requestId = requestId
        )
        searchByCodeSourceRepository.save(source)
    }

}