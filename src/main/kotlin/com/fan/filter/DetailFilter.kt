package com.fan.filter

import com.fan.po.NoticeDetailPO

fun interface DetailFilter {
    fun doFilter(detail: NoticeDetailPO): Boolean
}
