package com.fan.controller

import com.fan.controller.WebSocketController.Companion.clearLog
import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.db.repository.AnalysisLogRepository
import com.fan.db.repository.CollectLogRepository
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.NoticeDetailFetchFailedLogRepository
import com.fan.db.repository.NoticeDetailRepository
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.NoticeSearchHistoryRepository
import com.fan.db.repository.ResultRepository
import com.fan.db.repository.SearchByCodeSourceRepository
import com.fan.db.repository.SourceRepository
import com.fan.db.repository.TitleFilterRuleRepository
import com.fan.service.SearchByCodeCollector
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/clear")
class ClearController(
    private val resultRepository: ResultRepository,
    private val analysisLogRepository: AnalysisLogRepository,
    private val collectLogRepository: CollectLogRepository,
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository,
    private val noticeDetailFetchFailedLogRepository: NoticeDetailFetchFailedLogRepository,
    private val noticeDetailRepository: NoticeDetailRepository,
    private val noticeRepository: NoticeRepository,
    private val noticeSearchHistoryRepository: NoticeSearchHistoryRepository,
    private val searchByCodeSourceRepository: SearchByCodeSourceRepository,
    private val sourceRepository: SourceRepository,
    private val titleFilterRuleRepository: TitleFilterRuleRepository,
    private val searchByCodeCollector: SearchByCodeCollector


) {

    @DeleteMapping
    fun start(httpServletResponse: HttpServletResponse): String {
        cancelAndClear()
        titleFilterRuleRepository.deleteAll()
        letPeopleKnow("全部清除成功!")
        return "清除成功!"

    }

    fun cancelAndClear() {
        searchByCodeCollector.cancel()
        while (!searchByCodeCollector.hasFinished()) {
            Thread.sleep(100)
        }
        clearLog()
        resultRepository.deleteAll()
        analysisLogRepository.deleteAll()
        collectLogRepository.deleteAll()
        noticeDetailFailLogRepository.deleteAll()
        noticeDetailFetchFailedLogRepository.deleteAll()
        noticeDetailRepository.deleteAll()
        noticeRepository.deleteAll()
        noticeSearchHistoryRepository.deleteAll()
        searchByCodeSourceRepository.deleteAll()
        sourceRepository.deleteAll()
    }


}