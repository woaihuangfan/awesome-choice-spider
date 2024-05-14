package com.fan.service

import com.fan.db.entity.TitleFilterRule
import com.fan.db.repository.TitleFilterRuleRepository
import com.fan.po.Type
import org.springframework.stereotype.Service

@Service
class TitleRulesService(
    private val titleFilterRuleRepository: TitleFilterRuleRepository,
) {

    fun getCurrentRules(): String {
        val currentRules = titleFilterRuleRepository.findAll()
        val includedKeywords = getWordsByType(currentRules, Type.INCLUDE.typeName)
        val excludedKeywords = getWordsByType(currentRules, Type.EXCLUDE.typeName)
        return "incl.$includedKeywords excl.$excludedKeywords"
    }

    private fun getWordsByType(currentRules: List<TitleFilterRule>, type: String) =
        if (currentRules.isNotEmpty()) currentRules.filter { it.type.lowercase() == type }
            .joinToString(" ") { it.keyword } else ""
}
