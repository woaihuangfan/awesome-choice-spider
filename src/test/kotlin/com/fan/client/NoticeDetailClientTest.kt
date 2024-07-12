package com.fan.client

import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import com.fan.Constant.NOTICE_CODE
import com.fan.Constant.STOCK
import com.fan.JsonFileReader
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class NoticeDetailClientTest {
    private val mockkHttpRequest = mockk<HttpRequest>()

    @BeforeEach
    fun setUp() {
        mockkStatic(HttpRequest::class)
        every { HttpRequest.get(any()) } returns mockkHttpRequest
    }

    @Test
    fun getDetailPageUrl() {
        val detailPageUrl = NoticeDetailClient.getDetailPageUrl(STOCK, NOTICE_CODE)
        assertEquals("https://data.eastmoney.com/notices/detail/000001/AN202403141626755358.html", detailPageUrl)
    }

    @Test
    fun fetchDetailFromRemote() {
        val response = mockk<HttpResponse>()
        val mockedDetail = JsonFileReader.readFileContentFromResources("stub/noticeDetail.json")

        every { mockkHttpRequest.execute() } returns response
        every { response.isOk } returns true
        every { response.body() } returns
            """
            dummy($mockedDetail)
            """.trimIndent()

        val noticeDetailPO = NoticeDetailClient.fetchDetailFromRemote(NOTICE_CODE, STOCK)

        assertThat(noticeDetailPO).isNotNull
        assertThat(noticeDetailPO?.code).isEqualTo(NOTICE_CODE)
        assertThat(noticeDetailPO?.stock).isEqualTo(STOCK)
        assertThat(noticeDetailPO?.title).isEqualTo("平安银行:拟续聘会计师事务所的公告")
        assertThat(noticeDetailPO?.content).contains("平安银行股份有限公司")
    }

    @Test
    fun shouldReturnNullIfResponseIsNotOk() {
        val response = mockk<HttpResponse>()

        every { mockkHttpRequest.execute() } returns response
        every { response.isOk } returns false

        val noticeDetailPO = NoticeDetailClient.fetchDetailFromRemote(NOTICE_CODE, STOCK)

        assertThat(noticeDetailPO).isNull()
    }

    @Test
    fun shouldReturnNullIfHasExceptionWhenParseResponse() {
        val response = mockk<HttpResponse>()
        val mockedDetail = JsonFileReader.readFileContentFromResources("stub/noticeDetail.json")

        every { mockkHttpRequest.execute() } returns response
        every { response.isOk } returns true
        every { response.body() } returns
            """
            dummy($mockedDetail)
            """.trimIndent()
        val noticeDetailPO = NoticeDetailClient.fetchDetailFromRemote("whatever", "whatever")
        assertThat(noticeDetailPO).isNull()
    }
}
