package com.fan.filter

import com.fan.response.Item
import com.fan.response.NoticeItem

interface SummaryFilter {
    fun doFilter(content: NoticeItem): Boolean {
        return false
    }

    fun doFilter(content: Item): Boolean {
        return false
    }
}