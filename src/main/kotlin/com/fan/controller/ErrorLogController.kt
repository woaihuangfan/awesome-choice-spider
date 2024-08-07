package com.fan.controller

import com.fan.po.EditResultParam
import com.fan.service.AnalysisLogService
import com.fan.service.DetailAnalysisErrorLogService
import com.fan.service.NoticeDetailService
import com.fan.service.NoticeService
import com.fan.service.RequestContext
import com.fan.service.ResultService
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/error")
class ErrorLogController(
    private val resultService: ResultService,
    private val detailAnalysisErrorLogService: DetailAnalysisErrorLogService,
    private val noticeService: NoticeService,
    private val noticeDetailService: NoticeDetailService,
    private val analysisLogService: AnalysisLogService,
) {
    @PatchMapping("/{id}")
    fun edit(
        @PathVariable id: Long,
        @RequestBody editResultParam: EditResultParam,
    ): String {
        val accountCompanyName = editResultParam.accountCompanyName
        val ignored = editResultParam.ignored
        if (StringUtils.hasText(accountCompanyName) && !ignored) {
            val errorLogOptional = detailAnalysisErrorLogService.getFailLogById(id)
            errorLogOptional.ifPresent {
                val errorLog = errorLogOptional.get()
                val noticeId = errorLog.noticeId
                val noticeOptional = noticeService.getNoticeById(noticeId)
                noticeOptional.ifPresent {
                    val notice = noticeOptional.get()
                    val noticeDetail = noticeDetailService.getNoticeDetailByFailLog(errorLog)
                    noticeDetail?.let {
                        resultService.attachToResult(
                            notice,
                            noticeDetail,
                            accountCompanyName.orEmpty(),
                            editResultParam.amount.orEmpty(),
                            RequestContext.get(),
                        )
                        detailAnalysisErrorLogService.removeErrorLog(errorLog)
                        analysisLogService.saveAnalysisLog("手动提取修正", RequestContext.get())
                    }
                }
            }
        }

        if (ignored) {
            detailAnalysisErrorLogService.ignoreErrorLog(id)
        }

        return "修改成功"
    }
}
