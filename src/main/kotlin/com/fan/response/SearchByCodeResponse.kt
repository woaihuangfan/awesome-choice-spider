package com.fan.response

data class SearchByCodeResponse(
    val data: SearchByCodeData,
    val error: String,
    val success: Int,
)

data class SearchByCodeData(
    val list: List<Item>,
    val page_index: Int,
    val page_size: Int,
    val total_hits: Int,
)

data class Item(
    val art_code: String,
    val codes: List<Code>,
    val columns: List<Column>,
    val display_time: String,
    val eiTime: String,
    val language: String,
    val listing_state: String,
    val notice_date: String,
    val product_code: String,
    val sort_date: String,
    val source_type: String,
    val title: String,
    val title_ch: String,
    val title_en: String,
)

data class Code(
    val ann_type: String,
    val inner_code: String,
    val market_code: String,
    val short_name: String,
    val stock_code: String,
)

data class Column(
    val column_code: String,
    val column_name: String,
)
