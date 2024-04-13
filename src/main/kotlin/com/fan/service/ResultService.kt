package com.fan.service

import cn.hutool.core.date.DateUtil
import com.fan.client.NoticeDetailClient.getDetailPageUrl
import com.fan.db.entity.Notice
import com.fan.db.entity.NoticeDetail
import com.fan.db.entity.Result
import com.fan.db.repository.ResultRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class ResultService(
    private val resultRepository: ResultRepository
) {

    @Transactional
    fun attachToResult(
        notice: Notice,
        detail: NoticeDetail,
        accountCompanyName: String,
        amount: String
    ) {
        val noticeYear = DateUtil.parseDate(notice.date).year().toString()
        val exist = resultRepository.findByStockAndCode(notice.stock, notice.code)
        val result = Result(
            noticeId = detail.noticeId,
            name = notice.securityFullName,
            stock = detail.stock,
            date = notice.date.substring(
                0, 10
            ),
            accountCompanyName = accountCompanyName,
            amount = amount,
            code = notice.code,
            year = noticeYear,
            title = encodeTitle(notice),
        )
        exist?.let {
            println("========更新分析结果======")
            result.id = exist.id
        }
        resultRepository.save(result)
    }

    fun removeFromResultIfAny(detail: NoticeDetail) {
        resultRepository.findByStockAndCode(detail.stock, detail.code)?.let {
            println("======== 从汇总结果中删除【${detail.stock} - ${detail.title} - ${detail.code}】========")
            resultRepository.delete(it)
        }

    }

    fun removeResult(it: Result) {
        resultRepository.delete(it)
    }

    fun getAllResults(): MutableList<Result> = resultRepository.findAll()

    private fun encodeTitle(notice: Notice) =
        "<a target='_blank' style='color: blue; text-decoration: none; font-weight: bold;' href='%s'>《%s》</a>".format(
            getDetailPageUrl(notice.stock, notice.code), notice.title
        )

    fun findById(id: Long): Optional<Result> {
        return resultRepository.findById(id)
    }

    fun save(result: Result) {
        resultRepository.save(result)
    }
}