package com.fan.client

import cn.hutool.core.date.DateUtil
import cn.hutool.http.HtmlUtil
import cn.hutool.http.HttpRequest
import com.fan.po.NoticeDetailPO
import com.fan.response.WebNoticeDetailResponse
import com.google.gson.Gson

object NoticeDetailClient {

    private const val DUMMY_CALLBACK = "dummy"
    private const val DETAIL_PAGE_URL = "https://data.eastmoney.com/notices/detail/%s/%s.html"
    private const val WEB_DETAIL_URL =
        "https://np-cnotice-stock.eastmoney.com/api/content/ann?cb=%s&art_code=%s&client_source=web&page_index=%s&_=%s"

    /**
     * 获取详情页URL
     */
    fun getDetailPageUrl(stock: String, code: String): String =
        String.format(DETAIL_PAGE_URL, stock, code)

    /**
     * 获取远程详情信息
     */
    fun fetchDetailFromRemote(infoCode: String, stock: String): NoticeDetailPO? {
        val url = String.format(WEB_DETAIL_URL, DUMMY_CALLBACK, infoCode, 1, DateUtil.current())
        val response = HttpRequest.get(url).execute()

        if (response.isOk) {
            val body = response.body()?.replace("$DUMMY_CALLBACK(", "")?.replace(")", "") ?: return null
            return parseResponse(body, stock)
        }
        return null
    }

    /**
     * 解析响应内容
     */
    private fun parseResponse(body: String, stock: String): NoticeDetailPO? {
        return try {
            val detail = Gson().fromJson(body, WebNoticeDetailResponse::class.java)
            val noticeContent = detail.data?.notice_content.orEmpty()
            val formattedContent = formatHtml2Text(noticeContent)
            val cleanContent = HtmlUtil.cleanHtmlTag(HtmlUtil.unescape(formattedContent))
            NoticeDetailPO(
                code = detail.data?.art_code ?: "",
                title = detail.data?.notice_title ?: "<标题未找到>",
                content = cleanContent,
                stock = extractStock(detail, stock)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 提取股票代码
     */
    private fun extractStock(detail: WebNoticeDetailResponse, stock: String): String {
        return detail.data?.security?.first { it.stock == stock }?.stock
            ?: detail.data?.security?.first { it.stock.length == 6 }?.stock.orEmpty()
    }

    /**
     * 格式化HTML为纯文本
     */
    private fun formatHtml2Text(html: String): String {
        return html
    }
}
