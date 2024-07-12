package com.fan.service

import com.fan.client.NoticeDetailClient.fetchDetailFromRemote
import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.db.entity.Notice
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.NoticeDetailFailLog
import com.fan.db.entity.NoticeDetailFetchFailedLog
import com.fan.db.entity.Result
import com.fan.db.repository.NoticeDetailFetchFailedLogRepository
import com.fan.db.repository.NoticeDetailRepository
import com.fan.filter.DetailFilterChain
import com.fan.service.RequestContext.Key.getRequestId
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.thymeleaf.util.StringUtils
import java.util.Objects

@Service
class NoticeDetailService(
    private val detailFilterChain: DetailFilterChain,
    private val noticeDetailRepository: NoticeDetailRepository,
    private val noticeDetailFetchFailedLogRepository: NoticeDetailFetchFailedLogRepository,
) {
    @Transactional
    fun fetchOrUpdateNoticeDetail(
        notice: Notice,
        context: RequestContext,
    ) {
        if (notice.isDone()) {
            return
        }
        val code = notice.code
        try {
            val exist = noticeDetailRepository.findByStockAndCode(notice.stock, code)
            val noticeTitle = "【${notice.securityFullName} - ${notice.title}】公告详情"
            if (exist == null || StringUtils.isEmpty(exist.content)) {
                letPeopleKnow("======== 开始下载$noticeTitle $code========")
                val detail = fetchDetailFromRemote(code, notice.stock)
                if (Objects.isNull(detail)) {
                    val message = "======== 未找到详情，$noticeTitle $code========"
                    letPeopleKnow(message)
                    throw RuntimeException(message)
                }
                if (notice.stock != detail?.stock) {
                    val message =
                        "======== 证券代码不匹配，期望：${notice.stock},实际：${detail?.stock}，$noticeTitle $code========"
                    letPeopleKnow(message)
                    throw RuntimeException(message)
                }
                if (detailFilterChain.doFilter(detail)) {
                    val noticeDetail =
                        NoticeDetail(
                            code = detail.code,
                            content = detail.content,
                            stock = detail.stock,
                            noticeId = notice.id!!,
                            title = detail.title,
                            requestId = getRequestId(context),
                            context = context,
                        )
                    letPeopleKnow("======== 保存公告详情 ========")
                    noticeDetailRepository.save(noticeDetail)
                }
            } else {
//                noticeDetailRepository.save(exist)
                letPeopleKnow("========$noticeTitle 已存在 ========")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            letPeopleKnow("======== 公告详情下载失败，记录已保存 $code========")
            noticeDetailFetchFailedLogRepository.save(NoticeDetailFetchFailedLog(code = code))
        }
    }

    fun getNoticeDetailByFailLog(record: NoticeDetailFailLog) = noticeDetailRepository.findByStockAndCode(record.stock, record.code)

    fun getNoticeDetailFromResult(it: Result) = noticeDetailRepository.findByStockAndCode(it.stock, it.code)

    fun getAllNoticeDetails(): MutableList<NoticeDetail> = noticeDetailRepository.findAll()
}
