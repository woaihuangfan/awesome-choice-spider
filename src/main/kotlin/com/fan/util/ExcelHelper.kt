package com.fan.util

import cn.hutool.poi.excel.ExcelUtil
import cn.hutool.poi.excel.ExcelWriter
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.ss.usermodel.Sheet
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import java.nio.charset.StandardCharsets

private const val MAX_WIDTH = 255 * 256

object ExcelHelper {
    fun writeRows(
        headers: List<String>,
        contents: List<List<String>>,
    ): ExcelWriter {
        val rows = listOf(headers, *contents.toTypedArray())
        val writer = ExcelUtil.getWriter(true)
        writer.write(rows, true)
        headers.forEachIndexed { index, _ ->
            val sheet = writer.sheet
            sheet.autoSizeColumn(index)
            sheet.setColumnWidth(index, getMaxWidth(sheet, index))
        }

        return writer
    }

    private fun getMaxWidth(
        sheet: Sheet,
        index: Int,
    ): Int {
        val target = sheet.getColumnWidth(index) * 15 / 10
        return if (target > MAX_WIDTH) MAX_WIDTH else target
    }

    fun flushToResponse(
        httpServletResponse: HttpServletResponse,
        writer: ExcelWriter,
        fileName: String,
    ) {
        httpServletResponse.characterEncoding = "UTF-8"
        httpServletResponse.contentType =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
        httpServletResponse.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition
                .attachment()
                .filename(fileName, StandardCharsets.UTF_8)
                .build()
                .toString(),
        )
        httpServletResponse.outputStream.use {
            writer.flush(it)
            writer.close()
        }
    }
}
