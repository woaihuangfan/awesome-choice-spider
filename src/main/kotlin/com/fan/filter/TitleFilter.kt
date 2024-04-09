package com.fan.filter

import com.fan.response.Item
import com.fan.response.NoticeItem

class TitleFilter : SummaryFilter {

    override fun doFilter(content: NoticeItem): Boolean {
        return content.title.contains("2024") && content.title.contains("会计")
    }

    override fun doFilter(item: Item): Boolean {
        return item.title.contains("2024") && item.title.contains("会计")
    }
}