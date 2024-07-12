package com.fan.response

import com.fasterxml.jackson.annotation.JsonProperty

data class NoticeResponseSearchResult(
    val code: Int,
    val message: String,
    val data: Data,
)

data class NoticeResponse(
    @JsonProperty("code") val code: Int,
    @JsonProperty("message") val message: String,
    @JsonProperty("data") val data: Data,
)

data class Data(
    @JsonProperty("hasNext") val hasNext: Boolean,
    @JsonProperty("searchResultList") val searchResultList: List<SearchResult>,
)

data class SearchResult(
    @JsonProperty("infotype") val infotype: String?,
    @JsonProperty("portfoliocode") val portfoliocode: String,
    @JsonProperty("secuList") val seculist: List<Secu>,
    @JsonProperty("columncode") val columncode: String,
    @JsonProperty("secuVarietyCode") val secuvarietycode: String,
    @JsonProperty("title") val title: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("medianame") val medianame: String,
    @JsonProperty("typecode") val typecode: String,
    @JsonProperty("datetime") val dateTime: String,
    @JsonProperty("companycode") val companycode: String,
    @JsonProperty("secuTypeCode") val secutypecode: String,
    @JsonProperty("showtime") val showtime: String,
    @JsonProperty("sitime") val sitime: String,
    @JsonProperty("marketCode") val marketcode: String,
    @JsonProperty("textBlack") val textblack: String,
    @JsonProperty("attach") val attach: List<Attach>,
    @JsonProperty("text") val text: String,
    @JsonProperty("secuFullCode") val secufullcode: String,
    @JsonProperty("importLevel") val importlevel: String,
    @JsonProperty("eitime") val eitime: String,
    @JsonProperty("secuCode") val secucode: String,
    @JsonProperty("textForm") val textform: String?,
    @JsonProperty("titleBlack") val titleblack: String,
    @JsonProperty("highlightMap") val highlightmap: Any?,
    @JsonProperty("infocode") val infocode: String,
    @JsonProperty("eutime") val eutime: String,
    @JsonProperty("columnName") val columnname: String,
)

data class Secu(
    @JsonProperty("secuSName") val secusname: String,
    @JsonProperty("secuTypeCode") val secutypecode: String,
    @JsonProperty("secuVarietyCode") val secuvarietycode: String,
    @JsonProperty("secuFullCode") val secufullcode: String,
)

data class Attach(
    val filetype: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("infocode") val infocode: String,
    @JsonProperty("filesize") val filesize: Int,
    @JsonProperty("pagenum") val pagenum: Any?,
    @JsonProperty("type") val type: String,
    @JsonProperty("content") val content: String,
    @JsonProperty("key") val key: String,
    @JsonProperty("seq") val seq: Int,
    @JsonProperty("sourcePath") val sourcepath: String,
)
