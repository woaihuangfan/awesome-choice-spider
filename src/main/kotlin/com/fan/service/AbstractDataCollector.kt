package com.fan.service

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil
import com.fan.db.entity.SearchLog
import com.fan.db.repository.CountByRequestIdRepository
import com.fan.db.repository.SearchLogRepository
import com.fan.enums.SearchType
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

abstract class AbstractDataCollector(
    private val countByRequestIdRepository: CountByRequestIdRepository,
    private val searchLogRepository: SearchLogRepository
) : DataCollector {
    private val lock = AtomicInteger(0)
    override fun start(param: String, type: SearchType): String {
        if (lock.get() == 1) {
            return "正在采集中，请稍后"
        }
        ThreadUtil.execAsync {
            println("==========开始爬取==========")
            lock.incrementAndGet()
            val requestId = UUID.randomUUID().toString()
            doCollect(param, type, requestId)

            println("==========爬取结束==========")
            searchLogRepository.save(
                SearchLog(
                    param = param,
                    date = DateUtil.now(),
                    type = type.typeName,
                    count = countByRequestIdRepository.countByRequestId(requestId)
                )
            )
            lock.decrementAndGet()
        }

        return "success"
    }

    protected abstract fun doCollect(param: String, type: SearchType, requestId: String)
}