package com.fan.filter

import com.fan.response.NoticeItem

class TitleFilter : SummaryFilter {

    override fun doFilter(content: NoticeItem): Boolean {
        return content.title.contains("2024") && content.title.contains("会计")
    }
}