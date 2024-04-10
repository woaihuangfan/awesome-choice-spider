package com.fan.filter

import com.fan.db.entity.TitleFilterRule
import com.fan.db.repository.TitleFilterRuleRepository
import com.fan.response.Item
import com.fan.response.NoticeItem
import org.springframework.stereotype.Component

@Component
class TitleFilter(
    private val titleFilterRuleRepository: TitleFilterRuleRepository
) : SearchFilter {


    override fun doFilter(noticeItem: NoticeItem): Boolean {
        return noticeItem.title.contains("2024") && noticeItem.title.contains("会计")
    }

    override fun doFilter(item: Item): Boolean {
        getKeywords().map { it.keyword }.forEach {
            if (!item.title.contains(it)) {
                return false
            }
        }
        return true
    }

    private fun getKeywords(): MutableList<TitleFilterRule> = titleFilterRuleRepository.findAll()

    fun doFilter(title: String): Boolean {
        getKeywords().forEach {
            if (!title.contains(it.keyword)) {
                return false
            }
        }
        return true
    }


}