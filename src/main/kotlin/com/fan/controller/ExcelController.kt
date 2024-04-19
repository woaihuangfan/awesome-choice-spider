package com.fan.controller

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.date.DateUtil
import cn.hutool.poi.excel.ExcelUtil
import cn.hutool.poi.excel.ExcelWriter
import com.fan.client.NoticeDetailClient.getDetailPageUrl
import com.fan.db.entity.NoticeDetailFailLog
import com.fan.db.entity.Result
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.ResultRepository
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFFont
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/excel")
class ExcelController(
    private val resultRepository: ResultRepository,
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository,
) {

    @GetMapping(value = ["/result"])
    fun downloadResult(httpServletResponse: HttpServletResponse) {
        val results = resultRepository.findAll()
        val headers: List<String> =
            CollUtil.newArrayList(
                "公司名称",
                "证券代码",
                "公告日期",
                "会计师事务所名称",
                "合同金额",
                "信息来源",
                "年度"
            )

        val contents: List<List<String>> = results.map {
            listOfNotNull(it.name, it.stock, it.date, it.accountCompanyName, it.amount, decodeTitle(it.title), it.year)
        }
        val writer = writeRows(headers, contents)
        createHyperLink(writer, results)
        flushToResponse(httpServletResponse, writer, "report-(${results.first().year}).xlsx")

    }

    @GetMapping(value = ["/error"])
    fun downloadError(httpServletResponse: HttpServletResponse) {
        val errorLogs = noticeDetailFailLogRepository.findAll()
        val headers: List<String> =
            CollUtil.newArrayList(
                "公告代码",
                "证券代码",
                "公告标题",
                "公告内容",
            )
        val contents: List<List<String>> = errorLogs.map {
            listOfNotNull(it.code, it.stock, it.title, it.content)
        }
        val writer = writeRows(headers, contents)
        createHyperLinkForErrorLog(writer, errorLogs)
        flushToResponse(httpServletResponse, writer, "error-logs-${DateUtil.today()}.xlsx")

    }

    private fun writeRows(
        headers: List<String>,
        contents: List<List<String>>
    ): ExcelWriter {
        val rows: List<List<String>> = listOf(headers, *contents.toTypedArray())
        val writer = ExcelUtil.getWriter(true)
        writer.write(rows, true)
        headers.forEachIndexed { index, _ ->
            writer.sheet.autoSizeColumn(index)
        }
        return writer
    }

    private fun flushToResponse(
        httpServletResponse: HttpServletResponse,
        writer: ExcelWriter,
        fileName: String
    ) {
        httpServletResponse.characterEncoding = "UTF-8";
        httpServletResponse.contentType =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
        httpServletResponse.setHeader(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition
                .attachment().filename(fileName).build().toString()
        )
        httpServletResponse.outputStream.use {
            writer.flush(it)
            writer.close()
        }
    }

    private fun createHyperLink(writer: ExcelWriter, results: MutableList<Result>) {
        val (creationHelper, cellStyle) = getCellStyle(writer)
        results.forEachIndexed { index, result ->
            val linkAddress = result.title.substringAfter("href='").substringBefore("'")
            addHyperLinkToCell(creationHelper, linkAddress, writer, index, cellStyle, 5)
        }
    }

    private fun createHyperLinkForErrorLog(writer: ExcelWriter, errorLogs: MutableList<NoticeDetailFailLog>) {
        val (creationHelper, cellStyle) = getCellStyle(writer)
        errorLogs.forEachIndexed { index, log ->
            val linkAddress = getDetailPageUrl(log.stock, log.code)
            addHyperLinkToCell(creationHelper, linkAddress, writer, index, cellStyle,2)
        }
    }

    private fun getCellStyle(writer: ExcelWriter): Pair<CreationHelper, CellStyle> {
        val workbook = writer.workbook
        val creationHelper = workbook.creationHelper
        val font = getFont(workbook)
        val cellStyle = getCellStyle(workbook, font)
        return Pair(creationHelper, cellStyle)
    }

    private fun addHyperLinkToCell(
        creationHelper: CreationHelper,
        linkAddress: String,
        writer: ExcelWriter,
        index: Int,
        cellStyle: CellStyle,
        cellIndex: Int
    ) {
        val hyperlink = creationHelper.createHyperlink(HyperlinkType.URL)
        hyperlink.address = linkAddress
        val cell = writer.sheet.getRow(index + 1).getCell(cellIndex)
        cell.hyperlink = hyperlink
        cell.cellStyle = cellStyle
    }

    private fun getCellStyle(
        workbook: Workbook,
        font: Font
    ): CellStyle {
        val cellStyle = workbook.createCellStyle()
        cellStyle.setFont(font)
        return cellStyle
    }

    private fun getFont(
        workbook: Workbook,
    ): Font {
        return workbook.createFont().apply {
            this.underline = XSSFFont.U_SINGLE
            this.color = HSSFColor.HSSFColorPredefined.BLUE.index
        }
    }

    private fun decodeTitle(title: String): String {
        return title.substringAfter(">").substringBeforeLast("<")

    }
}