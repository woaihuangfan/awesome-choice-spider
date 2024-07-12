package com.fan.controller

import com.fan.service.ContentAnalysisService
import com.fan.service.RequestContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin")
class AdminController(
    private val contentAnalysisService: ContentAnalysisService,
) {
    @GetMapping(value = ["/reAnalysisErrors"])
    fun reAnalysisErrors(): String {
        contentAnalysisService.reAnalysisErrors(RequestContext.get())
        return "success"
    }

    @GetMapping(value = ["/reAnalysisAll"])
    fun reAnalysisAll(): String {
        contentAnalysisService.reAnalysisAll(RequestContext.get())
        return "任务启动成功，请稍后查询"
    }

    @GetMapping(value = ["/reAnalysisDetail"])
    fun reAnalysisDetail(): String {
        contentAnalysisService.reAnalysisDetail(RequestContext.get())
        return "操作成功，请留意汇总查询页面和错误分析页面数据变化！"
    }
}
