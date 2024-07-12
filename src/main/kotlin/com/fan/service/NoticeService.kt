package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.db.entity.Notice
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.SearchByCodeSource
import com.fan.db.repository.NoticeRepository
import com.fan.enums.SearchType
import com.fan.response.Item
import com.fan.service.RequestContext.Key.getRequestId
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val noticeDetailService: NoticeDetailService,
) {
    @Transactional
    fun saveOrUpdateNotice(
        item: Item,
        context: RequestContext,
    ) {
        val codes = item.codes.first()
        val stock = codes.stock_code
        val code = item.art_code
        val logMessage = "【${item.codes.first().short_name}】${item.title}"
        noticeRepository
            .findByStockAndCode(stock, code)
            ?.let {
                letPeopleKnow("======== $logMessage 在待分析列表中已存在 ========")
                return
            }
        val columnCode = if (item.columns.isNotEmpty()) item.columns.first().column_code else ""
        val notice =
            Notice(
                stock = stock,
                code = code,
                columnCode = columnCode,
                title = item.title,
                date = item.notice_date.substring(0, 10),
                securityFullName = codes.short_name,
                source = SearchType.CODE.typeName,
                requestId = getRequestId(context),
                year = DateUtil.parseDate(item.notice_date).year().toString(),
                context = context,
            )
        noticeRepository.save(notice)
        letPeopleKnow("======== 待分析列表新收录 $logMessage======")
    }

    @Transactional
    fun saveOrUpdateNotice(
        searchByCodeSource: SearchByCodeSource,
        context: RequestContext,
    ) {
        val notice =
            Notice(
                stock = searchByCodeSource.stock,
                code = searchByCodeSource.code,
                columnCode = searchByCodeSource.columnCode,
                title = searchByCodeSource.title,
                date = searchByCodeSource.date,
                securityFullName = searchByCodeSource.companyName,
                source = SearchType.CODE.typeName,
                requestId = getRequestId(context),
                year = DateUtil.parseDate(searchByCodeSource.date).year().toString(),
                context = context,
            )
        val exist = noticeRepository.findByStockAndCode(searchByCodeSource.stock, searchByCodeSource.code)
        exist?.let {
            notice.id = it.id
        }
        noticeRepository.save(notice)
    }

    fun getNoticeFromDetail(detail: NoticeDetail) = noticeRepository.findById(detail.noticeId).get()

    fun getNoticeById(noticeId: Long) = noticeRepository.findById(noticeId)
}
