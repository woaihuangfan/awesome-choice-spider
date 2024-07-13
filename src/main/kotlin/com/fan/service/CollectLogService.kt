package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.db.entity.CollectLog
import com.fan.db.repository.CollectLogRepository
import com.fan.enums.SearchType
import com.fan.po.DataCollectParam
import com.fan.service.RequestContext.Key.getRequestId
import org.springframework.stereotype.Service

@Service
class CollectLogService(
    private val collectLogRepository: CollectLogRepository,
    private val titleRulesService: TitleRulesService,
) {

    fun saveCollectLog(
        param: DataCollectParam,
        type: SearchType,
        collectedCount: Int,
        context: RequestContext,
    ) {
        val titleFilterRuleNames = titleRulesService.getCurrentRules()
        collectLogRepository.save(
            CollectLog(
                tillDate = param.tillDate,
                date = DateUtil.now(),
                type = type.typeName,
                count = collectedCount,
                keyword = param.keyword,
                titleFilteredWords = titleFilterRuleNames,
                requestId = getRequestId(context),
            ),
        )
    }
}
