package com.fan.listener

import cn.hutool.core.thread.ThreadUtil
import com.fan.client.NoticeDetailClient.fetchWebDetailSearchedByKeyWord
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

            val detail = fetchWebDetailSearchedByKeyWord(code)
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
                val exist = noticeDetailRepository.findByCode(code)
                exist?.let {
                    noticeDetail.id = exist.id
                }
                noticeDetailRepository.save(noticeDetail)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            noticeDetailFetchFailedLogRepository.save(NoticeDetailFetchFailedLog(code = code))
        }

    }
}