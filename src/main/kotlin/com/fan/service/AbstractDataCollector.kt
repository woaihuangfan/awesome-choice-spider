package com.fan.service

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil
import com.fan.db.entity.SearchLog
import com.fan.db.repository.CountByRequestIdRepository
import com.fan.db.repository.SearchLogRepository
import com.fan.enums.SearchType
import java.util.UUID

abstract class AbstractDataCollector(
    private val countByRequestIdRepository: CountByRequestIdRepository,
    private val searchLogRepository: SearchLogRepository
) :
    DataCollector {
    override fun start(param: String, type: SearchType) {
        println("==========开始爬取==========")
        val requestId = UUID.randomUUID().toString()
        ThreadUtil.execute {
            doCollect(param, type, requestId)
        }

        println("==========爬取结束==========")
        searchLogRepository.save(
            SearchLog(
                param = param,
                date = DateUtil.now(),
                type = type.typeName,
                count = countByRequestIdRepository.countByRequestId(requestId)
            )
        )

    }

    protected abstract fun doCollect(param: String, type: SearchType, requestId: String)
}