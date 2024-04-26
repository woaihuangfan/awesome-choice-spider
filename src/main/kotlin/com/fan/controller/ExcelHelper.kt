package com.fan.controller

import cn.hutool.poi.excel.ExcelUtil
import cn.hutool.poi.excel.ExcelWriter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders

object ExcelHelper {
    fun writeRows(headers: List<String>, contents: List<List<String>>): ExcelWriter {
        val rows = listOf(headers, *contents.toTypedArray())
        val writer = ExcelUtil.getWriter(true)
        writer.write(rows, true)
        headers.forEachIndexed { index, _ -> writer.sheet.autoSizeColumn(index) }
        return writer
    }

    fun flushToResponse(httpServletResponse: HttpServletResponse, writer: ExcelWriter, fileName: String) {
        httpServletResponse.characterEncoding = "UTF-8"
        httpServletResponse.contentType =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
        httpServletResponse.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment().filename(fileName).build().toString()
        )
        httpServletResponse.outputStream.use {
            writer.flush(it)
            writer.close()
        }
    }
}