package com.fan.filter

import com.fan.response.Item
import com.fan.response.NoticeItem
import org.springframework.stereotype.Component

@Component
class SummaryFilterChain {
    private val summaryFilters: MutableList<SummaryFilter> = mutableListOf()

    init {
        summaryFilters.add(TitleFilter())
    }

    fun doFilter(content: NoticeItem): Boolean {
        for (filter in summaryFilters) {
            if (!filter.doFilter(content)) {
                return false
            }
        }
        return true
    }

    fun doFilter(content: Item): Boolean {
        for (filter in summaryFilters) {
            if (!filter.doFilter(content)) {
                return false
            }
        }
        return true
    }
}