package com.fan.client

import cn.hutool.http.HttpRequest
import com.fan.response.SearchByCodeResponse
import com.google.gson.Gson

object SearchClient {
    private const val DUMMY_CALLBACK = "dummy"

    private const val SEARCH_BY_CODE_URL =
        "https://np-anotice-stock.eastmoney.com/api/security/ann?cb=%s&sr=-1&page_size=%s&page_index=%s&ann_type=A&client_source=web&stock_list=%s&f_node=0&s_node="

    fun searchByCode(code: String, index: Int, rows: Int): SearchByCodeResponse {
        val url = SEARCH_BY_CODE_URL.format(DUMMY_CALLBACK, rows, index, code)
        val response = HttpRequest.get(url).execute()

        if (response.isOk) {
            val body = response.body()?.replace("$DUMMY_CALLBACK(", "")?.replace(")", "")
            return Gson().fromJson(body, SearchByCodeResponse::class.java)
                ?: throw IllegalStateException("Failed to parse JSON response")
        } else {
            error("HTTP request failed with status: ${response.status}")
        }
    }
}
