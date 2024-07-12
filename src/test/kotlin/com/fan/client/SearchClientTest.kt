package com.fan.client

import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse
import com.fan.Constant.NOTICE_CODE
import com.fan.Constant.STOCK
import com.fan.JsonFileReader
import com.google.gson.JsonSyntaxException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SearchClientTest {
    private val mockkHttpRequest = mockk<HttpRequest>()

    @BeforeEach
    fun setUp() {
        mockkStatic(HttpRequest::class)
        every { HttpRequest.get(any()) } returns mockkHttpRequest
    }

    @Test
    fun searchByCode() {
        val response = mockk<HttpResponse>()
        val noticeList = JsonFileReader.readFileContentFromResources("stub/noticeList.json")

        every { mockkHttpRequest.execute() } returns response
        every { response.isOk } returns true
        every { response.body() } returns
            """
            dummy($noticeList)
            """.trimIndent()

        val searchByCodeResponse = SearchClient.searchByCode(NOTICE_CODE, 1, 10)
        assertThat(searchByCodeResponse).isNotNull
        assertThat(searchByCodeResponse.data).isNotNull
        assertThat(searchByCodeResponse.data.list).isNotEmpty
        assertThat(searchByCodeResponse.data.list[0].title).isEqualTo("平安银行:监事会决议公告")
        assertThat(
            searchByCodeResponse.data.list[0]
                .codes[0]
                .stock_code,
        ).isEqualTo(STOCK)
        assertThat(
            searchByCodeResponse.data.list[0]
                .codes[0]
                .short_name,
        ).isEqualTo("平安银行")
        assertThat(
            searchByCodeResponse.data.list[0]
                .columns[0]
                .column_name,
        ).isEqualTo("监事会决议公告")
        assertThat(searchByCodeResponse.data.list[0].art_code).isEqualTo("AN202404191630666628")
    }

    @Test
    fun shouldThrowException() {
        val response = mockk<HttpResponse>()
        val noticeList = JsonFileReader.readFileContentFromResources("stub/noticeList.json")

        every { mockkHttpRequest.execute() } returns response
        every { response.isOk } returns true
        every { response.body() } returns
            """
            test($noticeList)
            """.trimIndent()
        assertThrows<JsonSyntaxException> { SearchClient.searchByCode(NOTICE_CODE, 1, 10) }
    }

    @Test
    fun shouldThrowExceptionWhenResponseIsNotOk() {
        val response = mockk<HttpResponse>()
        every { mockkHttpRequest.execute() } returns response
        every { response.isOk } returns false
        every { response.status } returns 500
        val exception = assertThrows<IllegalStateException> { SearchClient.searchByCode(NOTICE_CODE, 1, 10) }
        assertThat(exception.message).isEqualTo("HTTP request failed with status: 500")
    }

    @Test
    fun shouldThrowExceptionWhenResponseBodyIsNull() {
        val response = mockk<HttpResponse>()
        every { mockkHttpRequest.execute() } returns response
        every { response.isOk } returns true
        every { response.body() } returns null
        val exception = assertThrows<IllegalStateException> { SearchClient.searchByCode(NOTICE_CODE, 1, 10) }
        assertThat(exception.message).isEqualTo("Failed to parse JSON response")
    }
}
