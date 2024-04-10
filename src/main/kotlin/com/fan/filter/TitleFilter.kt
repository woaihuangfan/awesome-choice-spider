package com.fan.filter

import com.fan.response.Item
import com.fan.response.NoticeItem

class TitleFilter : SearchFilter {

    override fun doFilter(noticeItem: NoticeItem): Boolean {
        return noticeItem.title.contains("2024") && noticeItem.title.contains("会计")
    }

    override fun doFilter(item: Item): Boolean {
        return item.title.contains("2024年度") && item.title.contains("会计师事务所")
    }
}