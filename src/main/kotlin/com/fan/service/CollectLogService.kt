package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.db.entity.CollectLog
import com.fan.db.repository.CollectLogRepository
import com.fan.enums.SearchType
import com.fan.po.DataCollectParam
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CollectLogService(
    private val collectLogRepository: CollectLogRepository,
) {

    @Transactional
    fun saveSearchLog(param: DataCollectParam, type: SearchType, collectedCount: Int) {
        collectLogRepository.save(
            CollectLog(
                tillDate = param.tillDate,
                date = DateUtil.now(),
                type = type.typeName,
                count = collectedCount,
                keyword = param.keyword
            )
        )
    }
}