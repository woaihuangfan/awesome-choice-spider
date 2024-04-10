package com.fan.client

import cn.hutool.core.date.DateUtil
import cn.hutool.core.thread.ThreadUtil
import cn.hutool.http.HtmlUtil
import cn.hutool.http.HttpRequest
import com.fan.po.NoticeDetailPO
import com.fan.response.WebNoticeDetailResponse
import com.google.gson.Gson
import java.util.stream.Collectors

object NoticeDetailClient {

    private const val DUMMY_CALLBACK = "dummy"
    private const val DETAIL_URL = "http://pdf.dfcfw.com/pdf/DH2_%sv2_1.txt"
    private const val WEB_DETAIL_URL =
        "https://np-cnotice-stock.eastmoney.com/api/content/ann?cb=$DUMMY_CALLBACK&art_code=%s&client_source=web&page_index=%s&_=%s"


    fun fetchDetail(infoCode: String): String {
        val url = DETAIL_URL.format(infoCode)
        val response = HttpRequest.get(url).execute()
        if (response.isOk) {
            val body = response.body()
            try {
                val map = Gson().fromJson(body, Map::class.java)
                val list = map.values.map { it.toString() }

                val content = list.reversed().stream().collect(Collectors.joining())
                val formattedContent = formatHtml2Text(content)
                val unescapedContent = HtmlUtil.unescape(formattedContent)
                val cleanContent = HtmlUtil.cleanHtmlTag(unescapedContent)
                return cleanContent
            } catch (e: Exception) {
                println("====无法解析响应====")
                println(url)
                println("====infoCode====")
                println(infoCode)
                return ""
            }
        }
        println("====请求失败====")
        println("====infoCode====")
        println(infoCode)
        println(response.status)
        return ""


    }

    fun fetchWebDetailSearchedByCode(code: String): NoticeDetailPO? {

        return null
    }

    fun fetchWebDetailSearchedByKeyWord(infoCode: String): NoticeDetailPO? {
        ThreadUtil.sleep(300)
        val index = 1
        val url = WEB_DETAIL_URL.format(infoCode, index, DateUtil.current())
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
                    stock = detail.data?.security?.first()?.stock.orEmpty()
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
        var text = html
//        if (text.isNotEmpty()) {
//            text = text.replace("<br>", "\r\n")
//            text = text.replace("<p>", "\r\n")
//            text = text.replace("&nbsp;", " ")
//        }
        return text
    }

}