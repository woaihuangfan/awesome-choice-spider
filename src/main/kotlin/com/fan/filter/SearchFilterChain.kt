package com.fan.filter

import com.fan.response.Item
import com.fan.response.NoticeItem
import org.springframework.stereotype.Component

@Component
class SearchFilterChain(
    titleFilter: TitleFilter
) {
    private val searchFilters: MutableList<SearchFilter> = mutableListOf()

    init {
        searchFilters.add(titleFilter)
    }

    fun doFilter(content: Item): Boolean {
        for (filter in searchFilters) {
            if (!filter.doFilter(content)) {
                return false
            }
        }
        return true
    }
}