package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.db.entity.Notice
import com.fan.db.entity.SearchByCodeSource
import com.fan.db.repository.NoticeRepository
import com.fan.enums.SearchType
import com.fan.response.Item
import org.springframework.stereotype.Component

@Component
class NoticeService(private val noticeRepository: NoticeRepository) {

    fun saveOrUpdateNotice(item: Item, requestId: String) {
        val codes = item.codes.first()
        val stock = codes.stock_code
        val code = item.art_code
        val logMessage = "【${item.codes.first().short_name}】${item.title}"
        noticeRepository.findByStockAndCode(stock, code)?.let {
            println("======== $logMessage 在待分析列表中已存在 ========")
            return
        }
        val columnCode = if (item.columns.isNotEmpty()) item.columns.first().column_code else ""
        val notice = Notice(
            stock = stock,
            code = code,
            columnCode = columnCode,
            title = item.title,
            date = item.notice_date.substring(0, 10),
            securityFullName = codes.short_name,
            source = SearchType.CODE.typeName,
            requestId = requestId,
            year = DateUtil.parseDate(item.notice_date).year().toString()
        )
        noticeRepository.save(
            notice
        )
        println("======== 待分析列表新收录 $logMessage======")
    }

    fun saveOrUpdateNotice(searchByCodeSource: SearchByCodeSource, requestId: String) {
        val notice = Notice(
            stock = searchByCodeSource.stock,
            code = searchByCodeSource.code,
            columnCode = searchByCodeSource.columnCode,
            title = searchByCodeSource.title,
            date = searchByCodeSource.date,
            securityFullName = searchByCodeSource.companyName,
            source = SearchType.CODE.typeName,
            requestId = requestId,
            year = DateUtil.parseDate(searchByCodeSource.date).year().toString()
        )
        val exist = noticeRepository.findByStockAndCode(searchByCodeSource.stock, searchByCodeSource.code)
        exist?.let {
            notice.id = it.id
        }
        noticeRepository.save(notice)
    }
}