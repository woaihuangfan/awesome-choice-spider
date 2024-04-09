package com.fan.controller

import com.fan.db.entity.Notice
import com.fan.db.entity.NoticeDetailFailLog
import com.fan.db.entity.NoticeDetailFetchFailedLog
import com.fan.db.entity.Result
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.NoticeDetailFetchFailedLogRepository
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.ResultRepository
import com.fan.db.repository.SourceRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
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
    fun result(): MutableList<Result> {
        return resultRepository.findAll()
    }

    @GetMapping(value = ["/errors"])
    fun errors(): MutableList<NoticeDetailFailLog> {
        return noticeDetailFailLogRepository.findAll()
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
