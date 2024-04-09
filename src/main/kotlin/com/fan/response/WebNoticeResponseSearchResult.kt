package com.fan.response

data class WebNoticeResponseSearchResult(
    val bizCode: String,
    val bizMsg: String,
    val code: Int,
    val extra: Map<String, Any>, // 假设 extra 字段可能是任意类型的值
    val hitsTotal: Int,
    val msg: String,
    val result: Result,
    val searchId: String
)

data class Result(
    val noticeWeb: List<NoticeItem>
)

data class NoticeItem(
    val code: String,
    val columnCode: String,
    val title: String,
    val content: String,
    val date: String,
    val securityFullName: String,
    val url: String
)
