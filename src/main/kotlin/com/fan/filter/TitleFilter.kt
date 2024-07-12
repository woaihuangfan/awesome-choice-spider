package com.fan.filter

import com.fan.db.entity.TitleFilterRule
import com.fan.db.repository.TitleFilterRuleRepository
import com.fan.po.Type
import com.fan.response.Item
import org.springframework.stereotype.Component

@Component
class TitleFilter(
    private val titleFilterRuleRepository: TitleFilterRuleRepository,
) : SearchFilter {
    override fun doFilter(item: Item): Boolean {
        getIncludeKeywords().map { it.keyword }.forEach {
            if (!item.title.contains(it)) {
                return false
            }
        }

        getExcludeKeywords().map { it.keyword }.forEach {
            if (item.title.contains(it)) {
                return false
            }
        }
        return true
    }

    private fun getIncludeKeywords(): List<TitleFilterRule> = titleFilterRuleRepository.findAllByTypeIs(Type.INCLUDE.typeName)

    private fun getExcludeKeywords(): List<TitleFilterRule> = titleFilterRuleRepository.findAllByTypeIs(Type.EXCLUDE.typeName)

    fun doFilter(title: String): Boolean {
        getIncludeKeywords().forEach {
            if (!title.contains(it.keyword)) {
                return false
            }
        }
        return true
    }
}
