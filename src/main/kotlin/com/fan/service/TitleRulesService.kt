package com.fan.service

import com.fan.db.repository.TitleFilterRuleRepository
import org.springframework.stereotype.Service

@Service
class TitleRulesService(
    private val titleFilterRuleRepository: TitleFilterRuleRepository
) {

    fun getCurrentRules(): List<String> {
        return titleFilterRuleRepository.findAll().map { it.keyword }
    }
}