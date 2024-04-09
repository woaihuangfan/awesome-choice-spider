package com.fan.controller

import com.fan.db.entity.Notice
import com.fan.db.entity.NoticeDetailFetchFailedLog
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.NoticeDetailFetchFailedLogRepository
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.ResultRepository
import com.fan.db.repository.SourceRepository
import com.fan.dto.PageResult
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/query")
class QueryController(
    private val noticeRepository: NoticeRepository,
    private val resultRepository: ResultRepository,
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository,
    private val sourceRepository: SourceRepository,
    private val noticeDetailFetchFailedLogRepository: NoticeDetailFetchFailedLogRepository
) {

    @GetMapping(value = ["/sources"])
    fun source(): Int {
        val all = sourceRepository.findAll()
        return all.map { it.code }.toSet().size
    }

    @GetMapping(value = ["/notice"])
    fun query(): List<Notice> {
        return noticeRepository.findByStatus("Done")
    }

    @GetMapping(value = ["/notices"])
    fun all(): List<Notice> {
        return noticeRepository.findAll()
    }

    @GetMapping(value = ["/result"])
    fun result(
        @RequestParam page: Int,
        @RequestParam limit: Int,
    ): PageResult {
        val pageable: PageRequest = PageRequest.of(page - 1, limit)
        return PageResult.success(resultRepository.findAll(pageable))
    }

    @GetMapping(value = ["/errors"])
    fun errors(
        @RequestParam page: Int,
        @RequestParam limit: Int,
    ): PageResult {
        val pageable: PageRequest = PageRequest.of(page - 1, limit)
        return PageResult.success(noticeDetailFailLogRepository.findAll(pageable))
    }

    @GetMapping(value = ["/fetchErrors"])
    fun fetchErrors(): MutableList<NoticeDetailFetchFailedLog> {
        return noticeDetailFetchFailedLogRepository.findAll()
    }

    @GetMapping(value = ["/fetchErrors/count"])
    fun countFetchErrors(): Int {
        val all = noticeDetailFetchFailedLogRepository.findAll()
        return all.map { it.code }.toSet().size
    }
}
