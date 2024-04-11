package com.fan.filter

import com.fan.po.NoticeDetailPO

class ContentFilter : DetailFilter {

    override fun doFilter(detail: NoticeDetailPO): Boolean {
//        return detail.content.contains("事务所") && detail.content.contains("聘") && detail.content.contains("会计") && detail.content.contains(
//            "2024"
//        )
        return true
    }
}