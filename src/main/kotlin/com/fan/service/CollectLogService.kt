package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.db.entity.CollectLog
import com.fan.db.repository.CollectLogRepository
import com.fan.enums.SearchType
import com.fan.po.DataCollectParam
import com.fan.service.RequestContext.Key.getRequestId
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CollectLogService(
    private val collectLogRepository: CollectLogRepository,
    private val titleRulesService: TitleRulesService,
) {
    @Transactional
    fun saveCollectLog(
        param: DataCollectParam,
        type: SearchType,
        collectedCount: Int,
        context: RequestContext,
    ) {
        val titleFilterRules = titleRulesService.getCurrentRules()
        val titleFilterRuleNames =
            if (titleFilterRules.isNotEmpty()) titleFilterRules.joinToString("、") else ""
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
