package com.fan.controller

import cn.hutool.core.collection.CollUtil
import cn.hutool.poi.excel.ExcelUtil
import com.fan.db.repository.ResultRepository
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/excel")
class ExcelController(
    private val resultRepository: ResultRepository
) {

    @GetMapping(value = ["/result"])
    fun start(httpServletResponse: HttpServletResponse) {

        val results = resultRepository.findAll()

        val headers: List<String> =
            CollUtil.newArrayList(
                "序号",
                "公司名称",
                "证券代码",
                "公告日期",
                "会计师事务所名称",
                "公告代码",
                "合同金额",
                "年度"
            )

        val contents: List<List<String>> = results.map {
            listOfNotNull(it.name, it.stock, it.date, it.accountCompanyName, it.code, it.amount, it.year)
        }

        val rows: List<List<String>> = listOf(headers, *contents.toTypedArray())

        val writer = ExcelUtil.getWriter(true)
        writer.write(rows, true)
        httpServletResponse.characterEncoding = "UTF-8";
        httpServletResponse.contentType =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
        httpServletResponse.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition
                .attachment().filename("report-(${results.first().year}).xlsx").build().toString()
        )
        httpServletResponse.outputStream.use {
            writer.flush(it)
            writer.close()
        }

    }
}