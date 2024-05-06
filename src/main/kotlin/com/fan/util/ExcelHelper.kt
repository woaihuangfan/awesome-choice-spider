package com.fan.util

import cn.hutool.poi.excel.ExcelUtil
import cn.hutool.poi.excel.ExcelWriter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import java.nio.charset.StandardCharsets

object ExcelHelper {
    fun writeRows(headers: List<String>, contents: List<List<String>>): ExcelWriter {
        val rows = listOf(headers, *contents.toTypedArray())
        val writer = ExcelUtil.getWriter(true)
        writer.write(rows, true)
        headers.forEachIndexed { index, _ ->
            val sheet = writer.sheet
            sheet.autoSizeColumn(index)
            sheet.setColumnWidth(index, sheet.getColumnWidth(index) * 15 / 10);
        }

        return writer
    }

    fun flushToResponse(httpServletResponse: HttpServletResponse, writer: ExcelWriter, fileName: String) {
        httpServletResponse.characterEncoding = "UTF-8"
        httpServletResponse.contentType =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
        httpServletResponse.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build().toString()
        )
        httpServletResponse.outputStream.use {
            writer.flush(it)
            writer.close()
        }
    }
}