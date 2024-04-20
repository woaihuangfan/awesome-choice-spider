package com.fan.service

import cn.hutool.core.thread.ThreadUtil
import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.db.repository.CountByRequestIdRepository
import com.fan.enums.SearchType
import com.fan.po.DataCollectParam
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

abstract class AbstractDataCollector(
    private val countByRequestIdRepository: CountByRequestIdRepository,
    private val analysisLogService: AnalysisLogService,
    private val collectLogService: CollectLogService
) : DataCollector {

    protected val lock = AtomicInteger(0)

    sealed class Status(val status: String) {
        data object RUNNING : Status("running")
        data object READY : Status("ready")
    }

    override fun startCollect(param: DataCollectParam, type: SearchType) {
        ThreadUtil.execAsync {
            letPeopleKnow("==========开始爬取==========")
            lock.incrementAndGet()
            val requestId = UUID.randomUUID().toString()
            doCollect(param, type, requestId)

            letPeopleKnow("==========爬取结束==========")
            log(requestId, param, type)
            lock.decrementAndGet()
        }

    }

    private fun log(requestId: String, param: DataCollectParam, type: SearchType) {
        val collectedCount = countByRequestIdRepository.countByRequestId(requestId)
        collectLogService.saveSearchLog(param, type, collectedCount)
        analysisLogService.saveAnalysisLog("采集数据并分析", requestId)
    }


    protected abstract fun doCollect(param: DataCollectParam, type: SearchType, requestId: String)
}