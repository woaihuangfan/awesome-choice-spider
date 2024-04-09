package com.fan.filter

import com.fan.response.NoticeItem

fun interface SummaryFilter {
    fun doFilter(content: NoticeItem): Boolean
}