package com.fan.service

import com.fan.db.entity.Notice
import com.fan.db.entity.SearchByCodeSource
import com.fan.db.repository.NoticeRepository
import com.fan.enums.SearchType
import com.fan.response.Item
import org.springframework.stereotype.Component

@Component
class NoticeService(private val noticeRepository: NoticeRepository) {

    fun saveOrUpdateNotice(noticeItem: Item) {
        val codes = noticeItem.codes.first()
        val stock = codes.stock_code
        val code = noticeItem.art_code
        noticeRepository.findByStockAndCode(stock, code)?.let {
            return
        }
        val columnCode = if (noticeItem.columns.isNotEmpty()) noticeItem.columns.first().column_code else ""
        val notice = Notice(
            stock = stock,
            code = code,
            columnCode = columnCode,
            title = noticeItem.title,
            date = noticeItem.notice_date.substring(0, 10),
            securityFullName = codes.short_name,
            source = SearchType.CODE.typeName
        )
        noticeRepository.save(
            notice
        )
    }

    fun saveOrUpdateNotice(searchByCodeSource: SearchByCodeSource) {
        val notice = Notice(
            stock = searchByCodeSource.stock,
            code = searchByCodeSource.code,
            columnCode = searchByCodeSource.columnCode,
            title = searchByCodeSource.title,
            date = searchByCodeSource.date,
            securityFullName = searchByCodeSource.companyName,
            source = SearchType.CODE.typeName
        )
        val exist = noticeRepository.findByStockAndCode(searchByCodeSource.stock, searchByCodeSource.code)
        exist?.let {
            notice.id = it.id
        }
        noticeRepository.save(notice)
    }
}