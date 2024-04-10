package com.fan.service

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil.sleep
import cn.hutool.core.util.PageUtil
import com.fan.client.SearchClient
import com.fan.db.entity.Notice
import com.fan.db.entity.NoticeSearchHistory
import com.fan.db.entity.SearchByCodeSource
import com.fan.db.repository.CodeRepository
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.NoticeSearchHistoryRepository
import com.fan.db.repository.SearchByCodeSourceRepository
import com.fan.db.repository.SearchLogRepository
import com.fan.enums.SearchType
import com.fan.filter.SearchFilterChain
import com.fan.response.Item
import com.fan.response.SearchByCodeResponse
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

private const val ROWS = 50

@Component
class SearchByCodeCollector(
    private val noticeRepository: NoticeRepository,
    private val searchFilterChain: SearchFilterChain,
    private val searchByCodeSourceRepository: SearchByCodeSourceRepository,
    private val codeRepository: CodeRepository,
    private val noticeSearchHistoryRepository: NoticeSearchHistoryRepository,
    searchLogRepository: SearchLogRepository

) : AbstractDataCollector(searchByCodeSourceRepository, searchLogRepository) {


    @Transactional
    override fun doCollect(param: String, type: SearchType, requestId: String) {
        val codeEntities = codeRepository.findAll()
        for (entity in codeEntities) {
            sleep(100)
            val stock = entity.stock
            val year = DateUtil.thisYear().toString()
            val noticeSearchHistory = noticeSearchHistoryRepository.findByStockAndYear(stock, year)
            val isLatest = noticeSearchHistory?.let {
                val today = DateUtil.parseDate(DateUtil.today()).dayOfYear()
                val lastUpdatedDate = DateUtil.parseDate(noticeSearchHistory.lastUpdatedDate).dayOfYear()
                if (lastUpdatedDate >= today) {
                    println("==========证券代码为【${stock}】的公司的当年度( $year )的公告数据已存在，共${it.count}条记录，将跳过==========")
                    return@let true
                }
                return@let false
            } ?: false

            if (isLatest) {
                continue
            }
            println("==========开始爬取证券代码为【${stock}】的公司的当年度的公告==========")
            val totalPages = getTotalPages(stock)
            for (i in 1..totalPages) {
                sleep(100)
                println("==========开始爬取第 $i 页, 共【$totalPages】页==========")
                try {
                    val searchByCodeResponse = SearchClient.searchByCode(stock, i, ROWS)
                    filterAndSave(searchByCodeResponse, requestId, stock)
                } catch (e: Exception) {

                    if (e.message == "0") {

                        val count = searchByCodeSourceRepository.countByStockAndYear(stock, year)
                        noticeSearchHistoryRepository.save(
                            NoticeSearchHistory(
                                stock = stock,
                                year = year,
                                count = count,
                                lastUpdatedDate = DateUtil.now()
                            )
                        )
                        println("==========证券代码为【${stock}】的公司 ${year}年度公告爬取完成(共${count}条记录)，将停止爬取==========")
                        break
                    } else {
                        e.printStackTrace()
                    }
                }
            }
        }

    }

    private fun getTotalPages(code: String): Int {
        val searchByCodeResponse = SearchClient.searchByCode(code, 1, 1)
        val totalHits = searchByCodeResponse.data.total_hits
        println("==========证券代码为【${code}】的公司查询到的公告总数为:$totalHits==========")
        return PageUtil.totalPage(totalHits, 50)
    }


    private fun filterAndSave(
        searchByCodeResponse: SearchByCodeResponse, requestId: String, stock: String
    ) {
        if (searchByCodeResponse.success == 1) {
            searchByCodeResponse.data.list.forEach { item ->
                val year = DateUtil.parseDate(item.notice_date).year()
                val currentYear = DateUtil.thisYear()
                if (year < currentYear) {
                    throw RuntimeException("0")
                }

                updateCompanyName(item, stock)
                saveOriginalData(item, requestId)
                if (searchFilterChain.doFilter(item)) {
                    saveOrUpdateNotice(item)
                }
            }
        }
    }

    private fun updateCompanyName(item: Item, stock: String) {
        val companyName = item.codes.first().short_name
        val company = codeRepository.findByStock(stock)
        company?.let {
            if (StringUtils.hasText(company.companyName) && company.companyName != companyName) {
                println("==========证券代码为【${stock}】的公司名称发生变化，原公司名称为【${company.companyName}】，新公司名称为【${companyName}】==========")
            }
            it.companyName = companyName
            codeRepository.save(it)
        }
    }

    private fun saveOrUpdateNotice(noticeItem: Item) {
        val codes = noticeItem.codes.first()
        val stock = codes.stock_code
        val code = noticeItem.art_code
        noticeRepository.findByStockAndCode(stock, code)?.let {
            return
        }
        val columnCode = if (noticeItem.columns.isNotEmpty()) noticeItem.columns.first().column_code else ""
        val notice = Notice(
            stock = stock,
            code = code,
            columnCode = columnCode,
            title = noticeItem.title,
            date = noticeItem.notice_date.substring(0, 10),
            securityFullName = codes.short_name,
            source = SearchType.CODE.typeName
        )
        noticeRepository.save(
            notice
        )
    }

    private fun saveOriginalData(item: Item, requestId: String) {

        val code = item.art_code
        val title = item.title
        val date = item.notice_date.substring(0, 10)
        searchByCodeSourceRepository.findByStockAndTitleAndDate(code, title, date)?.let {
            return
        }
        val column = if (item.columns.isNotEmpty()) item.columns.first() else null
        val source = SearchByCodeSource(
            stock = item.codes.first().stock_code,
            code = code,
            columnCode = column?.column_code ?: "",
            columnName = column?.column_name ?: "",
            title = title,
            date = date,
            requestId = requestId,
            year = DateUtil.thisYear().toString()
        )
        searchByCodeSourceRepository.save(source)
    }

}