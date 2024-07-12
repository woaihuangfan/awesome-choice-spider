package com.fan.response

data class WebNoticeDetailResponse(
    val data: DetailData?,
    val success: Int?,
)

data class DetailData(
    val art_code: String,
    val attach_list: List<AttachItem>,
    val attach_list_ch: List<Any>,
    val attach_list_en: List<Any>,
    val attach_size: String,
    val attach_type: String,
    val attach_url: String,
    val attach_url_web: String,
    val eitime: String,
    val extend: Map<String, Any>, // 假设 extend 字段可能是任意类型的值
    val is_rich: Int,
    val is_rich2: Int,
    val language: String,
    val notice_content: String,
    val notice_date: String,
    val notice_title: String,
    val page_size: Int,
    val page_size_ch: Int,
    val page_size_cht: Int,
    val page_size_en: Int,
    val security: List<Security>,
    val short_name: String,
)

data class AttachItem(
    val attach_size: Int,
    val attach_type: String,
    val attach_url: String,
    val seq: Int,
)

data class Security(
    val market_uni: String,
    val short_name: String,
    val short_name_ch: String,
    val short_name_cht: String,
    val short_name_en: String,
    val stock: String,
)
