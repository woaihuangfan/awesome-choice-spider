package com.fan.filter

import com.fan.response.Item
import com.fan.response.NoticeItem

interface SearchFilter {
    fun doFilter(noticeItem: NoticeItem): Boolean {
        return false
    }

    fun doFilter(item: Item): Boolean {
        return false
    }
}