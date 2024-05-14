package com.fan.controller

import cn.hutool.core.date.DateUtil
import cn.hutool.poi.excel.ExcelWriter
import com.fan.client.NoticeDetailClient.getDetailPageUrl
import com.fan.db.entity.NoticeDetailFailLog
import com.fan.db.entity.Result
import com.fan.db.repository.CollectLogRepository
import com.fan.db.repository.CompanyRepository
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.ResultRepository
import com.fan.service.TitleRulesService
import com.fan.util.ExcelHelper.flushToResponse
import com.fan.util.ExcelHelper.writeRows
import jakarta.servlet.http.HttpServletResponse
import org.apache.poi.common.usermodel.HyperlinkType
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.CreationHelper
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFFont
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/excel")
class ExcelController(
    private val resultRepository: ResultRepository,
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository,
    private val titleRulesService: TitleRulesService,
    private val collectLogRepository: CollectLogRepository,
    private val companyRepository: CompanyRepository
) {
    companion object {
        private const val COMPANY_NAME = "公司名称"
        private const val STOCK_CODE = "证券代码"
        private const val ANNOUNCE_DATE = "公告日期"
        private const val ACCOUNT_COMPANY_NAME = "会计师事务所名称"
        private const val CONTRACT_AMOUNT = "合同金额"
        private const val INFO_SOURCE = "信息来源"
        private const val ANNOUNCE_CODE = "公告代码"
        private const val ANNOUNCE_TITLE = "公告标题"
        private const val ANNOUNCE_CONTENT = "公告内容"
    }

    @GetMapping("/result")
    fun downloadResult(
        httpServletResponse: HttpServletResponse,
        @RequestParam(required = false) tillDate: String?,
    ) {
        val requestIds = getRequestIds(tillDate)
        val results =
            if (requestIds.isEmpty()) {
                resultRepository.findAll()
            } else {
                resultRepository.findAllByRequestIdIn(requestIds)
            }
        val headers =
            listOf(
                COMPANY_NAME,
                STOCK_CODE,
                ANNOUNCE_DATE,
                ACCOUNT_COMPANY_NAME,
                CONTRACT_AMOUNT,
                INFO_SOURCE,
            )
        val companyMap = getCompanyMap()
        val contents = results.map {
            listOfNotNull(
                it.name, companyMap[it.stock],
                    it.date,
                    it.accountCompanyName,
                    it.amount,
                    decodeTitle(it.title),
                )
            }
        val writer = writeRows(headers, contents)
        createHyperLink(writer, results)
        flushToResponse(httpServletResponse, writer, getResultFileName(tillDate))
    }

    private fun getCompanyMap(): Map<String, String> {
        return companyRepository.findAll().map { it.stock to it.stock + "." + it.block }.toMap()
    }

    private fun getRequestIds(tillDate: String?): List<String> =
        if (tillDate.isNullOrBlank()) {
            emptyList()
        } else {
            collectLogRepository
                .findAllByTillDateIs(tillDate)
                .map { it.requestId }
                .filter { it.isNotBlank() }
        }

    private fun getResultFileName(tillDate: String?): String {
        val date = if (tillDate.isNullOrBlank()) collectLogRepository.findTop1ByOrderByIdDesc().tillDate else tillDate
        return "结果汇总(${titleRulesService.getCurrentRules()})_${
            formatDate(date)
        }_${formatDate(DateUtil.today())}.xlsx"
    }

    private fun formatDate(date: String): String? =
        DateUtil.format(
            DateUtil.parseDate(date),
            "yyyyMMdd",
        )

    @GetMapping("/error")
    fun downloadError(httpServletResponse: HttpServletResponse) {
        val errorLogs = noticeDetailFailLogRepository.findAll()
        val headers = listOf(ANNOUNCE_CODE, STOCK_CODE, ANNOUNCE_TITLE, ANNOUNCE_CONTENT)
        val contents = errorLogs.map { listOfNotNull(it.code, it.stock, it.title, it.content) }
        val writer = writeRows(headers, contents)
        createHyperLinkForErrorLog(writer, errorLogs)
        flushToResponse(httpServletResponse, writer, getErrorsFileName())
    }

    private fun getErrorsFileName() = "错误记录(关键字：${titleRulesService.getCurrentRules()})_${DateUtil.today()}.xlsx"

    private fun createHyperLink(
        writer: ExcelWriter,
        results: List<Result>,
    ) {
        val (creationHelper, cellStyle) = getCreationHelperAndCellStyle(writer)
        results.forEachIndexed { index, result ->
            val linkAddress = result.title.substringAfter("href='").substringBefore("'")
            addHyperLinkToCell(creationHelper, linkAddress, writer, index, cellStyle, 5)
        }
    }

    private fun createHyperLinkForErrorLog(
        writer: ExcelWriter,
        errorLogs: List<NoticeDetailFailLog>,
    ) {
        val (creationHelper, cellStyle) = getCreationHelperAndCellStyle(writer)
        errorLogs.forEachIndexed { index, log ->
            val linkAddress = getDetailPageUrl(log.stock, log.code)
            addHyperLinkToCell(creationHelper, linkAddress, writer, index, cellStyle, 2)
        }
    }

    private fun getCreationHelperAndCellStyle(writer: ExcelWriter): Pair<CreationHelper, CellStyle> {
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
        cellIndex: Int,
    ) {
        val hyperlink = creationHelper.createHyperlink(HyperlinkType.URL)
        hyperlink.address = linkAddress
        val cell = writer.sheet.getRow(index + 1).getCell(cellIndex)
        cell.hyperlink = hyperlink
        cell.cellStyle = cellStyle
    }

    private fun getCellStyle(
        workbook: Workbook,
        font: Font,
    ): CellStyle {
        val cellStyle = workbook.createCellStyle()
        cellStyle.setFont(font)
        cellStyle.borderTop = BorderStyle.THIN
        cellStyle.borderBottom = BorderStyle.THIN
        cellStyle.borderLeft = BorderStyle.THIN
        cellStyle.borderRight = BorderStyle.THIN
        cellStyle.alignment = HorizontalAlignment.CENTER
        cellStyle.verticalAlignment = VerticalAlignment.CENTER
        return cellStyle
    }

    private fun getFont(workbook: Workbook): Font =
        workbook.createFont().apply {
            underline = XSSFFont.U_SINGLE
            color = HSSFColor.HSSFColorPredefined.BLUE.index
        }

    private fun decodeTitle(title: String): String = title.substringAfter(">").substringBeforeLast("<")
}
