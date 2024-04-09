package com.fan.listener

import cn.hutool.core.thread.ThreadUtil
import com.fan.db.entity.NoticeDetailFailLog
import com.fan.db.entity.Result
import com.fan.db.repository.NoticeDetailFailLogRepository
import com.fan.db.repository.NoticeRepository
import com.fan.db.repository.ResultRepository
import com.fan.event.NoticeDetailSaveEvent
import com.fan.extractor.AccountCompanyExtractor
import jakarta.transaction.Transactional
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class NoticeDetailSaveEventListener(
    private val resultRepository: ResultRepository,
    private val noticeRepository: NoticeRepository,
    private val noticeDetailFailLogRepository: NoticeDetailFailLogRepository
) {

    @Transactional
    @EventListener
    fun handleEvent(event: NoticeDetailSaveEvent) {
        try {
            ThreadUtil.sleep(1000)
            val detail = event.noticeDetail
            val code = detail.code
            val notice = noticeRepository.findById(detail.noticeId).get()
            val accountCompanyName = AccountCompanyExtractor.extractAccountCompanyName(detail.content, code)
            if (!accountCompanyName.contains(code)) {
                val result = Result(
                    noticeId = detail.noticeId,
                    name = notice.securityFullName,
                    stock = detail.stock,
                    date = notice.date.substring(
                        0,
                        10
                    ),
                    accountCompanyName = accountCompanyName,
                    code = code
                )
                resultRepository.save(result)
                println(result.toString())
            } else {
                noticeDetailFailLogRepository.save(
                    NoticeDetailFailLog(
                        code = detail.code,
                        content = detail.content,
                        stock = detail.stock
                    )
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }


    }
}