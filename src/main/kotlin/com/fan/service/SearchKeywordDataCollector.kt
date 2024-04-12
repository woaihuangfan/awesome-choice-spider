package com.fan.service

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil.sleep
import com.fan.client.SearchClient
import com.fan.db.entity.Notice
import com.fan.db.entity.Source
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.SourceRepository
import com.fan.enums.SearchType
import com.fan.filter.SearchFilterChain
import com.fan.po.DataCollectParam
import com.fan.response.NoticeItem
import com.fan.response.WebNoticeResponseSearchResult
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

private const val ROWS = 20

@Component
class SearchKeywordDataCollector(
    private val noticeRepository: NoticeRepository,
    private val searchFilterChain: SearchFilterChain,
    private val sourceRepository: SourceRepository,
    analysisLogService: AnalysisLogService,
    collectLogService: CollectLogService,
) : AbstractDataCollector(sourceRepository, analysisLogService, collectLogService) {


    @Transactional
    override fun doCollect(param: DataCollectParam, type: SearchType, requestId: String) {
        for (i in 1..100) {
            sleep(100)
            println("==========开始爬取第 $i 页==========")
            try {
                val webNoticeResponseSearchResult = SearchClient.searchWeb(param, i, ROWS)
                filterAndSave(webNoticeResponseSearchResult, requestId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun filterAndSave(
        webNoticeResponseSearchResult: WebNoticeResponseSearchResult,
        requestId: String
    ) {
        webNoticeResponseSearchResult.result.noticeWeb.forEach { notice ->
            saveOriginalData(notice, requestId)
            if (searchFilterChain.doFilter(notice)) {
                saveNotice(notice, requestId)
            }
        }


    }

    private fun saveNotice(noticeItem: NoticeItem, requestId: String) {
        val notice = Notice(
            stock = "",
            code = noticeItem.code,
            columnCode = noticeItem.columnCode,
            title = noticeItem.title,
            date = noticeItem.date.substring(0, 10),
            securityFullName = noticeItem.securityFullName,
            source = SearchType.KEYWORD.typeName,
            requestId = requestId,
            year = DateUtil.parseDate(noticeItem.date).year().toString()
        )
        noticeRepository.save(
            notice
        )
    }

    private fun saveOriginalData(notice: NoticeItem, requestId: String) {
        val source = Source(
            code = notice.code,
            columnCode = notice.columnCode,
            title = notice.title,
            content = notice.content,
            date = notice.date,
            securityFullName = notice.securityFullName,
            url = notice.url,
            requestId = requestId
        )

        sourceRepository.save(source)
    }

}
