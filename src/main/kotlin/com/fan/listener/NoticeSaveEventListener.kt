package com.fan.listener

import cn.hutool.core.thread.ThreadUtil
import com.fan.client.NoticeDetailClient.fetchDetailFromRemote
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.NoticeDetailFetchFailedLog
import com.fan.db.repository.NoticeDetailFetchFailedLogRepository
import com.fan.db.repository.NoticeDetailRepository
import com.fan.event.NoticeSaveEvent
import com.fan.filter.DetailFilterChain
import jakarta.transaction.Transactional
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.Objects

@Component
class NoticeSaveEventListener(
    private val detailFilterChain: DetailFilterChain,
    private val noticeDetailRepository: NoticeDetailRepository,
    private val noticeDetailFetchFailedLogRepository: NoticeDetailFetchFailedLogRepository
) {

    @Transactional
    @EventListener
    fun handleEvent(event: NoticeSaveEvent) {

        val notice = event.notice
        if (notice.isDone()) {
            return
        }
        val code = notice.code
        try {
            ThreadUtil.sleep(100)
            val exist = noticeDetailRepository.findByStockAndCode(notice.stock, code)
            val noticeTitle = "【${notice.securityFullName} - ${notice.title}】公告详情"
            if (exist == null) {
                println("======== 开始下载$noticeTitle ${code}========")
                val detail = fetchDetailFromRemote(code)
                if (Objects.isNull(detail)) {
                    return
                }
                if (detailFilterChain.doFilter(detail)) {
                    val noticeDetail =
                        NoticeDetail(
                            code = detail!!.code,
                            content = detail.content,
                            stock = detail.stock,
                            noticeId = notice.id!!,
                            title = detail.title
                        )
                    println("======== 保存公告详情 ========")
                    noticeDetailRepository.save(noticeDetail)
                }
            } else {
                println("========$noticeTitle 已存在 ========")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            println("======== 公告详情下载失败，记录已保存 ${code}========")
            noticeDetailFetchFailedLogRepository.save(NoticeDetailFetchFailedLog(code = code))
        }

    }
}