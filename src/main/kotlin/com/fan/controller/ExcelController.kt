package com.fan.controller

import cn.hutool.core.collection.CollUtil
import cn.hutool.poi.excel.ExcelUtil
import cn.hutool.poi.excel.ExcelWriter
import com.fan.db.entity.Result
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
    private val resultRepository: ResultRepository
) {

    @GetMapping(value = ["/result"])
    fun start(httpServletResponse: HttpServletResponse) {
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

        val rows: List<List<String>> = listOf(headers, *contents.toTypedArray())
        val writer = ExcelUtil.getWriter(true)
        writer.write(rows, true)
        headers.forEachIndexed { index, _ ->
            writer.sheet.autoSizeColumn(index)
        }
        createHyperLink(writer, results)
        flushToResponse(httpServletResponse, results, writer)

    }

    private fun flushToResponse(
        httpServletResponse: HttpServletResponse,
        results: List<Result>,
        writer: ExcelWriter
    ) {
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

    private fun createHyperLink(writer: ExcelWriter, results: MutableList<Result>) {
        val workbook = writer.workbook
        val creationHelper = workbook.creationHelper
        val font = getFont(workbook)
        val cellStyle = getCellStyle(workbook, font)
        results.forEachIndexed { index, result ->
            addHyperLinkToCell(creationHelper, result, writer, index, cellStyle)
        }
    }

    private fun addHyperLinkToCell(
        creationHelper: CreationHelper,
        result: Result,
        writer: ExcelWriter,
        index: Int,
        cellStyle: CellStyle?
    ) {
        val hyperlink = creationHelper.createHyperlink(HyperlinkType.URL)
        hyperlink.address = result.title.substringAfter("href='").substringBefore("'")
        val cell = writer.sheet.getRow(index + 1).getCell(5)
        cell.hyperlink = hyperlink
        cell.cellStyle = cellStyle
    }

    private fun getCellStyle(
        workbook: Workbook,
        font: Font?
    ): CellStyle? {
        val cellStyle = workbook.createCellStyle()
        cellStyle.setFont(font)
        return cellStyle
    }

    private fun getFont(
        workbook: Workbook,
    ): Font? {
        return workbook.createFont().apply {
            this.underline = XSSFFont.U_SINGLE
            this.color = HSSFColor.HSSFColorPredefined.BLUE.index
        }
    }

    private fun decodeTitle(title: String): String {
        return title.substringAfter(">").substringBeforeLast("<")

    }
}