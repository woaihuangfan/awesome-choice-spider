package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.db.entity.CollectLog
import com.fan.db.repository.CollectLogRepository
import com.fan.enums.SearchType
import org.springframework.stereotype.Service

@Service
class CollectLogService(
    private val collectLogRepository: CollectLogRepository,
) {

    fun saveSearchLog(param: String, type: SearchType, collectedCount: Int) {
        collectLogRepository.save(
            CollectLog(
                param = param, date = DateUtil.now(), type = type.typeName, count = collectedCount
            )
        )
    }
}