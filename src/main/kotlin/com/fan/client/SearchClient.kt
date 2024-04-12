package com.fan.client

import cn.hutool.core.date.DateUtil
import cn.hutool.http.HttpRequest
import cn.hutool.json.JSONUtil
import com.fan.po.DataCollectParam
import com.fan.response.Data
import com.fan.response.NoticeResponseSearchResult
import com.fan.response.SearchByCodeResponse
import com.fan.response.WebNoticeResponseSearchResult
import com.google.gson.Gson
import java.net.URLEncoder

object SearchClient {
    private const val DUMMY_CALLBACK = "dummy"
    private const val URL =
        "https://choicegw.eastmoney.com/app/report/web/app/search/notice?keyWord=%s&startIndex=%d&rows=%d"

    private const val WEB_URL = "https://search-api-web.eastmoney.com/search/jsonp?cb=%s&param=%s&_=%s"

    private const val SEARCH_BY_CODE_URL =
        "https://np-anotice-stock.eastmoney.com/api/security/ann?cb=$DUMMY_CALLBACK&sr=-1&page_size=%s&page_index=%s&ann_type=A&client_source=web&stock_list=%s&f_node=0&s_node="


    private val headers: Map<String, String>
        get() {
            val headers = """
            Host: choicegw.eastmoney.com
            Connection: keep-alive
            Accept: */*
            User-Agent: app-iphone-client-iPhone14,5-BBD1331F-3385-4890-902B-CC87D317E20D
            Accept-Language: zh-CN,zh-Hans;q=0.9
            appid: UHc7QTQqgQe0JtUsK7cdWaBIrRJYmmsJ@MOBILEAPP
            Accept-Encoding: gzip
        """.trimIndent().lineSequence().map {
                val (key, value) = it.split(": ", limit = 2)
                key to value
            }.toMap()
            return headers
        }

    fun search(keyword: String, index: Int, rows: Int): Data {
        val body = HttpRequest.get(URL.format(keyword, index, rows)).addHeaders(headers).execute().body()
        val (_, _, data) = Gson().fromJson(body, NoticeResponseSearchResult::class.java)
        return data
    }

    fun searchByCode(code: String, index: Int, rows: Int): SearchByCodeResponse {
        try {
            val body = HttpRequest.get(SEARCH_BY_CODE_URL.format(rows, index, code)).execute().body()
            val result = body.replace("$DUMMY_CALLBACK(", "").replace(")", "")
            return Gson().fromJson(result, SearchByCodeResponse::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun searchWeb(param: DataCollectParam, index: Int, rows: Int): WebNoticeResponseSearchResult {
        val body =
            HttpRequest.get(
                WEB_URL.format(
                    "dummy",
                    buildParams(param.keyword, rows, index),
                    DateUtil.current()
                )
            ).execute()
                .body()
        val result = body.replace("$DUMMY_CALLBACK(", "").replace(")", "")
        return Gson().fromJson(result, WebNoticeResponseSearchResult::class.java)
    }

    private fun buildParams(keyword: String, rows: Int, index: Int): String? {
        val params = """{
                "uid":"",
                "keyword":$keyword,
                "type":["noticeWeb"],
                "client":"web",
                "clientVersion":"curr",
                "clientType":"web",
                "param": {
                    "noticeWeb":{
                        "preTag":"<em class=\"red\">",
                        "postTag":"</em>",
                        "pageSize":$rows,
                        "pageIndex":$index
                    }
                }
                }
            """.trimIndent()
        val json = JSONUtil.parse(params)
        return URLEncoder.encode(json.toString(), "UTF-8")
    }


}