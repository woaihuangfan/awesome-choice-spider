package com.fan.filter

import com.fan.po.NoticeDetailPO
import org.springframework.stereotype.Component

@Component
class DetailFilterChain {
    private val detailFilters: MutableList<DetailFilter> = mutableListOf()

    init {
        detailFilters.add(ContentFilter())
    }

    fun doFilter(detail: NoticeDetailPO?): Boolean {
        if (detail == null) return false
        for (filter in detailFilters) {
            if (!filter.doFilter(detail)) {
                return false
            }
        }
        return true
    }

}