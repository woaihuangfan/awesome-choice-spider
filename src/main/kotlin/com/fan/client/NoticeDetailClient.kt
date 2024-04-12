package com.fan.client

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil
import cn.hutool.http.HtmlUtil
import cn.hutool.http.HttpRequest
import com.fan.po.NoticeDetailPO
import com.fan.response.WebNoticeDetailResponse
import com.google.gson.Gson

object NoticeDetailClient {

    private const val DUMMY_CALLBACK = "dummy"
    private const val DETAIL_PAGE_URL = "https://data.eastmoney.com/notices/detail/%s/%s.html"
    private const val WEB_DETAIL_URL =
        "https://np-cnotice-stock.eastmoney.com/api/content/ann?cb=$DUMMY_CALLBACK&art_code=%s&client_source=web&page_index=%s&_=%s"

    private fun getDetailUrl(code: String, timestamp: Long, index: Int? = 1): String {
        return WEB_DETAIL_URL.format(code, index, timestamp)
    }

    fun getDetailPageUrl(stock: String, code: String): String {
        return DETAIL_PAGE_URL.format(stock, code)
    }


    fun fetchDetailFromRemote(infoCode: String): NoticeDetailPO? {
        ThreadUtil.sleep(300)
        val index = 1
        val url = getDetailUrl(infoCode, DateUtil.current(), index)
        val response = HttpRequest.get(url).execute()
        if (response.isOk) {
            var body = response.body()
            body = body.replace("dummy(", "").replace(")", "")
            try {
                val detail = Gson().fromJson(body, WebNoticeDetailResponse::class.java)
                val noticeContent = detail.data?.notice_content ?: ""
                val formattedContent = formatHtml2Text(noticeContent)
                val unescapedContent = HtmlUtil.unescape(formattedContent)
                val cleanContent = HtmlUtil.cleanHtmlTag(unescapedContent)
                return NoticeDetailPO(
                    code = infoCode,
                    title = detail.data?.notice_title ?: "<标题未找到>",
                    content = cleanContent,
                    stock = detail.data?.security?.first { it.stock.length == 6 }?.stock.orEmpty(),
                )
            } catch (e: Exception) {
                println("====无法解析响应====")
                println(url)
                println("====infoCode====")
                println(infoCode)
                return null
            }
        }
        println("====请求失败====")
        println("====infoCode====")
        println(infoCode)
        println(response.status)
        return null


    }


    private fun formatHtml2Text(html: String): String {
        return html
    }

}