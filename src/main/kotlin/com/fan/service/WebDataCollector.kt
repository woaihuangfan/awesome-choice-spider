package com.fan.service

import cn.hutool.core.thread.ThreadUtil
import cn.hutool.core.thread.ThreadUtil.sleep
import cn.hutool.core.util.PageUtil
import com.fan.client.SearchClient
import com.fan.db.entity.Notice
import com.fan.db.entity.Source
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.SourceRepository
import com.fan.filter.SummaryFilterChain
import com.fan.response.NoticeItem
import com.fan.response.WebNoticeResponseSearchResult
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

private const val ROWS = 20

@Component
class WebDataCollector(
    private val noticeRepository: NoticeRepository,
    private val summaryFilterChain: SummaryFilterChain,
    private val sourceRepository: SourceRepository
) : DataCollector {


    @Transactional
    override fun start(keyword: String) {
        ThreadUtil.execute {
            println("==========开始爬取==========")
            startSearchNotice(keyword)
            println("==========爬取结束==========")
        }
    }


    private fun startSearchNotice(keyword: String) {
//        val totalPage = detectTotalPages(keyword)
//        println("总页数：$totalPage")
        for (i in 1..100) {
            sleep(1000)
            println("==========开始爬取第 $i 页==========")
            try {
                val webNoticeResponseSearchResult = SearchClient.searchWeb(keyword, i, ROWS)
                filterAndSave(webNoticeResponseSearchResult)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun filterAndSave(
        webNoticeResponseSearchResult: WebNoticeResponseSearchResult
    ) {
        webNoticeResponseSearchResult.result.noticeWeb.forEach { notice ->
            saveOriginalData(notice)
            if (summaryFilterChain.doFilter(notice)) {
                saveOrUpdateNotice(notice)
            }
        }


    }

    private fun saveOrUpdateNotice(noticeItem: NoticeItem) {
        val notice = Notice(
            code = noticeItem.code,
            columnCode = noticeItem.columnCode,
            title = noticeItem.title,
            content = noticeItem.content,
            date = noticeItem.date,
            securityFullName = noticeItem.securityFullName,
            url = noticeItem.url
        )
        noticeRepository.save(
            notice
        )
    }

    private fun saveOriginalData(notice: NoticeItem) {
        val source = Source(
            code = notice.code,
            columnCode = notice.columnCode,
            title = notice.title,
            content = notice.content,
            date = notice.date,
            securityFullName = notice.securityFullName,
            url = notice.url
        )

        sourceRepository.save(source)
    }

    private fun detectTotalPages(keyword: String): Int {
        val pageNumber = 1
        val response = SearchClient.searchWeb(keyword, pageNumber, ROWS)
        val hitsTotal = response.hitsTotal
        val totalPages = PageUtil.totalPage(hitsTotal, ROWS)
        return totalPages
    }

}
