package com.fan.service

import cn.hutool.core.thread.ThreadUtil
import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.db.repository.CountByRequestIdRepository
import com.fan.enums.SearchType
import com.fan.po.DataCollectParam
import com.fan.service.RequestContext.Key.getRequestId
import java.util.concurrent.atomic.AtomicInteger

abstract class AbstractDataCollector(
    private val countByRequestIdRepository: CountByRequestIdRepository,
    private val analysisLogService: AnalysisLogService,
    private val collectLogService: CollectLogService,
) : DataCollector {
    protected val lock = AtomicInteger(0)

    sealed class Status(
        val status: String,
    ) {
        data object RUNNING : Status("running")

        data object READY : Status("ready")
    }

    override fun startCollect(
        param: DataCollectParam,
        type: SearchType,
        context: RequestContext,
    ) {
        ThreadUtil.execAsync {
            letPeopleKnow("==========开始爬取==========")
            lock.incrementAndGet()
            doCollect(param, type, context)

            letPeopleKnow("==========爬取结束==========")
            log(param, type, context)
            lock.decrementAndGet()
        }
    }

    private fun log(
        param: DataCollectParam,
        type: SearchType,
        context: RequestContext,
    ) {
        val requestId = getRequestId(context)
        val collectedCount = countByRequestIdRepository.countByRequestId(requestId)
        collectLogService.saveCollectLog(param, type, collectedCount, context)
        analysisLogService.saveAnalysisLog("采集数据并分析", context)
    }

    protected abstract fun doCollect(
        param: DataCollectParam,
        type: SearchType,
        context: RequestContext,
    )
}
