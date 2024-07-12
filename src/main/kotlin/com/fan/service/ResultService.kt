package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.controller.WebSocketController.Companion.letPeopleKnow
import com.fan.db.entity.Notice
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.Result
import com.fan.db.repository.ResultRepository
import com.fan.service.RequestContext.Key.getRequestId
import com.fan.util.LinkHelper.addHyperLinkAndReturn
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ResultService(
    private val resultRepository: ResultRepository,
) {
    @Transactional
    fun attachToResult(
        notice: Notice,
        detail: NoticeDetail,
        accountCompanyName: String,
        amount: String,
        context:RequestContext
    ) {
        val noticeYear = DateUtil.parseDate(notice.date).year().toString()
        val exist = resultRepository.findByStockAndCode(notice.stock, notice.code)
        val result =
            Result(
                noticeId = detail.noticeId,
                name = notice.securityFullName,
                stock = detail.stock,
                date =
                    notice.date.substring(
                        0,
                        10,
                    ),
                accountCompanyName = accountCompanyName,
                amount = amount,
                code = notice.code,
                year = noticeYear,
                title = encodeTitle(notice),
                requestId = getRequestId(context),
            )
        exist?.let {
            letPeopleKnow("========更新分析结果======")
            result.id = exist.id
        }
        try {
            resultRepository.save(result)
        } catch (e: Exception) {
            throw e
        }
    }

    fun removeFromResultIfAny(detail: NoticeDetail) {
        resultRepository.findByStockAndCode(detail.stock, detail.code)?.let {
            letPeopleKnow("======== 从汇总结果中删除【${detail.stock} - ${detail.title} - ${detail.code}】========")
            resultRepository.delete(it)
        }
    }

    fun removeResult(it: Result) {
        resultRepository.delete(it)
    }

    fun getAllResults(): MutableList<Result> = resultRepository.findAll()

    private fun encodeTitle(notice: Notice) = addHyperLinkAndReturn(notice.stock, notice.code, notice.title)

    fun findById(id: Long): Optional<Result> = resultRepository.findById(id)

    fun save(result: Result) {
        resultRepository.save(result)
    }
}
